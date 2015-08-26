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

package nz.govt.natlib.meta.log;

/**
 * Filters log messages based on their level.
 * 
 * @author unascribed
 * @version 1.0
 */
public class LevelFilter implements LogFilter {

	private boolean inclusive = true;

	private boolean only = false;

	private String description;

	private LogLevel filterLevel;

	public LevelFilter(LogLevel filterLevel) {
		this(null, filterLevel, false, true);
	}

	/**
	 * 
	 * @param description
	 * @param filterLevel
	 * @param only
	 *            set this to filter out all messages but the level given.
	 * @param inclusive
	 *            set this to also include the level given.
	 */
	public LevelFilter(String description, LogLevel filterLevel, boolean only,
			boolean inclusive) {
		this.inclusive = inclusive;
		this.only = only;
		this.description = description;
		this.filterLevel = filterLevel;
	}

	public boolean filter(LogMessage message) {
		if (only) {
			return message.getLevel().getLevel() == filterLevel.getLevel();
		}

		if (inclusive) {
			return message.getLevel().getLevel() >= filterLevel.getLevel();
		}

		return message.getLevel().getLevel() > filterLevel.getLevel();
	}

	public String getDescription() {
		if (description != null) {
			return description;
		}
		return filterLevel.getName();
	}

	public String toString() {
		return getDescription();
	}
}
