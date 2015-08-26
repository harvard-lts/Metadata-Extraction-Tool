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

import java.util.Date;

/**
 * @author unascribed
 * @version 1.0
 */

public class LogMessage {

	public static final LogLevel WORTHLESS_CHATTER = new LogLevel(
			"Program Workings", -10);

	public static final LogLevel INFO = new LogLevel("Info", 0);

	public static final LogLevel DEBUG = new LogLevel("Debug", 2);

	public static final LogLevel ERROR = new LogLevel("Error", 7);

	public static final LogLevel CRITICAL = new LogLevel("Critical", 10);

	private static long seq = 1000;

	private Object source;

	private Date date;

	private String message;

	private String comment;

	private LogLevel level;

	private long id;

	protected LogMessage(long id, LogLevel level, Date date, Object source,
			String message, String comment) {
		this.source = source;
		this.message = message;
		this.comment = comment;
		this.date = date;
		this.level = level;
		this.id = id;
	}

	public LogMessage(LogLevel level, Object source, String message,
			String comment) {
		this(getNextNumber(), level, new Date(), source, message, comment);
	}

	public String getComment() {
		return comment;
	}

	public Date getDate() {
		return date;
	}

	public String getMessage() {
		return message;
	}

	public Object getSource() {
		return source;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static long getNextNumber() {
		return seq++;
	}

	public long getId() {
		return id;
	}

	public LogLevel getLevel() {
		return level;
	}

}