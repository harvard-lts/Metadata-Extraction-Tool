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

import nz.govt.natlib.meta.FileHarvestSource;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.config.ConfigurationException;
import nz.govt.natlib.meta.ui.PropsManager;

/**
 * Sample class showing how to use the Harvester as an embedded tool.
 * 
 * @author bbeaumont
 */
public class HarvesterUsage {

	/**
	 * Harvest a file using both the NLNZ and Native configurations.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {

		// Make sure that we've been given an argument.
		if(args.length != 1) {
			usage();
			System.exit(0);
		}

		// Create the File object.
		File file = new File(args[0]);
		
		// Check that the file exists and that it is not a directory.
		if(!file.exists() || !file.isFile()) {
			usage();
			System.out.println(file.getAbsolutePath() + " does not exist or is not a file");
			System.exit(0);
		}
		
		try {
			// Create a HarvestSource of the object we want to harvest.
			FileHarvestSource source = new FileHarvestSource(file);
			
			// Get the native Configuration.
			Configuration c = Config.getInstance().getConfiguration("Extract in Native form");
			
			// Harvest the file. Note that the output is sent to a file as
			// specified in the configuration. This class produces very little
			// in the way of direct output.
			c.getHarvester().harvest(c, source, new PropsManager());
			
			// Get the NLNZ Data Dictionary configuration.
			c = Config.getInstance().getConfiguration("NLNZ Data Dictionary");
			
			// Harvest the file. Note that the output is sent to a file as
			// specified in the configuration. This class produces very little
			// in the way of direct output.
			c.getHarvester().harvest(c, source, new PropsManager());
			
			System.out.println("Harvest Completed Successfully");
		}
		catch(ConfigurationException ex) {
			// Exception initialising the harvester.
			ex.printStackTrace();
		}
		catch(IOException ex) {
			// Exception performing the harvest.
			ex.printStackTrace();
		}
	}
	
	/**
	 * Display a message on the usage.
	 */
	public static void usage() {
		System.out.println("Usage: java nz.govt.natlib.sample.Test filename");
	}
}
