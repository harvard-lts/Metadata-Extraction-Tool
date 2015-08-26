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

package nz.govt.natlib.adapter.any;

import java.io.File;
import java.io.IOException;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.xsl.XSLTFunctions;

/**
 * @author Nic Evans
 * @version 1.0
 */
public class DefaultAdapter extends DataAdapter {

	public void adapt(File file, ParserContext ctx) throws IOException {
		// add the MetaData to the tree!
		ctx.fireStartParseEvent("default");
		writeFileInfo(file, ctx);
		ctx.fireEndParseEvent("default");
	}

	public String getMimeType(File file) {
		XSLTFunctions func = new XSLTFunctions();
		return "" + func.getMimeType(file);
	}

	public String getVersion() {
		return "1.1";
	}

	public boolean acceptsFile(File file) {
		return true;
	}

	public String getOutputType() {
		return "default.dtd";
	}

	public String getInputType() {
		return "application/*";
	}

	public boolean isSystem() {
		return true;
	}

	public String getName() {
		return "Default Adapter";
	}

	public String getDescription() {
		return "Handles any type of file, harvests only generic filesystem metadata";
	}

}