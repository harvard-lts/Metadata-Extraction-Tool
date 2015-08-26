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

import java.io.File;

/**
 * This interface is all that is needed by a harvester operation to describe a
 * heirarchy (tree) of objects that are to be harvested.
 * 
 * @author Nic Evans
 * @version 0.1
 */
public interface HarvestSource {

	public static final int COMPLEX = 3;

	public static final int SIMPLE = 1;

	public HarvestSource[] getChildren();

	public String getName();

	public File getFile();

	public int getType();

	public void setStatus(HarvestStatus status, String message);

}