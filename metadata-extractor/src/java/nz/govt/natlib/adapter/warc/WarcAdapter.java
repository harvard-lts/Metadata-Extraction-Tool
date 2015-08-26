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

package nz.govt.natlib.adapter.warc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

import org.apache.commons.io.FilenameUtils;
import org.jwat.common.HttpHeader;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

/**
 * WarcAdapter is responsible for extracting metadata out of Internet Archive "WARC" files.
 * 
 * <p>
 * For official specification of WARC files, see the URLs: <br>
 * http://www.iso.org/iso/iso_catalogue/catalogue_tc/catalogue_detail.htm?csnumber=44717 <br>
 * http://bibnum.bnf.fr/WARC/warc_ISO_DIS_28500.pdf
 * 
 * <p>
 * Additional resources for WARC format can be found at: <br>
 * http://www.digitalpreservation.gov/formats/fdd/fdd000236.shtml <br>
 * http://archive-access.sourceforge.net/warc/ <br>
 * http://archive-access.sourceforge.net/warc/warc_file_format-0.9.html
 * 
 * <p>
 * This class makes a heavy use of the Heritrix library available from Internet Archive website at: <br>
 * http://crawler.archive.org/
 * 
 * @author Raghu Pushpakath
 * @author Chris Mclean
 * @version 1.0
 */
public class WarcAdapter extends DataAdapter {

	private static final String SOFTWARE = "software";
	private static final String HOSTNAME = "hostname";
	private static final String IP = "ip";
	private static final String OPERATOR = "operator";
	private static final String CREATEDDATE = "created";
	private static final String ROBOTPOLICY = "robots";
	private static final String WARCFORMAT = "format";
	private static final String CONFORMSTO = "conformsTo";

	private static final Set<String> WARC_TYPES_OF_INTEREST = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

	private class WarcMetadata {
		private String software;
		private String hostname;
		private String ip;
		private String operator;
		private String createdDate;
		private String robotPolicy;
		private String warcFormat;
		private String conformsTo;
		public String warcDate;
	}

	static {
		WARC_TYPES_OF_INTEREST.add("response");
		WARC_TYPES_OF_INTEREST.add("resource");
		WARC_TYPES_OF_INTEREST.add("continuation");
		WARC_TYPES_OF_INTEREST.add("revisit");
	}

	public WarcAdapter() {
	}

