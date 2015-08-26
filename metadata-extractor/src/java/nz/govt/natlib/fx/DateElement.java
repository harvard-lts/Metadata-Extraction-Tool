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
import java.text.SimpleDateFormat;

/**
 * Reads bytes and converts them to a Java Date according to a date pattern. the
 * pattern is standard UTF timecodes eg:
 * 
 * <blockquote>
 * 
 * <pre>
 *  Symbol   Meaning                 Presentation        Example
 *  ------   -------                 ------------        -------
 *  G        era designator          (Text)              AD
 *  y        year                    (Number)            1996
 *  M        month in year           (Text &amp; Number)     July &amp; 07
 *  d        day in month            (Number)            10
 *  h        hour in am/pm (1&tilde;12)    (Number)            12
 *  H        hour in day (0&tilde;23)      (Number)            0
 *  m        minute in hour          (Number)            30
 *  s        second in minute        (Number)            55
 *  S        millisecond             (Number)            978
 *  E        day in week             (Text)              Tuesday
 *  D        day in year             (Number)            189
 *  F        day of week in month    (Number)            2 (2nd Wed in July)
 *  w        week in year            (Number)            27
 *  W        week in month           (Number)            2
 *  a        am/pm marker            (Text)              PM
 *  k        hour in day (1&tilde;24)      (Number)            24
 *  K        hour in am/pm (0&tilde;11)    (Number)            0
 *  z        time zone               (Text)              Pacific Standard Time
 *  '        escape for text         (Delimiter)
 *  ''       single quote            (Literal)           '
 * 
 *  Examples
 *  Format Pattern                         Result
 *  --------------                         -------
 *  &quot;yyyy.MM.dd G 'at' hh:mm:ss z&quot;    -&gt;&gt;  1996.07.10 AD at 15:08:56 PDT
 *  &quot;EEE, MMM d, ''yy&quot;                -&gt;&gt;  Wed, July 10, '96
 *  &quot;h:mm a&quot;                          -&gt;&gt;  12:08 PM
 *  &quot;hh 'o''clock' a, zzzz&quot;           -&gt;&gt;  12 o'clock PM, Pacific Daylight Time
 *  &quot;K:mm a, z&quot;                       -&gt;&gt;  0:00 PM, PST
 *  &quot;yyyyy.MMMMM.dd GGG hh:mm aaa&quot;    -&gt;&gt;  1996.July.10 AD 12:08 PM *
 * 
 * @author Nic Evans
 * @version 1.0
 */
public class DateElement extends Element {

	private String pattern;

	private int length;

	/**
	 * Note: the pattern MUST be the same length as the data to be checked. The
	 * pattern should be standard UTF codes for date/time
	 * 
	 * @param pattern
	 *            The pattern to be applied to the bytes.
	 */
	public DateElement(String pattern) {
		this(pattern, pattern.length());
	}

	/**
	 * The pattern to be applied to the bytes. The pattern should be standard
	 * UTF codes for date/time
	 * 
	 * @param pattern
	 *            The pattern to be applied to the bytes.
	 * @param length
	 *            the length of the bytes to be parsed.
	 */
	public DateElement(String pattern, int length) {
		this.pattern = pattern;
		this.length = length;
	}

	/**
	 * Reads a data and converts to a java.util.Date
	 * 
	 * @param data
	 *            the datasource
	 * @param ctx
	 *            the context.
	 * @throws IOException
	 */
	public void read(DataSource data, ParserContext ctx) throws IOException {

		byte[] buf = data.getData(length);

		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			dateFormatter.applyPattern(pattern);
			fireParseEvent(ctx, dateFormatter.parse(new String(buf)));
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

}
