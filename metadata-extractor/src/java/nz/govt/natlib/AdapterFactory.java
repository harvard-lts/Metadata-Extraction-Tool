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

package nz.govt.natlib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.adapter.any.DefaultAdapter;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */
public class AdapterFactory {

	/**
	 * Inner workings of the class...
	 */

	private static ArrayList adapters = null;

	private static DataAdapter defaultAdapter = null;

	private static AdapterFactory instance = null;

	private HashSet listeners = new HashSet();

	private static final String DEFAULT_ADAPTER = "nz.govt.natlib.adapter.any.DefaultAdapter";

	private AdapterFactory() {
		adapters = new ArrayList();
		defaultAdapter = new DefaultAdapter();
	}

	public void addAdapter(DataAdapter adapter) {
		if (adapter.getClass().getName().equals(DEFAULT_ADAPTER)) {
			defaultAdapter = adapter;
			fireAdapterAdded(adapter);
		} else if (!adapters.contains(adapter)) {
			adapters.add(adapter);
			fireAdapterAdded(adapter);
		}
	}

	public void removeAdapter(DataAdapter adapter) {
		if (adapter.getClass().getName().equals(DEFAULT_ADAPTER)) {
			defaultAdapter = null;
			fireAdapterRemoved(adapter);
		}
		if (adapters.contains(adapter))
			adapters.remove(adapter);
		fireAdapterRemoved(adapter);
	}

	public void addAdapter(DataAdapter[] adapter) {
		for (int i = 0; i < adapter.length; i++) {
			addAdapter(adapter[i]);
		}
	}

	private void fireAdapterAdded(DataAdapter adapter) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			AdapterFactoryListener adl = (AdapterFactoryListener) it.next();
			adl.adapterAdded(adapter);
		}
	}

	private void fireAdapterRemoved(DataAdapter adapter) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			AdapterFactoryListener adl = (AdapterFactoryListener) it.next();
			adl.adapterRemoved(adapter);
		}
	}

	public static synchronized AdapterFactory getInstance() {
		if (instance == null) {
			instance = new AdapterFactory();
		}
		return instance;
	}

	public DataAdapter getAdapter(File file) {
		return getAdapter(file, null);
	}

	public DataAdapter[] getAdapters() {
		int size = adapters.size() + ((defaultAdapter != null) ? 1 : 0);
		DataAdapter[] a = new DataAdapter[size];
		adapters.toArray(a);
		if (defaultAdapter != null) {
			a[size - 1] = defaultAdapter;
		}
		return a;
	}

	public DataAdapter getAdapter(String className) {
		Iterator it = adapters.iterator();
		while (it.hasNext()) {
			DataAdapter da = (DataAdapter) it.next();
			if (da.getClass().getName().equals(className)) {
				return da;
			}
		}
		return null;
	}

	public void addAdapterFactoryListener(AdapterFactoryListener al) {
		listeners.add(al);
	}

	public void removeAdapterFactoryListener(AdapterFactoryListener al) {
		listeners.remove(al);
	}

	/**
	 * several adapters may claim to process this file... Only one really can -
	 * so the next stage is trying to do it with each of them
	 */
	public DataAdapter getAdapter(File file, String output) {
		// ask each adapter if it does this file?
		Iterator it = adapters.iterator();
		while (it.hasNext()) {
			DataAdapter adapter = (DataAdapter) it.next();
			if ((output == null || adapter.getOutputType().equals(output))
					&& Config.getInstance().getCurrentProfile().hasAdapter(
							adapter) && (adapter.acceptsFile(file))) {
				LogManager.getInstance().logMessage(LogMessage.INFO, "Adapter " + adapter.getClass().getName() + " has been chosen to adapt " + file.getName());
				return adapter;
			}
		}
		if ((defaultAdapter != null)
				&& (output == null || defaultAdapter.getOutputType().equals(
						output))
				&& Config.getInstance().getCurrentProfile().hasAdapter(
						defaultAdapter) && (defaultAdapter.acceptsFile(file))) {
			LogManager.getInstance().logMessage(LogMessage.INFO, "Default adapter " + defaultAdapter.getClass().getName() + " has been chosen to adapt " + file.getName());
			return defaultAdapter;
		}

		throw new RuntimeException("No Adapter Found for :" + file.getName());

	}

	public boolean isAdapterLoaded(String adapterClass) {
		// check each adapter to find
		if ((defaultAdapter != null)
				&& (defaultAdapter.getClass().getName().equals(adapterClass))) {
			return true;
		}
		Iterator it = adapters.iterator();
		while (it.hasNext()) {
			DataAdapter adapter = (DataAdapter) it.next();
			if (adapter.getClass().getName().equals(adapterClass)) {
				return true;
			}
		}
		return false;
	}
}