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

package nz.govt.natlib.meta.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Manages logging.
 * 
 * @author unascribed
 * @version 1.0
 */

public class LogManager implements Log {

	private HashMap logs;

	private static final LogManager instance = new LogManager();

	private Log defaultLogger = new ConsoleLogger();

	private LogManager() {
		logs = new HashMap();

		// some defaults...
	}

	public static LogManager getInstance() {
		return instance;
	}

	public void addLog(Log log) {
		addLog(log, null);
	}

	public synchronized void addLog(Log log, LogFilter filter) {
		if (filter == null) {
			filter = new NullFilter();
		}
		logs.put(log, filter);
	}

	public synchronized void logMessage(Throwable ex) {
		ex.printStackTrace();
		LogMessage msg = new LogMessage(LogMessage.ERROR, ex, ex.getMessage(),
				null);
		logMessage(msg);
	}

	public synchronized void logMessage(LogLevel level, String message) {
		LogMessage msg = new LogMessage(level, null, message, null);
		logMessage(msg);
	}

	public synchronized void logMessage(LogMessage message) {
		Iterator it = instance.logs.keySet().iterator();
		boolean anyone = false;
		while (it.hasNext()) {
			Log log = (Log) it.next();
			LogFilter filter = (LogFilter) instance.logs.get(log);
			if (filter.filter(message)) {
				try {
					log.logMessage(message);
					anyone = true;
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
				}
			}
		}

		if (!anyone) {
			// System log it...
			try {
				instance.defaultLogger.logMessage(message);
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
		}
	}

	/**
	 * Takes a StackTrace and flattens it to a string for output
	 * 
	 * @param ex
	 *            the Exception object
	 * @return the StackTrace as a String
	 */
	public static String flattenStack(Throwable ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		return new String(sw.getBuffer());
	}

	public synchronized void suspendEvents(boolean suspend) {
		Iterator it = logs.keySet().iterator();
		while (it.hasNext()) {
			((Log) it.next()).suspendEvents(suspend);
		}
	}

	public void close() {
		Iterator it = logs.keySet().iterator();
		while (it.hasNext()) {
			Log log = (Log) it.next();
			try {
				log.close();
			} catch (Throwable t) {
				// what are ya going to do? log it!
			}
		}
	}

	private static class NullFilter implements LogFilter {
		public boolean filter(LogMessage message) {
			return true;
		}
	}

	public static class ConsoleLogger implements Log {
		public void logMessage(LogMessage message) {
			System.out.println("LOG:" + message.getId() + ", "
					+ message.getMessage());
			if (message.getSource() instanceof Throwable) {
				((Throwable) message.getSource()).printStackTrace(System.err);
			}
		}

		public void close() {
		}

		public void suspendEvents(boolean suspend) {
		}
	}

}