	public boolean acceptsFile(File file) {
		boolean warc = false;
		WarcReader warcReader = null;
		try {
			warcReader = getWarcReader(file);
			String fileExt = FilenameUtils.getExtension(file.getName());
			warc = (warcReader != null && fileExt.equals("warc"));
			return warc;
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"IO Exception determining WARC file type");
		} finally {
			if (warcReader != null) {
				warcReader.close();
			}
		}
		return warc;
	}

	public String getOutputType() {
		return "warc.dtd";
	}

	public String getInputType() {
		return "application/warc";
	}

	public String getName() {
		return "Internet Archive WARC File Adapter";
	}

	public String getDescription() {
		return "Adapts Internet archive WARC files (both compressed and uncompressed)";
	}

	public String getVersion() {
		return "1.0";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		WarcReader warcReader = null;
		try {
			// Get the reader (either compressed or uncompressed)
			warcReader = getWarcReader(file);
			// Get an iterator over the warc records
			Iterator<WarcRecord> iter = warcReader.iterator();
			// Reference to the first record which is the "archive metadata record"
			WarcMetadata warcMetadata = null;
			// Map to hold the mime type statistics
			HashMap<String, Integer> mimeMap = new HashMap<String, Integer>();
			HashMap<String, Integer> warcTypeMap = new HashMap<String, Integer>();
			// Iterate over the warc records
			try {
				while (iter != null && iter.hasNext()) {
					WarcRecord record = iter.next();
					/*
					 * Get required information from the retrieved WARC record
					 */
					String warcType = getWarcType(record);
					addContentToMap(warcType, warcTypeMap);
					/*
					 * Extract the warc metadata from the warcinfo record. Extract the mime type info from "response" and other
					 * specific types of warc records.
					 */
					if ("warcinfo".equals(warcType) && warcMetadata == null) {
						// Extract the metadata from this warc record
						warcMetadata = parseWarcMetadataRecord(record);
					} else if (WARC_TYPES_OF_INTEREST.contains(warcType)) {
						// Add logic later if we ever need to get metadata out of other
						// WARC types.
					}
					addMimeTypeToMimeMap(record, mimeMap);
					record.close();
				}
			} catch (Exception ex) {
				System.out.println("Exception while iterating through WARC records: " + ex);
				ex.printStackTrace();
			}

			ctx.fireStartParseEvent("WARC");
			writeFileInfo(file, ctx);

			// Write the <WARCMETADATA> element
			if (warcMetadata != null) {
				ctx.fireStartParseEvent("WARCMETADATA");
				ctx.fireParseEvent("SOFTWARE", warcMetadata.software);
				ctx.fireParseEvent("HOSTNAME", warcMetadata.hostname);
				ctx.fireParseEvent("IP", warcMetadata.ip);
				ctx.fireParseEvent("OPERATOR", warcMetadata.operator);
				ctx.fireParseEvent("CREATEDDATE", warcMetadata.createdDate);
				ctx.fireParseEvent("ROBOTPOLICY", warcMetadata.robotPolicy);
				ctx.fireParseEvent("WARCFORMAT", warcMetadata.warcFormat);
				ctx.fireParseEvent("CONFORMSTO", warcMetadata.conformsTo);
				ctx.fireParseEvent("WARCDATE", warcMetadata.warcDate);
				ctx.fireEndParseEvent("WARCMETADATA");
			}

			// Write the <WARCINFO> element
			ctx.fireStartParseEvent("WARCINFO");
			ctx.fireParseEvent("COMPRESSED", warcReader.isCompressed());

			ctx.fireStartParseEvent("CONTENTSUMMARY");

			if (warcTypeMap.size() > 0) {
				Set<String> keys = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				keys.addAll(warcTypeMap.keySet());
				Iterator<String> keyIterator = keys.iterator();
				while (keyIterator != null && keyIterator.hasNext()) {
					String warctype = keyIterator.next();
					ctx.fireStartParseEvent("WARCTYPEREPORT");
					ctx.fireParseEvent("WARCTYPE", warctype);
					ctx.fireParseEvent("COUNT", warcTypeMap.get(warctype));
					ctx.fireEndParseEvent("WARCTYPEREPORT");
				}
			}
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
			ctx.fireEndParseEvent("WARCINFO");
			ctx.fireEndParseEvent("WARC");
		} catch (Throwable ex) {
			System.out.println("Exception: " + ex);
			ex.printStackTrace();
		} finally {
			if (warcReader != null)
				warcReader.close();
		}
	}

	private WarcMetadata parseWarcMetadataRecord(WarcRecord record) {
		if (record == null)
			return null;
		WarcMetadata metadata = new WarcMetadata();
		metadata.warcDate = record.getHeader("WARC-Date").value;
		ByteArrayOutputStream bos = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(record.getPayloadContent()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				metadata.software = getIfMatches(line, SOFTWARE, metadata.software);
				metadata.hostname = getIfMatches(line, HOSTNAME, metadata.hostname);
				metadata.ip = getIfMatches(line, IP, metadata.ip);
				metadata.operator = getIfMatches(line, OPERATOR, metadata.operator);
				metadata.createdDate = getIfMatches(line, CREATEDDATE, metadata.createdDate);
				metadata.robotPolicy = getIfMatches(line, ROBOTPOLICY, metadata.robotPolicy);
				metadata.warcFormat = getIfMatches(line, WARCFORMAT, metadata.warcFormat);
				metadata.conformsTo = getIfMatches(line, CONFORMSTO, metadata.conformsTo);
			}
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

	private String getIfMatches(String line, String lineToLookFor, String oldValue) {
		if (oldValue != null)
			return oldValue;
		String token = lineToLookFor + ": ";
		if (line.startsWith(token)) {
			return line.replaceFirst(token, "");
		}
		return null;
	}

	private void addMimeTypeToMimeMap(WarcRecord record, HashMap<String, Integer> mimeMap) {
		HttpHeader httpHeader = record.getHttpHeader();
		String mime = httpHeader!=null?httpHeader.contentType:null;
		if (mime == null) {
			mime = "not recorded";
		}
		addContentToMap(mime, mimeMap);
	}

	private String getWarcType(WarcRecord record) {
		return record.getHeader("WARC-Type").value;
	}

	private void addContentToMap(String content, HashMap<String, Integer> contentMap) {
		if (content == null)
			return;
		content = content.trim();
		Integer counter = contentMap.get(content);
		int count = 0;
		if (counter != null) {
			count = counter.intValue();
		}
		count++;
		contentMap.put(content, new Integer(count));
	}

	private WarcReader getWarcReader(File file) {
		try {
			return getWarcReader(file, false);
		} catch (Exception ex) {
			try {
				return getWarcReader(file, true);
			} catch (Exception e) {
				return null;
			}
		}
	}

	private WarcReader getWarcReader(File file, boolean compressed) {
		boolean isError = false;
		WarcReader warcReader = null;
		try {
			warcReader = WarcReaderFactory.getReader(new FileInputStream(file));
			if (warcReader.isCompliant()) {
				return warcReader;
			} else {
				isError = true;
				throw new RuntimeException("WarcReader is invalid");
			}
		} catch (Exception ex) {
			isError = true;
			throw new RuntimeException("WarcReader is invalid", ex);
		} finally {
			if (isError == true) {
				try {
					if (warcReader != null)
						warcReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}