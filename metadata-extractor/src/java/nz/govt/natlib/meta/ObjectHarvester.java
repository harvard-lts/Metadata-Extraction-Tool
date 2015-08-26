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

import nz.govt.natlib.meta.config.Configuration;

/**
 * The base class for Harvesters. This class implements a few features, but 
 * is primarily an interface for concrete implementations.
 * @author unascribed
 * @version 1.0
 */

public abstract class ObjectHarvester implements ProgressStopListener {

	private boolean stopping = false;

	/**
	 * No default constructor - every harvester will do different stuff, but
	 * this is mandatory
	 */
	public ObjectHarvester() {
	}

	/**
	 * Do the work of processing
	 */
	public abstract void harvest(Configuration config, HarvestSource source,
			PropertySource src, ProgressListener listener);

	public void stop() {
		stopping = true;
	}

	public boolean isStopping() {
		return stopping;
	}

	public void fireStartHarvest(ProgressListener listener, HarvestSource source) {
		// HarvestEvent progEvent = new HarvestEvent(source,true,null);
	}

	public void fireEndHarvest(ProgressListener listener, HarvestSource source,
			boolean sucessful, String message) {
		HarvestEvent progEvent = new HarvestEvent(source, sucessful, message);
		fireProgress(listener, progEvent);
	}

	private void fireProgress(ProgressListener listener, Object progEvent) {
		if (listener != null) {
			listener.progressEvent(progEvent);
		}
	}

}