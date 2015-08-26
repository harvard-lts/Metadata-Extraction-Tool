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

package nz.govt.natlib.meta.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author unascribed
 * @version 1.0
 */

public class TextCleaner extends InputStream {

	private InputStream in;

	public static final String XMLAllowed = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ</>:\"' ()=.,-*_[]+;?";

	public static final String HTMLAllowed = "</>\"' ";

	public TextCleaner(InputStream in) {
		this.in = in;
	}

	public int read() throws IOException {
		int i = in.read();
		while (i > -1) {
			if (valid(i)) {
				return i;
			}
			i = in.read();
		}
		return -1; // end of file
	}

	public void close() throws IOException {
		in.close();
	}

	protected boolean valid(int i) {
		return XMLAllowed.indexOf(i) != -1;
	}

	public static void main(String[] args) {

		try {
			FileInputStream in = new FileInputStream(
					"K:\\National Library of NZ\\Projects\\Metadata Extraction Tool Productionisation\\nlnz_presmet_0_3.xsd");

			TextCleaner textCleaner1 = new TextCleaner(in);

			FileOutputStream out = new FileOutputStream("c:\\temp\\clean.txt");
			int i = textCleaner1.read();
			while (i > -1) {
				out.write(i);
				i = textCleaner1.read();
			}

			out.close();
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}