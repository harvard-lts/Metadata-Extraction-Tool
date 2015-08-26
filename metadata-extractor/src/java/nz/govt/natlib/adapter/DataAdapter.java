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

package nz.govt.natlib.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * Subclasses of the DataAdapter are responsible for extracting metadata out of
 * file.
 * 
 * To parse a new type of file, you will need to create a subclass.
 * 
 * @author Nic Evans
 * @version 1.0
 */
public abstract class DataAdapter {

	/**
	 * A flag to indicate that the <code>acceptsFile()</code> needs to ignore the 
	 * file extension when deciding whether a given adapter can accept the file.
	 * 
	 * <p><b>Note:</b> By default, this flag is set to <code>false</code> to make 
	 * the <code>acceptsFile()</code> backward-compatible. To make use of this feature, 
	 * one has to call the mutator method 
	 * <code>public void ignoreFileExtension(boolean flag)</code> explicitly with a value
	 * <code>true</code>.
	 * 
	 */
	protected boolean ignoreFileExtension = false;
	
	/**
	 * Sets a flag to indicate that the <code>acceptsFile()</code> needs to ignore the 
	 * file extension when deciding whether a given adapter can accept the file.
	 * 
	 * @param flag New value of the <code>ignoreFileExtension</code> to set.
	 */
	public void ignoreFileExtension(boolean flag) {
		ignoreFileExtension = flag;
	}
	
	/**
	 * This method tests whether this is adapter wants to attempt to extract
	 * metadata from this file. The standard adapters test for a file extension
	 * match, and then make additional tests to increase the confidence of
	 * getting the right type. These extra tests may involve checking header
	 * bytes or looking for particular sequences of characters in the file.
	 * 
	 * This method may make use of the checkFileHeader(File,String) helper
	 * method.
	 * 
	 * @param file
	 *            The file to test.
	 * @return True if this adapter will attempt to extract the metadata from
	 *         the file.
	 */
	public abstract boolean acceptsFile(File file);

	/**
	 * This method adapts the file given into the outputstream - the results
	 * that are placed into the output stream should be in XML format.
	 * 
	 * @throws IOException
	 *             if there are errors reading the file.
	 */
	public abstract void adapt(File file, ParserContext out) throws IOException;

	/**
	 * Return the DTD of the native metadata file this DataAdapter produces. 
	 * Subclasses should hard-code this method to return the name of that DTD.
	 * 
	 * @return The filename of the DTD.
	 */
	public abstract String getOutputType();

	/**
	 * Return the MimeType of the type of file this adapter "adapts".
	 * 
	 * For standard adapters this should return the MIME type of the input file.
	 * Subclasses will, in general, hard-code this value.
	 * 
	 * @return the MIME Type.
	 */
	public abstract String getInputType();

	/**
	 * Gets the MIME type of the file being adapted. The default is to return
	 * the input type.
	 * 
	 * @param file
	 *            The file to get the MIME type for.
	 * @return The MIME type of the file.
	 */
	public String getMimeType(File file) {
		return getInputType();
	}

	/**
	 * Get the name of the adapter.
	 * 
	 * @return The name of the adapter.
	 */
	public abstract String getName();

	/**
	 * Gets a description of the adapter.
	 * 
	 * @return The description of the adapter.
	 */
	public abstract String getDescription();

	/**
	 * Returns the version of the adapter.
	 * 
	 * @return The version of the adapter.
	 */
	public abstract String getVersion();

