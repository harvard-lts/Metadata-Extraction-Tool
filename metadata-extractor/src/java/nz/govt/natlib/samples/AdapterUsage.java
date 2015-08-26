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
package nz.govt.natlib.samples;

import java.io.File;
import java.io.IOException;

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.config.Config;

/**
 * Sample class illustrating how to use Adapters directly.
 * @author bbeaumont
 */
public class AdapterUsage {

	public static void main(String[] args) {
		File f = new File(args[0]);

		// Create a ParserContext to listen for Metadata 
		// events. We are using a custom listener to extend the 
		// behaviour of the standard ones.
		ParserContext ctx = new ParserContext();
		StringBufferXMLParserListener listener = new StringBufferXMLParserListener("bmp.dtd");
		ctx.addListener(listener);
		
		// Attempt to harvest the metadata.
		try {
			// Make sure the Harvester System is initialised.
			Config.getInstance();
			
			// Get the appropriate adapter.
			DataAdapter adapter = AdapterFactory.getInstance().getAdapter(f);
			
			// Extract the metadata.
			adapter.adapt(f, ctx);

			// Display the metadata.
			System.out.println(listener.getContents());
			
		} catch (IOException e) {
			// We failed to harvest the metadata, display the 
			// exception.
			e.printStackTrace();
		}
	}
}
