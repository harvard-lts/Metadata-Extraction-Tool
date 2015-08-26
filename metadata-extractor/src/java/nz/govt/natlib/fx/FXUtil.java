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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title: FXUtil
 * </p>
 * <p>
 * Description: A utiulity class to assis other classes in the fx package
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class FXUtil {

	/**
	 * cool helper method to convert values...
	 * 
	 * @param value
	 *            the value of the object to be converted.
	 * @return The string value.
	 */
	public static String getString(Object value) {
		return String.valueOf(value);
	}

	/**
	 * Returns a long value from an object, assuming the object is a number
	 * 
	 * @param value
	 * @return The long value of the object.
	 */
	public static long getLong(Object value) {
		return ((Number) value).longValue();
	}

	public static final String dateFormat = "yyyyMMdd";

	public static final String timeFormat = "HHmmssSSS";

	public static void writeDateTag(XMLParserListener to, String tag, Date date)
			throws IOException {
		SimpleDateFormat formatter = new SimpleDateFormat("z");
		String zone = formatter.format(date);
		formatter.applyPattern(dateFormat);
		String dateString = formatter.format(date);
		formatter.applyPattern(timeFormat);
		String timeString = formatter.format(date);

		to.writeTagOpen(tag, new String[] { "locale" }, new String[] { zone });
		to.writeTagOpen("Date", new String[] { "format" },
				new String[] { dateFormat });
		to.writeTagContents(dateString);
		to.writeTagClose("Date");
		to.writeTagOpen("Time", new String[] { "format" },
				new String[] { timeFormat });
		to.writeTagContents(timeString);
		to.writeTagClose("Time");
		to.writeTagClose(tag);
	}

	/**
	 * Returns a int value from an object, assuming the object is a number
	 * 
	 * @param value
	 * @return The int value of the object.
	 */
	public static int getInteger(Object value) {
		return ((Number) value).intValue();
	}

	/**
	 * Returns a short value from an object, assuming the object is a number
	 * 
	 * @param value
	 * @return The short value.
	 */
	public static short getShort(Object value) {
		return ((Number) value).shortValue();
	}

	/**
	 * Returns a byte value from an object, assuming the object is a number
	 * 
	 * @param value
	 * @return The byte value.
	 */
	public static byte getByte(Object value) {
		return ((Number) value).byteValue();
	}

	/**
	 * Returns a char value from an object, assuming the object is a number
	 * 
	 * @param value
	 * @return the char value.
	 */
	public static char getChar(Object value) {
		return (char) ((Number) value).byteValue();
	}

	/**
	 * Seta a value into an object.
	 * 
	 * @param bean
	 *            the object to recieve the value
	 * @param property
	 *            the properties name
	 * @param value
	 *            the value to be set into the bean
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static void setProperty(Object bean, String property, Object value)
			throws InvocationTargetException, IllegalAccessException {
		HashMap setters = getPropertySetters(bean);
		String adjParamName = property.toLowerCase();
		// now populate the bean with the appropriate values - looking at types
		// of course...
		Method method = (Method) setters.get(adjParamName);
		if (method != null) {
			mapPropertyValueToMethod(bean, method, value);
		}
	}

	/**
	 * Invoke a method if it can be found! Use this to remain backward
	 * compatible with older JDK libraries that do not have the methods you
	 * might want. This will allow your code to compile and run - as long as you
	 * have a plan for what to do if this method returns null.
	 * 
	 * @param obj
	 *            the object upon which, to call the method
	 * @param method
	 *            the method you would like to call
	 * @param param
	 *            the parameter (or parameters if you pass an array) you wish to
	 *            pass. The types of the parameters form part of the method
	 *            signature so make sure types are right.
	 * @return the return value from the method - if there is one. This will be
	 *         null if the method was not avaialble and also null if the method
	 *         does not have a return value.
	 */
	public static Object invoke(Object obj, String method, Object param,
			Object paramType) {
		Object result = null;
		Object[] params = null;
		Class[] paramTypes = null;
		if (param.getClass().isArray()) {
			params = (Object[]) param;
			paramTypes = (Class[]) paramType;
		} else {
			params = new Object[1];
			params[0] = param;
			paramTypes = new Class[1];
			paramTypes[0] = (Class) paramType;
		}
		try {
			Method realMethod = obj.getClass().getMethod(method, paramTypes);
			realMethod.setAccessible(true);
			result = realMethod.invoke(obj, params);

			// sucessful invoke, if the result is still null, then the method
			// did not have a return value
		} catch (IllegalAccessException ex) {
			// just can't call it - bad luck. The desired behaviour is to
			// continue without throwing exceptions
			// modify this to send back an error
		} catch (InvocationTargetException ex) {
			// just can't call it - bad luck. The desired behaviour is to
			// continue without throwing exceptions
			// modify this to send back an error
		} catch (NoSuchMethodException ex) {
			// just can't call it - bad luck. The desired behaviour is to
			// continue without throwing exceptions
			// modify this to send back an error
		}

		return result;
	}

	/**
	 * getPropertySetters takes an object that has get/set methods in the style
	 * of a Java bean. The method will go through all public "setter" methods
	 * and return a list of them (methods) in a hashtable. The keys in the
	 * returned hashmap have the prefix (set,can,put) stripped off and all in
	 * lower case.
	 * 
	 * @param bean
	 *            an Object in the style of a java bean.
	 * @return HashMap a key/value pair containing a lower case method name for
	 *         the key and method as the value.
	 */
	private static HashMap getPropertySetters(Object bean) {
		String[] propSetters = new String[] { "set", "can", "put" };
		HashMap setters = new HashMap();
		Method[] methods = bean.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			String propName = null;
			// turn into a property...
			for (int p = 0; p < propSetters.length; p++) {
				if (methodName.startsWith(propSetters[p])) {
					// strip off the prefix
					propName = methodName.substring(propSetters[p].length())
							.toLowerCase();
					break;
				}
			}
			if (propName != null) {
				setters.put(propName, method);
			}
		}
		return setters;
	}

	/**
	 * The mapPropertyValueToMethod method takes a string property and sets it
	 * into the given method on the given bean. NOTE: This method will require a
	 * lot of work to map different types and arrays to a method
	 * 
	 * @param bean
	 *            the instance of a java bean like object.
	 * @param method
	 *            the method to invoke.
	 * @param value
	 *            an Array of strings containing the values that will be used as
	 *            the parameters for the method.
	 */
	private static void mapPropertyValueToMethod(Object bean, Method method,
			Object value) throws InvocationTargetException,
			IllegalAccessException {
		Class[] types = method.getParameterTypes();
		if (types.length == 1) {
			method.invoke(bean, new Object[] { value });
		}
	}

	/**
	 * Returns a string of a fixed length from a datasource
	 * 
	 * @param dts
	 * @param length
	 * @return The string value.
	 * @throws IOException
	 */
	public static String getFixedStringValue(DataSource dts, int length)
			throws IOException {
		byte[] b = dts.getData(length);
		return new String(b);
	}

	/**
	 * Reads a 'pascal' style string from a datasource.
	 * 
	 * @param dts
	 * @return A string value.
	 * @throws IOException
	 */
	public static String getPascalStringValue(DataSource dts)
			throws IOException {
		int ilength = (int) getNumericalValue(dts, IntegerElement.BYTE_SIZE,
				false);
		// the length is fixed...
		byte[] buf = dts.getData(ilength);
		return new String(buf);
	}

	/**
	 * Returns a numerical value from a datasource
	 * 
	 * @param dts
	 * @param size
	 *            number of bytes to read
	 * @param bigEndian
	 *            true if the bytes read are big endian.
	 * @return The number read from the datasource.
	 * @throws IOException
	 */
	public static long getNumericalValue(DataSource dts, int size,
			boolean bigEndian) throws IOException {
		byte[] b = dts.getData(size);
		return getNumericalValue(b, bigEndian);
	}

	/**
	 * returns an integer that represents the bytes passed in
	 * 
	 * @param in
	 * @param bigEndian
	 *            true if the bytes passed in are big endian.
	 * @return The numerical value of the bytes.
	 */
	public static long getNumericalValue(byte[] in, boolean bigEndian) {
		long val = -1;
		if (!bigEndian) {
			val = getLittleEndian(in, 0, in.length);
		} else {
			val = getBigEndian(in, 0, in.length);
		}
		return val;
	}

	/**
	 * Takes some bytes and returns the Little Endian version of those bytes as
	 * an integer.
	 * 
	 * @param data
	 *            the block of data containing the number
	 * @param offset
	 *            the offset to start processing from
	 * @param size
	 *            the sizer of the number in bytes
	 * @return the integer.
	 */
	protected static long getLittleEndian(final byte[] data, final int offset,
			final int size) {
		long result = 0;

		for (int j = offset + size - 1; j >= offset; j--) {
			result <<= 8;
			result |= 0xff & data[j];
		}
		return result;
	}

	/**
	 * Takes some bytes and returns the Big Endian version of those bytes as an
	 * integer.
	 * 
	 * @param data
	 *            the block of data containing the number
	 * @param offset
	 *            the offset to start processing from
	 * @param size
	 *            the sizer of the number in bytes
	 * @return the integer.
	 */
	protected static long getBigEndian(final byte[] data, final int offset,
			final int size) {
		long result = 0;

		for (int j = offset; j < offset + size; j++) {
			result <<= 8;
			result |= 0xff & data[j];
		}
		return result;
	}

	/**
	 * Helper method to retrieve a value from an element directly.
	 * 
	 * @param ds
	 * @param element
	 * @return The object read from the stream.
	 * @throws IOException
	 */
	public static Object readElement(DataSource ds, Element element)
			throws IOException {
		ParserContext context = new ParserContext();
		QuickParserListener listener = new QuickParserListener();
		context.addListener(listener);
		element.read(ds, context);
		return listener.getValue();
	}

	public static void debugElement(DataSource ds, Element element)
			throws IOException {
		ParserContext context = new ParserContext();
		DebugParserListener listener = new DebugParserListener();
		context.addListener(listener);
		element.read(ds, context);
	}

	private static class DebugParserListener implements ParserListener {
		private Object result = null;

		public void handleParseEvent(ParserEvent value) {
			int id = value.getID();
			if (id == ParserEvent.OPEN_EVENT) {
				System.out.println("Name = " + value.getValue().toString());
			}
			if (id == ParserEvent.VALUE_EVENT) {
				System.out.println("  Value = " + value.getValue().toString());
			}
			if (id == ParserEvent.CLOSE_EVENT) {
			}
		}
	}

	private static class QuickParserListener implements ParserListener {
		private Object result = null;

		public void handleParseEvent(ParserEvent value) {
			result = value.getValue();
		}

		public Object getValue() {
			return result;
		}
	}

}