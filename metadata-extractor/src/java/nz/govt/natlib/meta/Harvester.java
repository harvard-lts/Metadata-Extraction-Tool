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

import java.io.IOException;

import nz.govt.natlib.meta.config.Configuration;

/**
 * Defines the base class for an ObjectHarvest. An object havester is stoppable
 * (ProgressStopListener) and has methods to fire progress events to a listener.
 * 
 * @author unascribed
 * @version 1.0
 */

public abstract class Harvester implements ProgressStopListener {

	private boolean stopping = false;

	/**
	 * No default constructor - every harvester will do different stuff, but
	 * this is mandatory
	 */
	public Harvester() {
	}

	/**
	 * Returns the DTD file that the output metadata files will conform to.
	 * For the Harvester to complete successfully, there must be a mapping that
	 * converts files from their default DTD to the DTD returned by this
	 * method.
	 * 
	 * For example, the GIFAdapter produces output in the gif.dtd format. If 
	 * the Harvester produces output in the format MyMetaData.dtd, then there
	 * must be a mapping XSLT that converts from gif.dtd to MyMetaData.dtd.
	 * 
	 * The output type does not technically need to be a DTD, but you must
	 * create an XSLT mapping to whatever output type you choose.
	 * 
	 * @return The name of the target output type.
	 */
	public abstract String getOutputType();

	/**
	 * Do the work of processing
	 */
	public void harvest(Configuration config, HarvestSource source,
			PropertySource src) throws IOException {
		harvest(config, source, src, new DefaultProgressListener());
	}

	private class DefaultProgressListener implements ProgressListener {
		public void progressEvent(Object event) {
		}
	}

	/**
	 * Do the work of processing
	 */
	public abstract void harvest(Configuration config, HarvestSource source,
			PropertySource src, ProgressListener listener) throws IOException;

	/**
	 * Signal the harvester to stop. The harvester may not stop immediately,
	 * but should stop as soon as possible.
	 */
	public void stop() {
		stopping = true;
	}

	/**
	 * Returns true if the harvester has been told to stop.
	 * @return true if the harvester has been told to stop.
	 */
	public boolean isStopping() {
		return stopping;
	}

	/**
	 * Called when a harvest is started.
	 * @param listener
	 * @param source
	 */
	protected void fireStartHarvest(ProgressListener listener,
			HarvestSource source) {
	}

	protected void fireAbortHarvest(ProgressListener listener,
			HarvestSource source) {
	}

	/**
	 * Called when the harvester finishes.
	 * @param listener  The progress listener.
	 * @param source    The source of the event.
	 * @param sucessful true if harvesting was successful.
	 * @param message   A message to display.
	 */
	protected void fireEndHarvest(ProgressListener listener,
			HarvestSource source, boolean sucessful, Object message) {
		HarvestEvent progEvent = new HarvestEvent(source, sucessful, message);
		fireProgress(listener, progEvent);
	}

	/**
	 * Send a message to the ProgressListener if it exists.
	 * @param listener  The listener to send the event to.
	 * @param progEvent The event to send.
	 */
	protected void fireProgress(ProgressListener listener, Object progEvent) {
		if (listener != null) {
			listener.progressEvent(progEvent);
		}
	}

}