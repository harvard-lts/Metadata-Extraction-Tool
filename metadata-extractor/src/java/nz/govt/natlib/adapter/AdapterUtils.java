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
 package nz.govt.natlib.adapter;

import java.io.InputStream;

import nz.govt.natlib.fx.DataSource;

/**
 * A utility class to abstract the code that is repeated often.
 * @author beaumontb
 *
 */
public class AdapterUtils {

	/**
	 * Close a datasource and print out the exception, but continue processing.
	 * @param ds The DataSource to close.
	 */
	public static void close(DataSource ds) { 
		try {
			ds.close();
		}
		catch(Exception ex) { 
			ex.printStackTrace();
		}
	}

	/**
	 * Close an InputStream and print out the exception, but continue processing.
	 * @param is The InputStream to close.
	 */
	public static void close(InputStream is) { 
		try {
			is.close();
		}
		catch(Exception ex) { 
			ex.printStackTrace();
		}
	}

}
