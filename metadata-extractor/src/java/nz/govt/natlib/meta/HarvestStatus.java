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
 * Describes the status of a Harvest.
 * 
 * @author unascribed
 * @version 1.0
 */

public class HarvestStatus {

	/**
	 * The Harvest failed
	 */
	public static final HarvestStatus ERROR = new HarvestStatus("Error");

	/**
	 * The Harvest was successful
	 */
	public static final HarvestStatus OK = new HarvestStatus("OK");

	/**
	 * The harvest has not occured or was undetermined
	 */
	public static final HarvestStatus BLANK = new HarvestStatus("BLANK");

	private String name;

	private HarvestStatus(String name) {
		this.name = name;
	}
}