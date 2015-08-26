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
package nz.govt.natlib.meta;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This "Transformer" just copies data from the input stream
 * to the output stream. The rationale of calling this a "Transformer"
 * is for consistency.
 *  
 * @author bbeaumont
 *
 */
public class DoNothingTransformer extends TransformProcessor {
	
	
	public void transform(InputStream in, OutputStream out) {
		try {
			byte[] buf = new byte[25000];
			int count = 0;
			for (count = in.read(buf); count > -1;) {
				out.write(buf, 0, count);
				count = in.read(buf);
			}
		} catch (IOException ex) {
			// error(ex);
			throw new RuntimeException("Transformation error: "
					+ ex.getMessage());
		}
	}
}