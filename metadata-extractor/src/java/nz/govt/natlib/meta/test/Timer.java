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

package nz.govt.natlib.meta.test;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class Timer {

	private static HashMap timers = new HashMap();

	public static void start(String name) {
		NamedTimer timer = new NamedTimer(name);
		timers.put(name, timer);
		timer.start();
	}

	public static void end(String name) {
		NamedTimer timer = (NamedTimer) timers.get(name);
		timer.stop();
	}

	public static void report() {
		StringBuffer buf = new StringBuffer("\nTimers :\n");
		Iterator it = timers.keySet().iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			NamedTimer timer = (NamedTimer) timers.get(name);
			buf.append("  " + name + " = " + timer.period() + "ms"
					+ (it.hasNext() ? "\n" : ""));
		}

		System.out.println(buf);
	}

	private static class NamedTimer {

		private String name = null;

		private long start;

		private long stop;

		public NamedTimer(String name) {
			this.name = name;
		}

		public void start() {
			start = System.currentTimeMillis();
		}

		public void stop() {
			stop = System.currentTimeMillis();
		}

		public long period() {
			return stop - start;
		}

		public String toString() {
			return period() + "ms";
		}
	}

}