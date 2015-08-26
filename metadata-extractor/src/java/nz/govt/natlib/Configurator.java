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
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package nz.govt.natlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import javax.swing.JOptionPane;

/**
 * This class performs some checks to try and make sure that the tool
 * is configured correctly.
 * @author bbeaumont
 *
 */
public class Configurator {

	/**
	 * Attempts to update the configuration XML based on the metahome
	 * property.
	 * @param hasUi true if there is a user interface. If false, responses will
	 *        only be written to the command line.
	 */
	public static void install(boolean hasUi) {
		String metahome = getMetaHome();
		
		if(!check()) {
			
			// First make sure that we've guess the install directory correctly.
			File configFile = new File(metahome, "config.xml");
			if(!configFile.exists()) {
				String message = "Could not guess the installation directory.\nPlease edit the startup scripts and set METAHOME explicitly.";
				System.out.println(message);
				if(hasUi) {
					JOptionPane.showMessageDialog(null, message, "Configuration Error", JOptionPane.ERROR_MESSAGE);
				}
				System.exit(1);
			}
			
			
			File newConfigF = new File(metahome, "config.new");
			BufferedReader r = null; 
			PrintWriter w = null;
			
			try {
				// Read and replace the configuration file with the 
				// METAHOME directory.
				r = new BufferedReader(new FileReader(configFile));
				w = new PrintWriter(new FileWriter(newConfigF));
			
				String line = null;
				
				while( (line = r.readLine()) != null) {
					line = line.replaceAll("METADATA_BASE", escapeRegex(metahome));
					w.println(line);
				}
			
				r.close();
				w.close();
			
				File bakFile = new File(getMetaHome(), "config.bak");
				if(configFile.renameTo(bakFile)) {
					newConfigF.renameTo(configFile);
					
					// Create the check file. This will prevent us needing
					// to check the configuration XML every time we start.
					getLockFile().createNewFile();
					
					System.out.println("Successfully configured the Metadata Extraction Tool.");		
				}
				else {
					System.out.println("Failed to configure the Metadata Extraction Tool.");
					System.out.println("Could not rename the configuration files.");
				}
			}
			catch(Exception ex) { 
				System.out.println("Failed to configure the Metadata Extraction Tool.");
				ex.printStackTrace();
			}
			finally {
				close(r);
				close(w);
			}
		}
	}
	
	/**
	 * Escape all backslashes in a string so it is a valid Regular Expression.
	 * @param str The string to escape.
	 * @return The escaped string.
	 */
	private static String escapeRegex(String str) {
		return str.replaceAll("\\\\", "\\\\\\\\");
	}
	
	/**
	 * Close a reader, consuming all exceptions. Very useful in finally 
	 * blocks.
	 * @param r The reader to close.
	 */
	public static void close(Reader r) { 
		if(r != null) { 
			try { 
				r.close();
			}
			catch(Exception ex) {
				// Ignore the exception
			}
		}
	}

	/**
	 * Close a writer, consuming all exceptions. Very useful in finally 
	 * blocks.
	 * @param w The writer to close.
	 */
	public static void close(Writer w) { 
		if(w != null) { 
			try { 
				w.close();
			}
			catch(Exception ex) {
				// Ignore the exception
			}
		}
	}	
	
	/**
	 * Retrieve the metahome directory, removing the trailing slash if one is
	 * present.
	 * @return The metadata home directory.
	 */
	public static String getMetaHome() {
		String metahome = System.getProperty("metahome");
		if(metahome.endsWith("/") || metahome.endsWith("\\")) {
			metahome = metahome.substring(0,metahome.length() - 1);
		}
		return metahome;
	}
	
	/**
	 * Get the file used to quickly detect if we've configured the tool already.
	 * @return The file.
	 */
	public static File getLockFile() {
		return new File(getMetaHome(), "installed.chk");
	}
	
	/**
	 * Check if the tool is configured, but do not try to configure it if it
	 * isn't.
	 * @return true if already configured.
	 */
	public static boolean check() {
		return getLockFile().exists();
	}
	
}
