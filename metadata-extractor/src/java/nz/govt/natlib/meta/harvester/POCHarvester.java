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

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.fx.ParserListener;
import nz.govt.natlib.meta.DoNothingTransformer;
import nz.govt.natlib.meta.PropertySource;
import nz.govt.natlib.meta.TransformProcessor;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Configuration;

/**
 * Extracts metadata from the files into the native XML format. In general,
 * the NLNZHarvester is used, which converts the native XML output into a 
 * standard metadata format. However, this class can be useful to get the 
 * raw information out of the file.
 * 
 * @author unascribed
 * @version 1.0
 */

public class POCHarvester extends DefaultHarvester {

	public POCHarvester() {
	}

	public String getOutputType() {
		return null;
	}

	public void harvestFile(Configuration config, File file,
			PropertySource props) throws IOException {
		OutputStream out = null;
		try {
			DataAdapter adapter = AdapterFactory.getInstance().getAdapter(file);
			
			File f = new File(config.getOutputDirectory() + "/"
					+ file.getName() + ".xml");
			out = new FileOutputStream(f);

			String outDTD = config.getOutputDTD();
			
			// This loads the DoNohtingTransformer.
			TransformProcessor transformer = new DoNothingTransformer(); 
				
				//TransformProcessor.getInstance(
				//	inDTD, outDTD);
			

			ByteArrayOutputStream bout = new ByteArrayOutputStream(2048);
			ParserContext handler = new ParserContext();
			ParserListener listener = new DTDXmlParserListener(bout,
					outDTD == null ? null : Config.getInstance()
							.getXMLBaseURL()
							+ "/" + outDTD);
			handler.addListener(listener);
			adapter.adapt(file, handler);

			transformer.transform(new ByteArrayInputStream(bout.toByteArray()),
					out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public void abortHarvest(Configuration config) {
	}

}