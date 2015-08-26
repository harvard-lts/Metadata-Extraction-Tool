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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * ParserContext is a class that represents the context of a particular parsing
 * operation. This 'context' is philosophically similiar a SAX based parser.
 * 
 * A file being parsed is typically represented by a number of nested
 * 'sections':
 * 
 * [[header][data|config|text][data header[blocks][offset]][block 1][block
 * 2][block n]] the above is just an example but you can see that when a file
 * like this is being parsed a number of 'events' can occur. 1. the header is
 * read some data is read from the header and the section ends 2. the next
 * section is read i.e. data, config or text and an event occurs, and the
 * section ends 3. the data header is read 3a. the number of blocks is read 3b.
 * the offset of the blocks is read 4. the data header section ends 5. each
 * block is read...
 * 
 * Firing events for every section and associated data allows a parser to
 * continue on without leaking memory by building up an object model. Output
 * listeners can stream the information out in any format as the input file is
 * parsed. (XML for instance)
 * 
 * @author Nic Evans
 * @version 1.0
 */
public final class ParserContext {

	public static final String PROCESS = "Process_default";

	public static final String OBJECT = "Object_default";

	public static final String FILE_INDEX = "File_default";

	private ArrayList listeners = new ArrayList();

	private HashMap<String, Object> attributes = new HashMap<String, Object>();
	
	private Stack stack = new Stack();

	/**
	 * Creates a new ParserContext and adds a default listener that gathers
	 * attributes as the parse continues.
	 */
	public ParserContext() {
		addListener(new AttributeGatherer());
	}

	/**
	 * Add a listener to the parser. When events occur during the parsing (such
	 * as elements reading discrete information) the parser (this) notifies the
	 * listeners (in no particular order)
	 * 
	 * @param listener
	 *            a listener to be notified of events.
	 */
	public void addListener(ParserListener listener) {
		listeners.add(listener);
	}