	/**
	 * This method extracts the basic file meta data from the file, including
	 * filename, path, and file attributes. This method should be called within
	 * the adapt(File,ParserContext) method of subclasses.
	 * 
	 * @param file
	 *            The file to extract meta data from.
	 * @param out
	 *            The parser context.
	 * @throws IOException
	 *             if there are errors gathering the metadata.
	 */
	public final void writeFileInfo(File file, ParserContext out)
			throws IOException {
		out.fireStartParseEvent("METADATA");
		out.fireParseEvent("FILENAME", file.getName());
		out.fireParseEvent("SEPARATOR", File.separator);
		out.fireParseEvent("PARENT", file.getParent());
		out.fireParseEvent("CANONICALPATH", file.getCanonicalPath());
		out.fireParseEvent("ABSOLUTEPATH", file.getAbsolutePath());
		out.fireParseEvent("PATH", file.getPath());
		out.fireParseEvent("FILE", new Boolean(file.isFile()));
		out.fireParseEvent("DIRECTORY", new Boolean(file.isDirectory()));
		out.fireParseEvent("FILELENGTH", new Long(file.length()));
		out.fireParseEvent("HIDDEN", new Boolean(file.isHidden()));
		out.fireParseEvent("ABSOLUTE", new Boolean(file.isAbsolute()));
//		out.fireParseEvent("URL", file.toURL());
//		out.fireParseEvent("URI", file.toURL());
		
		//Removed the deprecated code above.
		out.fireParseEvent("URL", file.toURI().toURL());
		out.fireParseEvent("URI", file.toURI());
		out.fireParseEvent("READ", new Boolean(file.canRead()));
		out.fireParseEvent("WRITE", new Boolean(file.canWrite()));

		if (file.getName().indexOf('.') != -1) {
			out.fireParseEvent("EXTENSION", file.getName().substring(
					file.getName().lastIndexOf('.') + 1));
		} else {
			out.fireParseEvent("EXTENSION", "");
		}

		// format the date correctly
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		Date date = new Date(file.lastModified());
		dateFormatter.applyPattern("yyyy-MM-dd HH:mm:ss");
		out.fireParseEvent("MODIFIED", dateFormatter.format(date));
		dateFormatter.applyPattern(FXUtil.dateFormat);
		out.fireParseEvent("DATE", dateFormatter.format(date));
		out.fireParseEvent("DATEPATTERN", FXUtil.dateFormat);
		dateFormatter.applyPattern(FXUtil.timeFormat);
		out.fireParseEvent("TIME", dateFormatter.format(date));
		out.fireParseEvent("TIMEPATTERN", FXUtil.timeFormat);

		// have a crack at the Mime Type...
		out.fireParseEvent("TYPE", getMimeType(file));

		// some extras...
		out.fireParseEvent("PID", out.getAttribute(ParserContext.PROCESS));
		out.fireParseEvent("OID", out.getAttribute(ParserContext.OBJECT));
		out.fireParseEvent("FID", out.getAttribute(ParserContext.FILE_INDEX));
		out.fireParseEvent("PROCESSOR", "unknown");
		out.fireEndParseEvent("METADATA");
	}

	
	
	public static String toHexFilter(String asciiString) { 
		StringBuffer buff = new StringBuffer();
		
		char[] chars = asciiString.toCharArray();
		for(int i=0;i<chars.length;i++) { 
			buff.append(Integer.toHexString((int) chars[i]));
			if(i < (chars.length-1)) { 
				buff.append(" ");
			}
		}
		
		return buff.toString();
	}
	
	
	/**
	 * Helper method for testing a file header. This method can be used as part
	 * of the acceptsFile(File) method.
	 * 
	 * @param file
	 *            The file to test.
	 * @param test
	 *            A space separated string of hex bytes that will be at the
	 *            start of a file. To skip bytes in the header, use "xx". As an
	 *            example, the WaveAdapter uses the string "52 49 46 46 xx xx xx
	 *            xx 57 41 56 45 66 6D 74 20".
	 * @return true if the string is at the start of the file. False is it isn't
	 *         or if there are any exceptions accessing/parsing the file.
	 */
	public static boolean checkFileHeader(File file, String test) {
		int length = (test.length() + 1) / 3;
		int[] value = new int[length];
		int[] mask = new int[length];

		StringTokenizer tokenizer = new StringTokenizer(test, " ");
		String token = null;

		// Split the string into an array of integers.
		for (int i = 0; tokenizer.hasMoreTokens(); i++) {
			token = tokenizer.nextToken();

			// If the token is xx, set the mask value to zeros so the test
			// of this byte is always successful.
			if ("xx".equals(token)) {
				value[i] = 0;
				mask[i] = 0;
			}

			// If the token is not xx, set the mask to 0xFF and set the test
			// character.
			else {
				value[i] = Integer.parseInt(token, 16);
				mask[i] = 0xFF;
			}
		}

		// Call the method with the calculated arrays.
		return checkFileHeader(file, value, mask);
	}

	/**
	 * Helper method for testing a file header.
	 * 
	 * @param file
	 *            The file to test.
	 * @param test
	 *            An array of bytes to check.
	 * @param mask
	 *            An array of mask bytes to apply.
	 * @return true if the string is at the start of the file. False is it isn't
	 *         or if there are any exceptions accessing/parsing the file.
	 */
	private static boolean checkFileHeader(File file, int[] test, int[] mask) {
		FileInputStream fis = null;

		int testLength = test.length;
		byte[] testBuffer = new byte[testLength];

		try {
			// Read the file.
			fis = new FileInputStream(file);
			fis.read(testBuffer);

			// Loop through all the bytes.
			for (int i = 0; i < test.length; i++) {
				// Mask and test the bytes.
				if (!((testBuffer[i] & mask[i]) == test[i])) {
					return false;
				}
			}
			return true;
		} catch (IOException ex) {
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
			"File header check failed - mustn't be of that type");
			ex.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception ex) {
			}
		}

		// To reach this point we must have thrown an exception. This means that
		// the parsing of the header failed, therefore this is not of the
		// expected type.
		return false;
	}
}