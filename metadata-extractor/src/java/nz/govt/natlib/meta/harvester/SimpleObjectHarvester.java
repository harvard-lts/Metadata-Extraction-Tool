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

package nz.govt.natlib.meta.harvester;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.fx.ParserListener;
import nz.govt.natlib.fx.XMLParserListener;
import nz.govt.natlib.meta.Harvester;
import nz.govt.natlib.meta.PropertySource;
import nz.govt.natlib.meta.TransformProcessor;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;
import nz.govt.natlib.xsl.XSLTFunctions;

/**
 * The SimpleObjectHarvester creates separate metadata output files for each
 * member in a Simple Object. To create a single output file for multiple
 * input files, use the ComplexObjectHarvester.
 * 
 * 
 * 
 * @author unascribed
 * @version 1.0
 */
public class SimpleObjectHarvester extends DefaultHarvester {

	private ByteArrayOutputStream byteOut;

	// use a proper XML formatter to output into the stream.
	private XMLListener formatter;

	// overall output
	protected OutputStream out;

	private int fileCount = 0;
	
	private Harvester parentHarvester = null;
	
	/**
	 * Create a new Harvester object.
	 * @param parentHarvester The parent Harvester. This is used to determine
	 *                        the desired output format.
	 */
	public SimpleObjectHarvester(Harvester parentHarvester) {
		this.parentHarvester = parentHarvester;
	}

	protected void startOutputFile(PropertySource props, OutputStream file)
			throws IOException {
		byteOut = new ByteArrayOutputStream(4096);
		formatter = new XMLListener(byteOut, null);
		fileCount = 0;
		// start outputting simple object stuff
		startTag("Object");
		writeObjectDetails(props);
		startTag("Files");
	}

	protected void startHarvestFile(Configuration config, File file,
			PropertySource props) throws IOException {
		// open the output file.
		File f = new File(config.getOutputDirectory() + "/" + file.getName()
				+ ".xml");
		out = new FileOutputStream(f);

		startOutputFile(props, out);
	}

	protected void endHarvestFile(Configuration config, File file,
			PropertySource props) throws IOException {
		endOutputFile(props, out);
		out.close();
		out = null;
		System.gc();
	}

	protected void harvestFile(Configuration config, File file,
			PropertySource props) throws IOException {
		
		// get the adapter...
		DataAdapter adapter = AdapterFactory.getInstance().getAdapter(file);

		// adapt each file...
		String outDTD = adapter.getOutputType();
		ByteArrayOutputStream bout = new ByteArrayOutputStream(2048);
		ParserContext handler = new ParserContext();

		// fill in some details - to be included in the output...
		handler.setAttribute(ParserContext.FILE_INDEX, fileCount++);
		handler.setAttribute(ParserContext.OBJECT, props
				.getProperty("ID", null));
		handler.setAttribute(ParserContext.PROCESS, props.getProperty("Type",
				null));

		// Set up the handler.
		ParserListener listener = new DTDXmlParserListener(bout, outDTD == null ? null
				: Config.getInstance().getXMLBaseURL() + "/" + outDTD);
		handler.addListener(listener);

		// Adapt the file.
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER, "Starting Adapter");
		adapter.adapt(file, handler);
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER, "Finished adapting");

		// Get the transformer that is configured to map from the native format
		// to the "nlnz_presmet.xsd" format. Note that because the
		// nlnz_presmet.xsd files have a preamble, and may contain metadata for
		// multiple files within a single output file (for the
		// ComplexObjectHarvester) the transformation does not
		// create a complete XML file, just a <File>...</File> section.
		// Different Harvester implementations may use the transformer mapping
		// to create complete XML documents; it's up to the Harvester what it
		// expects the transformation to achieve.
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER, "Starting Transformation");
		TransformProcessor transformer = TransformProcessor.getInstance(adapter
				.getOutputType(), getOutputType());
		
		// Transform the document into the target format.
		LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER, "Finished Transformation");
		transformer.transform(new ByteArrayInputStream(bout.toByteArray()),
				byteOut);
		bout.close();
	}

	protected void endOutputFile(PropertySource props, OutputStream file)
			throws IOException {

		endTag("Files");
		endTag("Object");

		// then transform overall - if required...
		// TransformProcessor transformer =
		// TransformProcessor.getInstance("object.dtd","nlnz_presmet.xsd");
		TransformProcessor transformer = TransformProcessor.getInstance(null);
		transformer.transform(new ByteArrayInputStream(byteOut.toByteArray()),
				file);

		// closeup...
		byteOut.close();
		formatter = null;
		byteOut.close();

	}

	public String getOutputType() {
		return parentHarvester.getOutputType();
		//"nlnz_presmet.xsd";
	}

	private void writeObjectDetails(PropertySource props) {
		XSLTFunctions func = new XSLTFunctions();
		try {
			writeTag("Name", props.getProperty("Name", null));
			writeTag("ID", props.getProperty("ID", null));
			writeTag("ReferenceNumber", props.getProperty("Reference Number",
					null));
			writeTag("GroupIdentifier", props.getProperty("Group ID", null));
			writeTag("PersistentIdentifier", props.getProperty(
					"Persistent Identifier", null));
			FXUtil.writeDateTag(formatter, "MasterCreationDate", new Date());
			writeTag("ObjectComposition", props.getProperty("type", null));
			// writeTag("IsPartOfGroup",props.getProperty("Part of
			// Group",null));
			startTag("StructuralType");
			writeTag("Name", "");
			writeTag("Extension", "");
			endTag("StructuralType");
			writeTag("HardwareEnvironment", func.getHardwareEnvironment());
			writeTag("SoftwareEnvironment", func.getSoftwareEnvironment());
			writeTag("InstallationRequirements", props.getProperty(
					"Installation Requirements", null));
			writeTag("AccessInhibitors", props.getProperty("Access Inhibitors",
					null));
			writeTag("AccessFacilitators", props.getProperty(
					"Access Facilitators", null));
			writeTag("Quirks", props.getProperty("Quirks", null));
			// writeTag("DataAuthentication",func.getCurrentUserAccountName());
			writeTag("MetadataRecordCreator", props.getProperty("user", null));
			FXUtil.writeDateTag(formatter, "MetadataCreationDate", new Date());
			writeTag("Comments", props.getProperty("Comments", null));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}

	protected void startTag(String tag, String attr, String value)
			throws IOException {
		formatter.writeTagOpen(tag, new String[] { attr },
				new String[] { value });
	}

	protected void startTag(String tag) throws IOException {
		formatter.writeTagOpen(tag);
	}

	protected void endTag(String tag) throws IOException {
		formatter.writeTagClose(tag);
	}

	protected void writeTagContents(Object value) throws IOException {
		formatter.writeTagContents(value);
	}

	protected void writeTag(String tag, Object data) throws IOException {
		formatter.writeTag(tag, data);
	}

	private class XMLListener extends XMLParserListener {

		private String dtd;

		public XMLListener(OutputStream out, String dtd) {
			super(out);
			this.dtd = dtd;
		}

		protected void writeExtraTags() {
		}

		public String getIndent(int indent) {
			return "";
		}

		protected String getEOL() {
			return "";
		}

		public String getDTD() {
			return this.dtd;
		}
	}

}