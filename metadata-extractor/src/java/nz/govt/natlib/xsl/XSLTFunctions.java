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

package nz.govt.natlib.xsl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import nz.govt.natlib.meta.MetaUtil;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class XSLTFunctions {

	private static HashMap oldNames;

	public static Object getPreviousFileName(String fileName, String currentPath) {
		String oldName = "";

		if (currentPath != null) {
			if (oldNames == null) {
				oldNames = new HashMap();

				// load it from somewhere...
				try {
					FileInputStream fin = new FileInputStream(fileName);
					DataInputStream din = new DataInputStream(fin);
					while (din.available() > 0) {
						String line = din.readLine();
						String csv1 = line.substring(0, line.indexOf("\t"));
						String csv2 = line.substring(line.indexOf("\t") + 1);
						oldNames.put(csv1, csv2);
					}
					din.close();
					fin.close();
				} catch (FileNotFoundException ex) {
					// there is no file found
					LogManager
							.getInstance()
							.logMessage(
									LogMessage.INFO,
									"File "
											+ fileName
											+ " not found when trying to identify previous filenames");
				} catch (Exception ex) {
					LogManager.getInstance().logMessage(ex);
				}
			}
			oldName = (String) oldNames.get(currentPath);
			if (oldName == null) {
				oldName = "";
			}
		}

		return oldName;
	}

	public static Object determineFileIdentifier(String techComposition, String oid,
			String fileName, String fileIndex) {
		Object fid = "";
		if (techComposition.trim().equalsIgnoreCase("simple")) {
			fid = getIntegerPrefix(fileName);
		} else {
			fid = oid + "-" + fileIndex;
		}

		return fid;
	}

	/**
	 * Determines the numerical prefix at the beginning of a filename
	 * 
	 * @param fileName
	 *            the filename to be 'stripped'
	 * @return a number, 0
	 */
	public static Object getIntegerPrefix(String fileName) {
		// determine the number that precedes all else in the filename
		// (unspecified length);
		String num = "";
		char postfix = '\0';
		for (int i = 0; i < fileName.length(); i++) {
			postfix = fileName.charAt(i);
			if (Character.isDigit(postfix)) {
				num += postfix;
			} else {
				break;
			}
		}
		if (num.equals("") || (postfix != '_')) {
			return "";
		} else {
			return new Long(num);
		}
	}

	public static Object getFileNameOnly(String fileName) {
		String name = fileName;
		if (fileName != null) {
			int i = fileName.lastIndexOf('.');
			name = fileName.substring(0, i);
		}
		return name;
	}

	public static Object getExtension(String fileName) {
		String ext = "";
		if (fileName != null) {
			int i = fileName.lastIndexOf('.');
			ext = fileName.substring(i + 1);
		}
		return ext;
	}
	
//	public static Object getMimeType(String fileName) {
//	String mime = "file/unknown";
//	try {
//		MimeEntry entry = MimeTable.getDefaultTable().findByFileName(fileName);
//		if (entry != null) {
//			mime = entry.getType();
//		}
//	} catch (Exception ex) {
//		// ex.printStackTrace();
//	}
//
//	String ext = (String) getExtension(fileName);
//	if ("bmp".equalsIgnoreCase(ext)) {
//		mime = "image/ms-bmp";
//	}
//	if ("doc".equalsIgnoreCase(ext)) {
//		mime = "text/ms-word";
//	}
//
//	return mime;
//}
	
	//Preeti: - 11.12.13: - Replaced the method above with 'MagicMatch' as 'MimeEntry' is no longer used.
	public static Object getMimeType(File file) {
		String mime = "file/unknown";
		MagicMatch match;
		try {
			match = Magic.getMagicMatch(file, true, false);
			mime = match.getMimeType();
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
		String ext = (String) getExtension(file.getName());
		if ("bmp".equalsIgnoreCase(ext)) {
			mime = "image/ms-bmp";
		}
		if ("doc".equalsIgnoreCase(ext)) {
			mime = "text/ms-word";
		}

		return mime;
	}

	public static Object getSystemProperty(String name) {
		return System.getProperty(name);
	}

	public static Object getCurrentUserAccountName() {
		return System.getProperty("user.name");
	}

	public static Object getHardwareEnvironment() {
		return System.getProperty("os.arch");
	}

	public static Object getSoftwareEnvironment() {
		return "OS: " + System.getProperty("os.name") + " "
				+ System.getProperty("os.version") + ", JVM:"
				+ System.getProperty("java.vendor") + " "
				+ System.getProperty("java.version");
	}

	public static Object getDate(double year, double month, double day, String format) {
		// Date(int, int, int) has been deprecated, so use Calendar to
		// set the year, month, and day.
		Calendar c = Calendar.getInstance();
		// Convert each argument to int.
		c.set((int) year, (int) month, (int) day);
		// create formatter
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		// perform formatting
		return dateFormat.format(c.getTime());
	}

	/**
	 * Assists with some of the streaming file types (wav/mp3/video) where
	 * bitrate and total bytes are given
	 * 
	 * @param bytes
	 * @param bytesPerSec
	 */
	public static Object getDuration(double bytes, double bytesPerSec) {
		long msecs = (long) ((bytes / bytesPerSec) * 1000);
		return MetaUtil.formatDuration(msecs);
	}

	public static Object determineWordVersion(String st) {
		return st;
	}

}