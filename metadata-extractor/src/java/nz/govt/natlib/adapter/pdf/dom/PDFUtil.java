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

/*
 * Created on 28/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.adapter.pdf.dom;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Stack;

import nz.govt.natlib.adapter.pdf.PDFParseException;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class PDFUtil {

	// delimiters
	public static final byte PARA_OPEN = '(';

	public static final byte PARA_CLOSE = ')';

	public static final byte ANGLE_OPEN = '<';

	public static final byte ANGLE_CLOSE = '>';

	public static final byte SQUARE_OPEN = '[';

	public static final byte SQUARE_CLOSE = ']';

	public static final byte BRACKET_OPEN = '{';

	public static final byte BRACKET_CLOSE = '}';

	public static final byte SLASH = '/';

	public static final byte PERCENT = '%';

	public static final byte[] delimiters = new byte[] { PARA_CLOSE, PARA_OPEN,
			ANGLE_CLOSE, ANGLE_OPEN, SQUARE_CLOSE, SQUARE_OPEN, BRACKET_CLOSE,
			BRACKET_OPEN, PERCENT, SLASH };

	public static final char BACKSLASH = '\\';

	// whitespace
	public static final byte NULL = 0x00;

	public static final byte TAB = 0x09;

	public static final byte LF = 0x0a;

	public static final byte FF = 0x0c;

	public static final byte CR = 0x0d;

	public static final byte SPACE = 0x20;

	public static final byte[] whitespace = new byte[] { NULL, TAB, LF, FF, CR,
			SPACE };

	/**
	 * Returns the start position of a token in the stream by looking back from
	 * the current point
	 * 
	 * @param in
	 * @param token
	 * @return the position of the match.
	 */
	public static int seekBackwards(RandomAccessFile in, String token)
			throws IOException {
		int tokenStart = -1;
		byte[] tok = token.getBytes();
		byte[] candidate = new byte[tok.length];
		byte[] buf = new byte[1024];

		long fileLen = in.length();
		long fpos = in.getFilePointer();
		boolean searching = true;
		while (searching) {
			// check how much there is to read prior to this point.
			int r = buf.length > fpos ? (int) fpos : buf.length;
			fpos -= r;
			in.seek(fpos);
			in.read(buf, 0, (int) r);
			// like reading a regular file:
			// - buf is the byte buffer
			// - pos '0' is the start of the read
			// - r is the number of bytes read where 0 or -1 is EOF.
			if (r == 0) {
				searching = false;
			} else {
				// work backwards through the buffer to determine if we have a
				// match...
				for (int i = r; i > 0; i--) {
					System.arraycopy(candidate, 0, candidate, 1,
							candidate.length - 1);
					candidate[0] = buf[i - 1];

					// now check candidate and tok to see if they match!
					boolean match = true;
					for (int k = 0; k < tok.length; k++) {
						if (tok[k] != candidate[k]) {
							match = false;
							break;
						}
					}
					if (match) {
						// found!
						tokenStart = (int) fpos + i - 1;
						searching = false;
						break;
					}
				}

			}
		}
		return tokenStart;
	}

	public static byte[] decodeHexByteString(String st) {
		byte[] b = new byte[st.length() / 2];
		int bpos = 0;
		for (int i = 0; i < st.length(); i += 2) {
			b[bpos++] = ((byte) Integer.parseInt(st.substring(i, i + 2), 16));
		}
		return b;
	}

	public static StringBuffer decodeHexString(StringBuffer st) {
		StringBuffer buf = new StringBuffer();
		// read 16bit chunks - 4chars...
		for (int i = 4; i < st.length(); i += 4) {
			buf.append((char) Integer.parseInt(st.substring(i, i + 4), 16));
		}
		return buf;
	}

	public static StringBuffer decodeNormalString(StringBuffer st) {
		StringBuffer buf = new StringBuffer();
		// normal strings can have delimiters in them (\c) where c is a legit
		// char
		boolean delimit = false;
		for (int i = 0; i < st.length(); i++) {
			char c = st.charAt(i);
			if ((c == '\\') && (!delimit)) {
				delimit = true;
			} else {

				if (delimit) {
					if (c == 'n') {
						c = '\n';
					}
					if (c == 'r') {
						c = '\r';
					}
					if (c == 't') {
						c = '\t';
					}
					if (c == 'b') {
						c = '\b';
					}
					if (c == 'f') {
						c = '\f';
					}
					delimit = false;
				}

				buf.append(c);
			}
		}
		return buf;
	}

	public static final byte[] getISOBytes(String text) {
		if (text == null)
			return null;
		int len = text.length();
		byte b[] = new byte[len];
		for (int k = 0; k < len; ++k)
			b[k] = (byte) text.charAt(k);
		return b;
	}

	public static final boolean isRegular(byte c) {
		return !isDelimiter(c) && !isWhiteSpace(c);
	}

	public static final boolean isDelimiter(byte c) {
		boolean delim = false;
		for (int i = 0; i < delimiters.length; i++) {
			if (c == delimiters[i]) {
				delim = true;
				break;
			}
		}
		return delim;
	}

	public static final boolean isEOL(byte c) {
		return (c == CR || c == LF);
	}

	public static final boolean isWhiteSpace(byte c) {
		boolean delim = false;
		for (int i = 0; i < whitespace.length; i++) {
			if ((byte) c == whitespace[i]) {
				delim = true;
				break;
			}
		}
		return delim;
	}

	private static String indent(int indent) {
		String is = "";
		for (int i = 0; i < indent; i++) {
			is += " ";
		}
		return is;
	}

	public static void walkNodes(PDFNode node) {
		walkNodes(node, 0);
	}

	public static void walkNodes(PDFNode node, int depth) {
		if (node instanceof NodeList) {
			NodeList nodeList = (NodeList) node;
			System.out.println(indent(depth) + "<list>");
			for (int i = 0; i < nodeList.size(); i++) {
				walkNodes(nodeList.get(i), depth + 1);
			}
			System.out.println(indent(depth) + "</list>");
		} else {
			System.out.println(indent(depth) + node);
		}
	}

	public static PDFNode readNodes(RandomAccessFile in, int howMany)
			throws IOException {
		// the 'macro' state of the parser is held
		PDFStack state = new PDFStack(howMany);

		// this is where the 'micro' state of the parser is held
		boolean processingString = false;
		boolean processingHexString = false;
		boolean processingComment = false;
		boolean processingName = false;
		boolean processingToken = false;
		boolean processingStream = false;
		int stringDepth = 0;
		// others that aren't strictly processing
		boolean inXref = false;
		boolean inTrailer = false;
		int dictionaryDepth = 0;
		int arrayDepth = 0;

		// gonna do some fancy bufferring
		StringBuffer buffer = new StringBuffer();
		int variableBuffer = 1;
		byte[] buf = new byte[variableBuffer * 1024]; // variableBuffer(kbytes)
		int streamPos = 0;
		byte[] prevChars = new byte[10]; // last bytes...
		boolean reading = true;
		int bufPos = 0;
		int fpos = 0;
		int bufSize = 0;
		byte curr = 0x00;
		byte prev = 0x00;
		while (reading) {
			// read and add another block
			bufPos = 0;
			bufSize = in.read(buf);
			if (bufSize == -1) {
				break;
			}
			for (; (bufPos < bufSize) && reading; bufPos++) {
				// one more control gate before carying on - state can cause a
				// halt
				if (state.endParsing()) {
					reading = false;
					break;
				}
				
				prev = curr;
				curr = buf[bufPos];
				
				fpos++;
				System.arraycopy(prevChars, 1, prevChars, 0,
						prevChars.length - 1);
				prevChars[prevChars.length - 1] = prev;

				// quick analysis
				boolean dlim = PDFUtil.isDelimiter(curr);
				boolean ws = PDFUtil.isWhiteSpace(curr);
				boolean eol = PDFUtil.isEOL(curr);
				boolean regular = (!dlim && !ws);

				// these bytes may already be spoken for...
				if (processingStream) {
					// stream might have ended...
					// endstream
					int lastCharPos = prevChars.length - 1;
					// need to do a more thoughrough check...
					if ((prevChars[lastCharPos - 0] == 'm')
							&& (prevChars[lastCharPos - 1] == 'a')
							&& (prevChars[lastCharPos - 2] == 'e')
							&& (prevChars[lastCharPos - 3] == 'r')
							&& (prevChars[lastCharPos - 4] == 't')
							&& (prevChars[lastCharPos - 5] == 's')
							&& (prevChars[lastCharPos - 6] == 'd')
							&& (prevChars[lastCharPos - 7] == 'n')
							&& (prevChars[lastCharPos - 8] == 'e')) {
						// process the stream...
						int endOff = (int) in.getFilePointer() - buf.length
								+ bufPos - 9;
						state.processNodeValue(new Integer(streamPos));
						state.processNodeValue(new Integer(endOff - streamPos));
						state.endNode();
						processingStream = false;
						clearBuffer(buffer);
						streamPos = 0;
						continue;
					}
					continue;
				} // fi processingStream
				if (processingHexString) {
					if (curr != PDFUtil.ANGLE_CLOSE) {
						if (regular) {
							buffer.append((char) curr);
						} else {
							throw new RuntimeException(
									"Bad chars in hex string [" + (char) curr
											+ "] filepos:" + fpos);
						}
					} else {
						String string = clearBuffer(buffer);
						state.processNodeValue(string);
						state.endNode();
						processingHexString = false;
					}
					continue;
				} // fi processingHexString
				else if (processingString) {
					// read until the end string char ')' is read...
					boolean close = (curr == PDFUtil.PARA_CLOSE);
					boolean open = (curr == PDFUtil.PARA_OPEN);
					boolean escape = (prev == PDFUtil.BACKSLASH);
					// bit tricky...
					if ((close) && (!escape)) {
						stringDepth--;
					}
					if ((open) && (!escape)) {
						stringDepth++;
					}
					
					if (stringDepth == 0) {
						String string = clearBuffer(buffer);
						state.processNodeValue(string);
						state.endNode();
						processingString = false;
					} else {
						buffer.append((char) curr);
					}
					continue; // cycle to the top
				} // fi processing String
				else if (processingComment) {
					// read until the end comment (eol) char is read...
					if (eol) {
						// comment has ended...
						String comment = clearBuffer(buffer);
						state.processNodeValue(comment);
						state.endNode();
						processingComment = false;
						if (comment.equalsIgnoreCase("%EOF")) {
							// then we need to stop xref and trailer
							if (inTrailer) {
								// that's over now...
								state.endNode();
							}
						}
					} else {
						buffer.append((char) curr);
					}
					continue; // cycle to the top
				} // fi processingComment
				else if (processingName) {
					// read until the end name (any ws or delim) char is read
					// this char (if a delim) may need to be used below
					if (regular) {
						buffer.append((char) curr);
					} else {
						String name = clearBuffer(buffer);
						state.processNodeValue(name);
						state.endNode();
						processingName = false;
					}

					if (processingName)
						continue; // cycle to the top, only if still
									// processing
				} // fi processingName
				else if (processingToken) {
					// read until the end name (any ws or delim) char is read
					// this char (if a delim) may need to be used below
					if (regular) {
						buffer.append((char) curr);
					} else {
						processingToken = false;
						String bufferStr = buffer.toString().toLowerCase();

						// this token might mean something...
						if (bufferStr.equals(PDFNode.START_OBJECT)) {
							state.startNode(new ObjectNode());
							clearBuffer(buffer);
						} else if (bufferStr.equals(PDFNode.START_STREAM)) {
							state.startNode(new StreamNode());
							streamPos = (int) in.getFilePointer() - buf.length
									+ bufPos;
							processingStream = true;
						} else if (bufferStr.equals(PDFNode.END_OBJ)) {
							state.endNode();
							clearBuffer(buffer);

						} else if (bufferStr.equals(PDFNode.START_XREF)) {
							inXref = true;
							clearBuffer(buffer);
							state.startNode(new CrossReferenceTableNode());
						} else if (bufferStr.equals(PDFNode.START_TRAILER)) {
							if (inXref) {
								// that's over now...
								state.endNode();
							}
							state.startNode(new TrailerNode());
							inTrailer = true;
							clearBuffer(buffer);
						} else {
							// do something with it...
							// didn't really know what it was till now, so start
							// and stop the state here
							String unknown = clearBuffer(buffer);
							// work out what it is...
							try {
								state.startNode(determineNode(unknown));
								state.endNode();
							} catch (Exception ex) {
								System.out.println(ex + ", " + fpos);
								ex.fillInStackTrace();
								throw new RuntimeException(ex);
							}

						}
					}

					if (processingToken)
						continue; // cycle to the top
				} // fi processingToken

				// if nothing thinks it's getting them at the mo - then maybe
				// start something new
				if (!ws) {

					// do something with it - something is starting... the world
					// is your oyster...
					if (prev == PDFUtil.ANGLE_OPEN) {
						if (curr == PDFUtil.ANGLE_OPEN) {
							// then we are in a dictionary...
							state.startNode(new DictionaryNode());
							dictionaryDepth++;
							curr = 0x00; // chomp it! - this'll stop us
											// thinking it's a hex string next
											// time, if the very next thing
											// isn't whitespace
							continue;
						} else {
							// we are in a hex string - and this is the first
							// char
							processingHexString = true;
							state.startNode(new HEXStringNode());
							buffer.append((char) curr);
							continue;
						}
					}

					if (curr == PDFUtil.ANGLE_OPEN) {
						// we are waiting till next time through...
						// to see if it's a dict or a hex string
						continue;
					}

					// if it's regular then just absorb it and start a token
					// with it...
					// you must let the << above go first though cause it might
					// be a regular char starting a hex string
					if (regular) {
						processingToken = true;
						// state.start(new GenericNode()); // don't start
						// anything - we need to know what it is, and the clue
						// comes from it's name
						// start (and maybe stop) once we know for sure...
						buffer.append((char) curr);
						continue;
					}

					if (prev == PDFUtil.ANGLE_CLOSE) {
						if (curr == PDFUtil.ANGLE_CLOSE) {
							// close a dictionary
							dictionaryDepth--;
							state.endNode();
							curr = 0x00;
							continue;
						}
					}
					if (curr == PDFUtil.ANGLE_CLOSE) {
						// swallow these, if we get one here it's certainly part
						// of
						// an end dict. if it was a hex string it would be
						// grabbed
						// in the processingHexString loop
						continue;
					}

					// starting an array
					if (curr == PDFUtil.SQUARE_OPEN) {
						state.startNode(new ArrayNode());
						arrayDepth++;
						continue;
					}

					// ending an array
					if (curr == PDFUtil.SQUARE_CLOSE) {
						arrayDepth--;
						state.endNode();
						continue;
					}

					if (curr == PDFUtil.PARA_OPEN) {
						processingString = true;
						state.startNode(new StringNode());
						stringDepth = 1;
						continue;
					}

					if (curr == PDFUtil.PERCENT) {
						processingComment = true;
						state.startNode(new CommentNode());
						continue;
					}

					if (curr == PDFUtil.SLASH) {
						processingName = true;
						state.startNode(new NamedValueNode());
						continue;
					}

					// if we get here then no one else reckoned it was the start
					// of anything!
					throw new PDFParseException("Unknown delimiter "
							+ ((char) curr));
				} // fi !ws (whitespace)

				// tidy up before the 'for' starts again...
			} // end of for loop

		} // end of the 'while' reading loop

		// if you didn't finish the buffer, push it back for someone else to
		// use!
		if (bufSize > bufPos) {
			in.seek(in.getFilePointer() - (bufSize - bufPos));
			// System.out.println("unreading "+(bufSize-bufPos)+" bytes");
		}

		// System.out.println("read "+fpos);
		return state.getRoot();
	}

	private static final String clearBuffer(StringBuffer buffer) {
		String ret = buffer.toString();
		buffer.delete(0, buffer.length());
		return ret;
	}

	/**
	 * An object is three values id, version & the object itself...
	 * 
	 * @param in
	 * @throws IOException
	 */
	public static ObjectNode readObject(RandomAccessFile in) throws IOException {
		NodeList nodes = (NodeList) readNodes(in, 3);
		IntegerNode id = (IntegerNode) nodes.get(0);
		IntegerNode version = (IntegerNode) nodes.get(1);
		ObjectNode obj = (ObjectNode) nodes.get(2);
		obj.setId(id.getIntValue());
		obj.setVersion(version.getIntValue());
		return obj;
	}

	/**
	 * returns a node - all set up with this value...
	 * 
	 * @param unknown
	 * @return
	 */
	private static PDFNode determineNode(String element) {
		// determine it's type...
		String val = element.toLowerCase();
		PDFNode node = null;

		if (val.equals("true") || val.equals("false")) {
			boolean bool = Boolean.getBoolean(val);
			node = new BooleanNode(bool);
		} else if (val.indexOf('.') != -1) {
			double d = Double.parseDouble(val);
			node = new NumberNode(d);
		} else if (val.equals("r")) {
			// node = new ReferenceNode(); // it's propbably a ref node, but
			// can't really tell yet (could just be an 'r')
			node = new StringNode(val);
		} else if (val.equals("n") || (val.equals("f"))) {
			node = new StringNode(val);
		} else if (val.equals("startxref")) {
			node = new StringNode(val);
		} else if (val.equals("null")) {
			// special char.
			node = new NullNode();
		} else {
			int i = Integer.parseInt(val);
			node = new IntegerNode(i);
		}
		return node;
	}

	private static class PDFStack {
		private boolean end;

		private PDFNode currentNode = null;

		private NodeList root = new NodeList();

		private int howManyToRead = 0; // 0=1, 1=2 ....

		private Stack delegateStack = new Stack();

		public PDFStack(int howManyToRead) {
			this.howManyToRead = howManyToRead;
			currentNode = root;
		}
		
		public void startNode(PDFNode node) {
			if (currentNode != null) {
				// coalesce node!
				delegateStack.push(currentNode);
			}
			currentNode = node;
		}

		/**
		 * 
		 * @return a PDFNode if you were only reading 1, ArrayList otherwise...
		 */
		public PDFNode getRoot() {
			if (howManyToRead == 1) {
				return root.get(0);
			}
			return root;
		}

		public void processNodeValue(Object value) {
			// this should be sent to the current node
			// System.out.println("sending -> "+value+" to
			// "+currentNode.getClass().getName());
			currentNode.acceptValue(value);
		}

		public void endNode() {
			PDFNode prePop = currentNode;
			currentNode = (PDFNode) delegateStack.pop();
			// the just completed node should be part of it's parent...
			prePop.flush();
			processNodeValue(prePop);

			// used to be in endParsing but didn't need to happen as often as
			// that
			end = root.size() >= howManyToRead;
		}

		// gets called every byte! so make efficient;
		public boolean endParsing() {
			return end;
		}
	}

}
