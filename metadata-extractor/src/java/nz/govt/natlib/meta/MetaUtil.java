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

import java.text.BreakIterator;

/**
 * @author Nic Evans
 * @version 1.0
 */

public class MetaUtil {

	private static final String[] sizePostfix = new String[] { " Bytes",
			" KBytes", " MBytes", " GBytes" };

	public static String formatText(String text, int width, boolean hard) {
		return formatText(text, width, hard, "\n");
	}

	private static final long SECS = 1000;

	private static final long MINS = SECS * 60;

	private static final long HRS = MINS * 60;

	public static String formatDuration(long number) {
		long hrs = number / HRS;
		number = number % HRS;
		long mins = number / MINS;
		number = number % MINS;
		long secs = number / SECS;
		number = number % SECS;

		return hrs + ":" + mins + ":" + secs + "." + number;
	}

	public static String formatMs(long number) {
		long hrs = number / HRS;
		number = number % HRS;
		long mins = number / MINS;
		number = number % MINS;
		long secs = number / SECS;

		return (hrs > 0 ? hrs + "hours " : "")
				+ (mins > 0 ? mins + " minutes " : "")
				+ (mins < 2 ? secs + " seconds" : "");
	}

	public static String formatBytes(long number) {
		float div = 1024;
		float start = number;
		int pfix = 0;
		while (start > div) {
			start = start / div;
			pfix++;
		}
		// round to 1 dec place...
		start = ((long) (start * 10)) / 10f;

		return start + sizePostfix[pfix];
	}

	public static String formatText(String text, int width, boolean hard,
			String bchar) {
		StringBuffer result = new StringBuffer();
		BreakIterator it = null;
		if (!hard) {
			it = BreakIterator.getWordInstance();
		} else {
			it = BreakIterator.getCharacterInstance();
		}
		it.setText(text);

		int lineLen = 0;
		int start = it.first();
		for (int end = it.next(); end != BreakIterator.DONE; start = end, end = it
				.next()) {
			String next = text.substring(start, end);

			if (lineLen + next.length() > width) {
				result.append(bchar);
				lineLen = 0;
			}

			result.append(next);
			lineLen += next.length();
		}

		return result.toString();
	}

}