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

package nz.govt.natlib.meta.config;

import nz.govt.natlib.meta.Harvester;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * @author Nic Evans
 * @version 1.0
 */
public class Configuration {

	private String name = null;

	private String dir = null;

	private String dtd = null;

	private String className = null;

	public Configuration(String name, String className, String dir, String dtd) {
		this.dtd = dtd;
		this.dir = dir;
		this.name = name;
		this.className = className;
		if (dtd == null) {
			this.className = className;
			try {
				this.dtd = ((Harvester) Class.forName(className).newInstance())
						.getOutputType();
			} catch (Exception e) {
				LogManager
						.getInstance()
						.logMessage(
								new LogMessage(
										LogMessage.ERROR,
										e,
										"Trying to add invalid Harvester class in configuration",
										"Check class name and try again"));
			}
		}
	}
	
	/**
	 * Get the Harvester object for this configuration.
	 * @return The Harvester object.
	 * @throws ConfigurationException if the Harvester object cannot be created.
	 */
	public Harvester getHarvester() throws ConfigurationException {
		try {
			return (Harvester) Class.forName(this.getClassName()).newInstance();
		}
		catch(ClassNotFoundException ex) {
			throw new ConfigurationException("Could not find the Harvester class " + this.getClassName(), ex);
		}
		catch(IllegalAccessException ex) {
			throw new ConfigurationException("Could not access the constructor of the Harvester class " + this.getClassName(), ex);
		}
		catch(InstantiationException ex) {
			throw new ConfigurationException("Could not instrantiate the Harvester class " + this.getClassName(), ex);			
		}
	}

	public String toString() {
		return getName();
	}

	public String getOutputDTD() {
		return dtd;
	}

	public void setOutputDTD(String dtd) {
		this.dtd = dtd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOutputDirectory() {
		return dir;
	}

	public void setOutputDirectory(String dir) {
		this.dir = dir;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
		try {
			dtd = ((Harvester) Class.forName(className).newInstance())
					.getOutputType();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
