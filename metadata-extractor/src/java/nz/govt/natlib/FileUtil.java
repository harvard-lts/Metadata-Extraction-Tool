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

/*
 * Created on 26/05/2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nz.govt.natlib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author aparker
 */
public class FileUtil {

	public static boolean copy(String src, String dest) {
		// Avoid copying files to themselves.
		if(src.equals(dest)) {
			return true;
		}
		
		
		FileInputStream fr = null;
		FileOutputStream fw = null;
		BufferedInputStream br = null;
		BufferedOutputStream bw = null;

		try {
			fr = new FileInputStream(src);
			fw = new FileOutputStream(dest);
			br = new BufferedInputStream(fr);
			bw = new BufferedOutputStream(fw);

			byte buff[] = new byte[8096];

			int len;
			while ((len = br.read(buff)) > 0)
				bw.write(buff, 0, len);
		} catch (FileNotFoundException fnfe) {
			// System.out.println(src + " does not exist!");
			return false;
		} catch (IOException ioe) {
			// System.out.println("Error reading/writing files!");
			return false;
		} finally {
			try {
				if (br != null)
					br.close();
				if (bw != null)
					bw.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return true;
	}
}
