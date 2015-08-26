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

package nz.govt.natlib.fx;

/**
 * A ParserEvent is an event fired from within an Adapter 
 * @author Nic Evans
 * @version 1.0
 */
public interface ParserEvent {

	public static final int CLOSE_EVENT = 0;

	public static final int OPEN_EVENT = 1;

	public static final int VALUE_EVENT = 2;

	/**
	 * @return the Type of event that this event is.
	 */
	public int getID();

	/**
	 * @return the value of the event
	 */
	public Object getValue();

	/**
	 * @return the ParserContext of this event - not really used much
	 */
	public ParserContext getParent();

	/**
	 * @return the path (dot delimited) of the event within the structure of the
	 *         file being parsed
	 */
	public String getPath();

	/**
	 * @return true if the event is an internal event - some listeners MAY
	 *         implement specific behaviour for this type of event for instance
	 *         a listener that outputs XML may elect NOT to output this value
	 */
	public boolean isInternal();

	/**
	 * @return the names of the parameters associated with this event. Again,
	 *         listeners may choose what to do with these
	 */
	public String[] getParameterNames();

	/**
	 * Gets a parameter by it's name.
	 * 
	 * @param name
	 * @return The parameter value.
	 */
	public Object getParameter(String name);
}