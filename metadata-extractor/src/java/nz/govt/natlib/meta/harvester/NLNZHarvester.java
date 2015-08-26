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

package nz.govt.natlib.meta.harvester;

import java.io.IOException;

import nz.govt.natlib.meta.HarvestSource;
import nz.govt.natlib.meta.Harvester;
import nz.govt.natlib.meta.ProgressListener;
import nz.govt.natlib.meta.PropertySource;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * Harvests Objects in the NLNZ Data Dictionary Schema format.
 * 
 * NOTE: Delegate pattern! Because a decision needs to be made about what the
 * harvester really is supposed to harvest and the calling interface does not
 * know how to make that desision - nor should it, it should be generic.
 * 
 * @author unascribed
 * @version 1.0
 */

public class NLNZHarvester extends Harvester {

	private Harvester delegate = null;

	public NLNZHarvester() {
	}

	public String getOutputType() {
		return "nlnz_presmet.xsd";
	}

	/**
	 * The main method decides which harvester to use based on some inside info
	 * about the source... The delegate then goes on to do the work on behalf of
	 * this class.
	 */
	public void harvest(Configuration config, HarvestSource source,
			PropertySource src, ProgressListener listener) throws IOException {
		// decide what the delegate should be...
		if (delegate == null) {
			if (source.getType() == HarvestSource.SIMPLE) {
				delegate = new SimpleObjectHarvester(this);
			}

			if (source.getType() == HarvestSource.COMPLEX) {
				delegate = new ComplexObjectHarvester(this);
			}
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"Using " + delegate + " harvester");
		}
		delegate.harvest(config, source, src, listener);
	}

	public void stop() {
		if (delegate != null) {
			delegate.stop();
		}
		super.stop();
	}

}