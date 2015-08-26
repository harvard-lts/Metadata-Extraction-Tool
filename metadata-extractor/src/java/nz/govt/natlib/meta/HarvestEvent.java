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

/**
 * Represents a Harvest Event. Either a successful harvest of a Harvest Source
 * (or unsuccessful with the error).
 * 
 * @author unascribed
 * @version 1.0
 */

public class HarvestEvent {

	private HarvestSource source;

	private boolean sucessful;

	private Object error;

	public HarvestEvent(HarvestSource source, boolean sucessful, Object error) {
		this.source = source;
		this.sucessful = sucessful;
		this.error = error;
	}

	/**
	 * Gets the source of the event - the harvest source
	 * 
	 * @return The source of the event.
	 */
	public HarvestSource getSource() {
		return source;
	}

	/**
	 * Returns the error of the event - if there is one. This can be an
	 * exception or a string 'type' message.
	 * 
	 * @return an Exception or a String/StringBuffer
	 */
	public Object getError() {
		return error;
	}

	/**
	 * 
	 * @return true if the event is successful
	 */
	public boolean isSucessful() {
		return sucessful;
	}
}