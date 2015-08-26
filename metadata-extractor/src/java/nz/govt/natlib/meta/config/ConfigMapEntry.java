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

/**
 * @author Nic Evans
 * @version 1.0
 */
public class ConfigMapEntry {
	private String inputDTD = null;

	private String outputDTD = null;

	private String xsltFunction = null;

	public ConfigMapEntry(String inputDTD, String outputDTD, String xsltFunction) {
		this.inputDTD = inputDTD;
		this.outputDTD = outputDTD;
		this.xsltFunction = xsltFunction;
	}

	public String getInputDTD() {
		return this.inputDTD;
	}

	public String getOutputDTD() {
		return this.outputDTD;
	}

	public String getXsltFunction() {
		return this.xsltFunction;
	}
}
