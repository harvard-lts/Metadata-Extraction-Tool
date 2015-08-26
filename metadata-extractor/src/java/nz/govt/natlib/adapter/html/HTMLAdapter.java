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

package nz.govt.natlib.adapter.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;

/**
 * Adapter for HTML files.
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class HTMLAdapter extends DataAdapter {

	public boolean acceptsFile(File file) {
		// check to see if it looks like a valid html file...
		boolean html = false;

		// Read the first 150 characters and check for the required starting
		// characters.
		try {
			InputStream in = new FileInputStream(file);
			int requiredChars = 150;
			// read until i've got requiredChars chars... one of them will
			// either:
			// <html> or <!doctype html
			byte[] buf = new byte[100];
			StringBuffer firstUp = new StringBuffer();
			while (true) {
				int r = in.read(buf);
				if (r == -1) {
					break;
				}

				for (int i = 0; i < r; i++) {
					if (!Character.isWhitespace((char) buf[i])
							&& buf[i] != ' ') {
						firstUp
								.append(Character
										.toLowerCase((char) buf[i]));
					}
				}

				if (firstUp.length() > requiredChars) {
					break;
				}
			}

			// System.out.println("found:"+firstUp);

			// all the things that make it an html candidate...
			if (firstUp.indexOf("<!doctypehtml") != -1) {
				html = true;
			} else if (firstUp.indexOf("<html>") != -1) {
				html = true;
			}

			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// System.out.println(file+" is an html file "+html);
		return html;
	}

	public String getOutputType() {
		return "html.dtd";
	}

	public String getVersion() {
		return "1.0";
	}

	public String getInputType() {
		return "text/html";
	}

	public String getName() {
		return "HTML Text Adapter";
	}

	public String getDescription() {
		return "Adapts HTML files, extracting all available information";
	}

	public void adapt(File oFile, ParserContext ctx) throws IOException {
		// Header and default information
		ctx.fireStartParseEvent("html");
		writeFileInfo(oFile, ctx);
		try {
			InputStream in = new FileInputStream(oFile);
			HTMLParser parser = new HTMLParser();
			ctx.fireStartParseEvent("html-meta");
			parser.parse(in, new HTMLHarvester(ctx));
			ctx.fireEndParseEvent("html-meta");
			in.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			ctx.fireEndParseEvent("html");
		}
	}

	/**
	 * Listens to events from the parser - in this case the special tags are
	 * harvested for any sign of metadata
	 * 
	 * @author nevans
	 * 
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
	private class HTMLHarvester implements HTMLHandler {

		private ParserContext ctx;

		private boolean head = true;

		private boolean body = false;

		private String currentTag = null;

		private String tagText = "";

		// stats...
		private int tables;

		private int forms;

		private int images;

		private int scripts;

		private int css;

		private boolean frameset = false;

		private boolean comments = false;

		private int words = 0;

		private int chars = 0;

		private int paragraphs = 0;

		// vaues to be written in at the end...
		private String title = "";

		private String mainType = "";

		private String subType = "";

		private String version = "";

		private String dtd = "";

		private String strict = "";

		private String httpEquivType = "";

		private String charset = "";

		private String language = "";

		private String application = "";

		HTMLHarvester(ParserContext ctx) {
			this.ctx = ctx;
		}

		public void docType(String mainType, String subType, String version,
				String strict, String DTD) {
			this.mainType = mainType;
			this.subType = subType;
			this.version = version;
			this.dtd = DTD;
			this.strict = strict;
		}

		public void startTag(String tag, HashMap attributes) {
			currentTag = tag;
			tagText = "";

			if (tag.toLowerCase().equals("body")) {
				body = true;
			}

			if (tag.toLowerCase().equals("meta")) {
				// pop the attributes in as well.
				String name = (String) attributes.get("name");
				String content = (String) attributes.get("content");
				String http = (String) attributes.get("http-equiv");

				if (http != null) {
					if (HTMLParser.stripQuotes(http.toLowerCase()).equals(
							"content-type")) {
						HashMap map = HTMLParser.processAttributes("content="
								+ HTMLParser.stripQuotes(content));
						String type = (String) map.get("content");
						String charset = (String) map.get("charset");
						this.httpEquivType = type == null ? "" : HTMLParser
								.stripQuotes(type);
						this.charset = charset == null ? "" : HTMLParser
								.stripQuotes(charset);
					}
				} else {
					if ((name != null) && (content != null)) {
						ctx.fireStartParseEvent("metatag");
						ctx
								.fireParseEvent("name", HTMLParser
										.stripQuotes(name));
						ctx.fireParseEvent("value", HTMLParser
								.stripQuotes(content));
						ctx.fireEndParseEvent("metatag");
					}
				}
			}

			if (tag.toLowerCase().equals("link")) {
				// pop the attributes in as well.
				String type = (String) attributes.get("type");
				String src = (String) attributes.get("src");
				if (src == null) {
					src = (String) attributes.get("href");
				}
				if ((type != null) && (src != null)) {
					ctx.fireStartParseEvent("associated-entity");
					ctx.fireParseEvent("type", HTMLParser.stripQuotes(type));
					ctx.fireParseEvent("src", HTMLParser.stripQuotes(src));
					ctx.fireEndParseEvent("associated-entity");

					if (type.toLowerCase().indexOf("java") != -1) {
						scripts++;
					}
					if (type.toLowerCase().indexOf("css") != -1) {
						css++;
					}
				}
			}

			if (tag.toLowerCase().equals("html")) {
				// sometimes the html tag has some
				String lang = (String) attributes.get("lang");
				if (lang != null) {
					this.language = HTMLParser.stripQuotes(lang);
				}
			}

			// stats...
			if (tag.toLowerCase().equals("img")) {
				images++;
			}
			if (tag.toLowerCase().equals("form")) {
				forms++;
			}
			if (tag.toLowerCase().equals("frameset")) {
				frameset = true;
			}
			if (tag.toLowerCase().equals("style")) {
				css++;
			}
			if (tag.toLowerCase().equals("script")) {
				scripts++;
			}
			if (tag.toLowerCase().equals("p")) {
				paragraphs++;
			}
		}

		public void endTag(String tag) {
			analyseTextForClues(tagText, false);

			if (tag.toLowerCase().equals("title")) {
				this.title = tagText;
			}

			if (tag.toLowerCase().equals("head")) {
				head = false;
			}

			tagText = "";
			currentTag = null;
		}

		public void text(char[] chars, int start, int end) {
			String text = new String(chars, start, end);
			tagText += text;

			if (body) {
				// update text stats
				this.chars += (end - start);

				// count non-consequetive spaces in the body
				int pos = 0;
				while (true) {
					int newpos = text.indexOf(' ', pos);
					if (newpos == -1) {
						break;
					}
					if (newpos - pos > 1) {
						// i.e. multiple spaces only count once...
						words++;
					}
					pos = newpos + 1;
				}
			}
		}

		public void comment(char[] chars, int start, int end) {
			if (!comments) {
				// analyse for givaway application/CMS names
				// sometimes the application or cms is in here!
				analyseTextForClues(new String(chars, start, end), true);
			}
			comments = true;
		}

		public void endDoc() {
			ctx.fireParseEvent("title", title);

			ctx.fireParseEvent("type", mainType);
			ctx.fireParseEvent("sub-type", subType);
			ctx.fireParseEvent("version", version);
			ctx.fireParseEvent("dtd", dtd);
			ctx.fireParseEvent("scrict", strict);
			ctx.fireParseEvent("http-equiv-type", httpEquivType);
			ctx.fireParseEvent("charset", charset);
			ctx.fireParseEvent("language", language);

			ctx.fireParseEvent("data-tables", tables);
			ctx.fireParseEvent("frameset", frameset + "");
			ctx.fireParseEvent("web-application", (forms > 0) + "");
			ctx.fireParseEvent("forms", forms);
			ctx.fireParseEvent("scripts", scripts);
			ctx.fireParseEvent("style-sheets", css);
			ctx.fireParseEvent("dynamic", (scripts > 0) + "");
			ctx.fireParseEvent("uses-style-sheets", (css > 0) + "");
			ctx.fireParseEvent("contains-comments", comments + "");
			ctx.fireParseEvent("characters", chars);
			ctx.fireParseEvent("words", words);
			ctx.fireParseEvent("paragraphs", paragraphs);
			ctx.fireParseEvent("cms-application", application);
		}

		private void analyseTextForClues(String text, boolean comments) {
			if (text.toLowerCase().indexOf("vignette") != -1) {
				this.application = "Vignette";
			}
			if (text.toLowerCase().indexOf("story server") != -1) {
				this.application = "Vignette V5 Story Server";
			}
			if ((text.toLowerCase().indexOf("instancebegin") != -1)
					&& (comments)) {
				this.application = "MacroMedia Dreamweaver";
			}
			if ((text.toLowerCase().indexOf("mhonarc") != -1) && (comments)) {
				this.application = "MHonArc Perl-HTML";
			}
			if (text.toLowerCase().indexOf("opencms") != -1) {
				this.application = "OpenCMS";
			}
			if (text.toLowerCase().indexOf("alkacon") != -1) {
				this.application = "OpenCMS";
			}
			if (text.toLowerCase().indexOf("wiki") != -1) {
				this.application = "Wiki";
			}
		}

		public void startDoc() {

		}

	}

}