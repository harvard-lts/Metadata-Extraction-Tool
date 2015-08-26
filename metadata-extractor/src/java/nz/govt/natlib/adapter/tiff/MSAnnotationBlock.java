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

package nz.govt.natlib.adapter.tiff;

import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.ParserContext;

/**
 * @author unascribed
 * @version 1.0
 */
public class MSAnnotationBlock extends Element {

	private long length = 0;

	public MSAnnotationBlock(long length) {
		this.length = length;
	}

	/**
	 * Read the annotation block. <a
	 * href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/3rdparty/html/processingofannotationdataintiffimagefiles.asp">MS
	 * Developer Network</a>
	 * 
	 * @param data
	 */
	public void read(DataSource data, ParserContext ctx) {
		ctx.fireParseEvent("unknown");
	}

}