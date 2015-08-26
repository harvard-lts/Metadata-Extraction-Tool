/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package nz.govt.natlib.adapter.arc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

import org.apache.commons.io.FilenameUtils;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;
import org.jwat.arc.ArcVersionBlock;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * ArcAdapter is responsible for extracting metadata out of Internet Archive "ARC" files.
 * 
 * For specification of arc files, see the URL: http://www.archive.org/web/researcher/ArcFileFormat.php
 * 
 * This class makes a heavy use of the Heritrix library available from Internet Archive website at: http://crawler.archive.org/
 * 
 * @author Raghu Pushpakath
 * @version 1.0
 */
public class ArcAdapter extends DataAdapter {

	private class ArcMetadata {
		private String software;
		private String hostname;
		private String ip;
		private String operator;
		private String createdDate;
		private String robotPolicy;
		private String arcFormat;
		private String conformsTo;
	}

	public ArcAdapter() {
	}

	public boolean acceptsFile(File file) {
		boolean arc = false;
		ArcReader arcReader = null;
		try {
			arcReader = getArcReader(file);
			String fileExt = FilenameUtils.getExtension(file.getName());
			arc = (arcReader != null && arcReader.isCompliant() && fileExt.equals("arc"));
			return arc;
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"IO Exception determining ARC file type");
		} finally {
			if (arcReader != null)
				arcReader.close();
		}
		return arc;
	}

	public String getOutputType() {
		return "arc.dtd";
	}

	public String getInputType() {
		return "x-application/arc";
	}

	public String getName() {
		return "Internet Archive ARC File Adapter";
	}

	public String getDescription() {
		return "Adapts Internet archive ARC files (both compressed and uncompressed)";
	}

	public String getVersion() {
		return "1.0";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		ArcReader arcReader = null;
		try {
			// Get the reader (either compressed or uncompressed)
			arcReader = getArcReader(file);
			// Get an iterator over the arc records
			Iterator<ArcRecordBase> iter = arcReader.iterator();
			// Reference to the first record which is the "archive metadata record"
			ArcMetadata arcMetadata = null;
			// Map to hold the mime type statistics
			HashMap<String, Integer> mimeMap = new HashMap();
			// Iterate over the arc records
			while (iter != null && iter.hasNext()) {
				ArcRecordBase record = iter.next();
				// First record is the Archive Metadata record. Get hold of its reference
				if (record instanceof ArcVersionBlock) {
					ArcVersionBlock arcMetadataRecord = (ArcVersionBlock)record;
					// Extract the metadata from the XML data that this arc record holds
					arcMetadata = parseArcMetadataRecord(arcMetadataRecord);
				}
				addMimeTypeToMimeMap(record, mimeMap);
				record.close();
			}
			ctx.fireStartParseEvent("ARC");
			writeFileInfo(file, ctx);

			// Write the <ARCMETADATA> element
			if (arcMetadata != null) {
				ctx.fireStartParseEvent("ARCMETADATA");
				ctx.fireParseEvent("SOFTWARE", arcMetadata.software);
				ctx.fireParseEvent("HOSTNAME", arcMetadata.hostname);
				ctx.fireParseEvent("IP", arcMetadata.ip);
				ctx.fireParseEvent("OPERATOR", arcMetadata.operator);
				ctx.fireParseEvent("CREATEDDATE", arcMetadata.createdDate);
				ctx.fireParseEvent("ROBOTPOLICY", arcMetadata.robotPolicy);
				ctx.fireParseEvent("ARCFORMAT", arcMetadata.arcFormat);
				ctx.fireParseEvent("CONFORMSTO", arcMetadata.conformsTo);
				ctx.fireEndParseEvent("ARCMETADATA");
			}

			// Write the <ARCINFO> element
			ctx.fireStartParseEvent("ARCINFO");
			ctx.fireParseEvent("COMPRESSED", arcReader.isCompressed());

			ctx.fireStartParseEvent("CONTENTSUMMARY");

			if (mimeMap.size() > 0) {
				Set<String> keys = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				keys.addAll(mimeMap.keySet());
				Iterator<String> keyIterator = keys.iterator();
				StringBuffer mimeSummary = new StringBuffer();
				boolean first = true;
				while (keyIterator != null && keyIterator.hasNext()) {
					String mimetype = (String) keyIterator.next();
					if (first == false) {
						mimeSummary.append(", ");
					}
					first = false;
					mimeSummary.append(mimetype).append(":").append(mimeMap.get(mimetype));
				}
				ctx.fireParseEvent("MIMEREPORT", mimeSummary.toString());
			}
			ctx.fireEndParseEvent("CONTENTSUMMARY");
			ctx.fireEndParseEvent("ARCINFO");
			ctx.fireEndParseEvent("ARC");
		} catch (Throwable ex) {
			System.out.println("Exception: " + ex);
			ex.printStackTrace();
		} finally {
			if (arcReader != null)
				arcReader.close();
		}
	}

	private ArcMetadata parseArcMetadataRecord(ArcVersionBlock arcMetadataRecord) {
		if (arcMetadataRecord == null)
			return null;
		ArcMetadata metadata = new ArcMetadata();
		ByteArrayOutputStream bos = null;
		try {
			Document doc = createXmlDocumentFrom(arcMetadataRecord.getPayloadContent());
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			xpath.setNamespaceContext(new ArcMetadataNamespaceContext());
			metadata.software = (String) xpath.evaluate("//arc:arcmetadata/arc:software", doc, XPathConstants.STRING);
			metadata.hostname = (String) xpath.evaluate("//arc:arcmetadata/arc:hostname", doc, XPathConstants.STRING);
			metadata.ip = (String) xpath.evaluate("//arc:arcmetadata/arc:ip", doc, XPathConstants.STRING);
			metadata.operator = (String) xpath.evaluate("//arc:arcmetadata/arc:operator", doc, XPathConstants.STRING);
			metadata.createdDate = (String) xpath.evaluate("//arc:arcmetadata/dc:date", doc, XPathConstants.STRING);
			metadata.robotPolicy = (String) xpath.evaluate("//arc:arcmetadata/arc:robots", doc, XPathConstants.STRING);
			metadata.arcFormat = (String) xpath.evaluate("//arc:arcmetadata/dc:format", doc, XPathConstants.STRING);
			metadata.conformsTo = (String) xpath.evaluate("//arc:arcmetadata/dcterms:conformsTo", doc, XPathConstants.STRING);
			return metadata;
		} catch (Exception e) {
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	private void addMimeTypeToMimeMap(ArcRecordBase record, HashMap<String, Integer> mimeMap) {
		String mime = record.getContentTypeStr();
		addMimeTypeToMimeMap(mime, mimeMap);
	}

	private void addMimeTypeToMimeMap(String mime, HashMap<String, Integer> mimeMap) {
		if (mime == null)
			return;
		Integer counter = mimeMap.get(mime);
		int count = 0;
		if (counter != null) {
			count = counter.intValue();
		}
		count++;
		mimeMap.put(mime, new Integer(count));
	}

	private Document createXmlDocumentFrom(InputStream arcMetadataStream) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			return builder.parse(arcMetadataStream);
		} catch (ParserConfigurationException pce) {
			throw new RuntimeException(pce);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} catch (SAXException se) {
			throw new RuntimeException(se);
		}
	}

	private ArcReader getArcReader(File file) {
		try {
			return getArcReader(file, false);
		} catch (Exception ex) {
			try {
				return getArcReader(file, true);
			} catch (Exception e) {
				return null;
			}
		}
	}

	private ArcReader getArcReader(File file, boolean compressed) {
		boolean isError = false;
		ArcReader arcReader = null;
		try {
			arcReader = ArcReaderFactory.getReader(new FileInputStream(file));
			if (arcReader.isCompliant()) {
				return arcReader;
			} else {
				isError = true;
				throw new RuntimeException("ArcReader is invalid");
			}
		} catch (Exception ex) {
			isError = true;
			throw new RuntimeException("ArcReader is invalid", ex);
		} finally {
			if (isError == true) {
				try {
					if (arcReader != null)
						arcReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class ArcMetadataNamespaceContext implements NamespaceContext {

		public String getNamespaceURI(String prefix) {
			if (prefix == null)
				throw new NullPointerException("Null prefix");
			else if ("dc".equals(prefix))
				return "http://purl.org/dc/elements/1.1/";
			else if ("dcterms".equals(prefix))
				return "http://purl.org/dc/terms/";
			else if ("arc".equals(prefix))
				return "http://archive.org/arc/1.0/";
			else if ("xml".equals(prefix))
				return XMLConstants.XML_NS_URI;
			return XMLConstants.NULL_NS_URI;
		}

		// This method isn't necessary for XPath processing.
		public String getPrefix(String uri) {
			throw new UnsupportedOperationException();
		}

		// This method isn't necessary for XPath processing either.
		public Iterator getPrefixes(String uri) {
			throw new UnsupportedOperationException();
		}

	}
}