	/**
	 * The primary 'fire' method. This method sends an event to all listeners
	 * 
	 * @param id         The type of the event (i.e. ParserEvent.OPEN_EVENT)
	 * @param value      The value associated with the event.
	 * @param internal   True if the event is interal. 
	 * @param parameters The parameters associated with the event.
	 */
	private void fireEvent(int id, Object value, boolean internal,
			HashMap parameters) {
		ParserEvent event = new BasicEvent(this, stack, id, value, internal,
				parameters);
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			ParserListener listener = (ParserListener) it.next();
			listener.handleParseEvent(event);
		}
	}

	/**
	 * 'Fires' a start event. a start event is a new 'section' of data being
	 * read. For the XMLParserListener, this opens a tag with the supplied name.
	 * @param name The name of the section to open.
	 */
	public void fireStartParseEvent(String name) {
		fireStartParseEvent(name, false, null);
	}

	/**
	 * This method starts a new 'section' of the parse. For the 
	 * XMLParserListener this call writes an open tag with the supplied name.
	 * @param name The name of the section to open.
	 * @param internal true if the event is internal. Some listeners may elect
	 *                 to perform special handling with internal events.
	 * @param parameters A hashmap of parameters associated with the event.
	 * 				   The ParserListener is responsible for handling the 
	 * 				   parameters. The XMLParserListener adds them as 
	 * 				   standard XML tag attributes.
	 */
	public void fireStartParseEvent(String name, boolean internal,
			HashMap parameters) {
		stack.push(name);
		fireEvent(ParserEvent.OPEN_EVENT, name, internal, parameters);
	}

	/**
	 * Send a single value metadata element to the parser. 
	 * 'Fires' a simple single 'section' with associated data event. Note this
	 * method will result in a startEvent, dataEvent and endEvent. For example,
	 * the XMLParserListener will display the following for a call to
	 * <code>fieParseEvent("MOD_DATE", "12/12/2006");</code>
	 * 
	 * <MOD_DATE>12/12/2006</MOD_DATE>
	 * 
	 * @param name  The name of the metadata element.
	 * @param value The value of the metadata element.
	 */
	public void fireParseEvent(String name, Object value) {
		fireParseEvent(name, value, false, null);
	}

	/**
	 * 'Fires' a simple single 'section' with associated data event. Note this
	 * method will result in a startEvent, dataEvent and endEvent.
	 * 
	 * @param name  The name of the metadata element.
	 * @param value The value of the metadata element.
	 * @param internal True if this is an internal event. Some parsers may elect
	 *        not to output internal events.
	 * @param parameters A map of parameters associated with this event.
	 */
	public void fireParseEvent(String name, Object value, boolean internal,
			HashMap parameters) {
		fireStartParseEvent(name, internal, parameters);
		fireParseEvent(value, internal);
		fireEndParseEvent(name, internal);
	}

	/**
	 * 'Fires' a simple single 'section' with associated data event. Note this
	 * method will result in a startEvent, dataEvent and endEvent.
	 * 
	 * @param name  The name of the metadata element.
	 * @param value The valueo f the metadata element.
	 */
	public void fireParseEvent(String name, long value) {
		fireParseEvent(name, new Long(value), false, null);
	}

	/**
	 * 'Fires' a simple single 'section' with associated data event. Note this
	 * method will result in a startEvent, dataEvent and endEvent.
	 * 
	 * @param name  The name of the metadata element.
	 * @param value The value of the metadata element.
	 */
	public void fireParseEvent(String name, boolean value) {
		fireParseEvent(name, new Boolean(value), false, null);
	}

	/**
	 * 'Fires' a simple single 'section' with associated data event. Note this
	 * method will result in a startEvent, dataEvent and endEvent.
	 * 
	 * @param name  The name of the metadata element.
	 * @param value The value of the metadata element.
	 */
	public void fireParseEvent(String name, int value) {
		fireParseEvent(name, new Integer(value), false, null);
	}

	/**
	 * 'Fires' a simple single data event.
	 * 
	 * @param value The value.
	 */
	public void fireParseEvent(Object value) {
		fireParseEvent(value, false);
	}

	/**
	 * Send a value directly to the parser. This should be preceded by a 
	 * fireStartParseEvent and tailed by a fireEndParseEvent call. 
	 * 
	 * @param value    The value to output.
	 * @param internal true if this is an internal event. Some parsers may have
	 * 	      special handling for internal events.
	 */
	public void fireParseEvent(Object value, boolean internal) {
		fireEvent(ParserEvent.VALUE_EVENT, value, internal, null);
	}

	/**
	 * Ends a 'section' of the metadata.
	 * 
	 * @param name The name of the section to end.
	 */
	public void fireEndParseEvent(String name) {
		fireEndParseEvent(name, false);
	}

	/**
	 * Ends a 'section' of the metadata.
	 * 
	 * @param name The name of the section to end.
	 * @param internal
	 *            if the event is an internal event - listeners may have
	 *            specific behaviour for these
	 */
	public void fireEndParseEvent(String name, boolean internal) {
		fireEvent(ParserEvent.CLOSE_EVENT, name, internal, null);
		stack.pop();
	}

	/**
	 * Returns an attribute as an integer
	 * 
	 * @param name The name of the attribute to retrieve.
	 * @return the integer attribute.
	 */
	public long getIntAttribute(String name) {
		return Long.parseLong(getAttribute(name).toString());
	}

	/**
	 * Returns an attribute as an boolean
	 * 
	 * @param name The name of the attribute to retrieve.
	 * @return the value of the attribute.
	 */
	public boolean getBooleanAttribute(String name) {
		return Boolean.valueOf(getAttribute(name).toString()).booleanValue();
	}

	/**
	 * returns the value of an attribute that the parser has gathered in it's
	 * parsing
	 * 
	 * @param name The name of the attribute to retrieve.
	 * @return the value of the attribute.
	 */
	public Object getAttribute(String name) {
		return attributes.get(name.toUpperCase());
	}

	/**
	 * sets an attribute of the parser for later use - note users of the
	 * ParserContext can set and get useful attributes from here
	 * 
	 * @param name  The name of the attribute to set.
	 * @param value The value to set.
	 */
	public void setAttribute(String name, long value) {
		attributes.put(name.toUpperCase(), value + "");
	}

	/**
	 * sets an attribute of the parser for later use - note users of the
	 * ParserContext can set and get useful attributes from here
	 * 
	 * @param name  The name of the attribute to set.
	 * @param value The value to set.
	 */
	public void setAttribute(String name, Object value) {
		attributes.put(name.toUpperCase(), value);
	}

	/**
	 * This class represents a generic parser event.
	 * 
	 * @author Nic Evans
	 * @version 1.0
	 */
	private static class BasicEvent implements ParserEvent {
		private int id;

		private Object value;

		private ParserContext parent;

		private Stack path;

		private boolean internalEvent;

		private HashMap parameters;

		/**
		 * constructs a basic event.
		 * 
		 * @param parent
		 * @param path
		 * @param id
		 * @param value
		 * @param internalEvent
		 * @param parameters
		 */
		public BasicEvent(ParserContext parent, Stack path, int id,
				Object value, boolean internalEvent, HashMap parameters) {
			this.id = id;
			this.value = value;
			this.parent = parent;
			this.path = path;
			this.internalEvent = internalEvent;
			this.parameters = parameters;
		}

		public int getID() {
			return this.id;
		}

		public boolean isInternal() {
			return this.internalEvent;
		}

		public Object getValue() {
			return this.value;
		}

		public ParserContext getParent() {
			return parent;
		}

		/**
		 * a fast way to determine if the event has a "path" as specified...
		 * 
		 * @return dotDelimitedAttributeName
		 */
		public String getPath() {
			StringBuffer buf = new StringBuffer();
			int maxIndex = path.size();
			for (int i = 0; i < maxIndex; i++) {
				buf.append(String.valueOf(path.get(i)));
				if (i + 1 < maxIndex) {
					buf.append(".");
				}
			}
			return buf.toString();
		}

		public String[] getParameterNames() {
			if (parameters == null) {
				return null;
			}
			String[] names = new String[parameters.size()];
			parameters.keySet().toArray(names);
			return names;
		}

		public Object getParameter(String name) {
			return parameters.get(name);
		}

	}

	/**
	 * Listens to the parser and gethers attributes. The attributes
	 * are automatically placed into the parser context. NOTE: This may cause a
	 * leak, it also violates the principles of the ParserContext to be able to
	 * parse as you go! But some of the Elements require to be able to get
	 * attributes back out of the context.
	 * 
	 * @author unascribed
	 * @version 1.0
	 */
	private class AttributeGatherer implements ParserListener {

		//private HashMap variables = new HashMap();

		public void handleParseEvent(ParserEvent event) {
			int id = event.getID();

			if (id == ParserEvent.VALUE_EVENT) {
				Object value = event.getValue();
				String path = event.getPath();
				// System.out.println(path+"="+value);
				setAttribute(path, value);
			}
		}

	}

	public void printAttributes() {
    	
    	for (Map.Entry<String, Object> entry : attributes.entrySet()){
    		String key = entry.getKey();
    		Object value = entry.getValue();
    		if (value != null){
    			System.out.println(key + ": " + value.toString());
    		}else {
    			System.out.println(key + ": null");
    		}
    	}
	}
}