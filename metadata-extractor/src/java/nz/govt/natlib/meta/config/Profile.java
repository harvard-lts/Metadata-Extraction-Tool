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
 * Created on 12/05/2004
 *
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package nz.govt.natlib.meta.config;

import java.util.HashMap;
import java.util.Iterator;

import nz.govt.natlib.adapter.DataAdapter;

/**
 * @author root
 * 
 */
public class Profile {
	private String inputDir = "";

	private String logDir = "";

	private String name = "unnamed";

	private HashMap adapters = new HashMap();

	public Profile() {

	}

	public void setAdapter(String adapterClass, boolean on) {
		if (on)
			adapters.put(adapterClass, adapterClass);
		else
			adapters.remove(adapterClass);
	}

	public void setLogDirectory(String dir) {
		logDir = dir;
	}

	public void setInputDirectory(String dir) {
		inputDir = dir;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInputDirectory() {
		return inputDir;
	}

	public String getLogDirectory() {
		return logDir;
	}

	public String getName() {
		return name;
	}

	public Iterator getAdapterClasses() {
		return adapters.keySet().iterator();
	}

	public boolean hasAdapter(DataAdapter adapter) {
		return adapters.containsValue(adapter.getClass().getName());
	}

	public String toString() {
		return name;
	}

}
