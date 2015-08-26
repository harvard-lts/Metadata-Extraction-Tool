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
 * Created on 27/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.adapter.pdf.dom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;

import nz.govt.natlib.adapter.pdf.crypt.NoEncryptionSecurityHandler;
import nz.govt.natlib.adapter.pdf.crypt.PDFSecurityHandler;
import nz.govt.natlib.adapter.pdf.crypt.StandardSecurityHandler;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * @author nevans
 * 
 * This is the minimum we need to read into memory in order to make our way
 * around a PDF.
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PDFDocument {

	private PDFSecurityHandler securityHandler;

	private ArrayList xrefs = new ArrayList(); // newest first

	private ArrayList trailers = new ArrayList(); // newest first

	private DictionaryNode info = null;

	private DictionaryNode root = null;

	private DictionaryNode encrypt = null;

	// other interesting document info...
	private String version = null;

	private String producer = null;

	private String creator = null;

	private Date creationDate = null;

	private String title = null;

	private String subject = null;

	private Date modificationDate = null;

	private String author = null;

	private String keywords = null;

	private String trapped = null;

	private boolean isTagged;

	private String language;

	private boolean hasMetadataStream;

	private boolean hasForms;

	private boolean hasThreads;

	private boolean hasOutline;

	private String pageMode;

	private String pageLayout;

	private int keyLength = 40;

	private String schemeType;

	private String scheme;

	private boolean allowTextNotes = true;

	private boolean allowCopy = true;

	private boolean readOnly = true;

	private boolean print = true;

	private String userToken;

	private String userPassword;

	private String ownerToken;

	private String idIteration;

	private String idBase;

	private IntegerNode permissions;

	// file streams etc...
	private File file;

	private RandomAccessFile in;

	public PDFDocument(File file) throws FileNotFoundException, IOException {
		this(file, null);
	}

	public PDFDocument(File file, String userPassword)
			throws FileNotFoundException, IOException {
		this.file = file;
		in = new RandomAccessFile(file, "r");
		this.userPassword = userPassword;
		init();
	}

	/**
	 * all the initialization if done here.
	 * 
	 * @throws IOException
	 */
	private void init() throws IOException {
		in.seek(0);
		readXRefTables();
		readFileInfo();
	}

	public void close() throws IOException {
		in.close();
	}

	public PDFNode readNode() throws IOException {
		return PDFUtil.readNodes(in, 1);
	}

	public ObjectNode readObject(int id, int version) throws IOException {
		CrosReferenceNode ref = getObjectRef(id, version);
		ObjectNode found = null;
		if (ref != null) {
			in.seek(ref.getOffset());
			found = PDFUtil.readObject(in);
		}
		return found;
	}

	public DictionaryNode getInfo() {
		return info;
	}

	public DictionaryNode getEncryption() {
		return encrypt;
	}

	public DictionaryNode getDocCatalog() {
		return root;
	}

	public void readFileInfo() throws IOException {
		// get the document version.
		in.seek(0);
		version = ((CommentNode) readNode()).getStringValue();

		// DOC ID stuff.
		PDFNode id = ((TrailerNode) trailers.get(0)).getDictionary().get("ID");

		if (id instanceof ReferenceNode) {
			id = readObject(((ReferenceNode) id).getId(), ((ReferenceNode) id)
					.getVersion());
		}

		if ((id != null) && (id instanceof ObjectNode)) {
			id = ((ObjectNode) id).getValue();
		}

		if ((id != null) && (id instanceof ArrayNode) &&
			((ArrayNode)id).size() >= 2) {
			// older versions of pdfs (<=1.2)may not have an id in the
			// trailer...
			Object firstElem = ((ArrayNode)id).get(0);
			Object secondElem = ((ArrayNode)id).get(1);
			
			
			if(firstElem instanceof HEXStringNode && secondElem instanceof HEXStringNode) {
				idBase = ((HEXStringNode) firstElem).getOriginal()+ "";
				idIteration = ((HEXStringNode) secondElem).getOriginal() + "";
			}
		}

		// find the most recent trailer block with encrypt/info & root...;
		ReferenceNode infoRef = null;
		ReferenceNode rootRef = null;
		ReferenceNode cryptRef = null;
		for (int i = 0; i < trailers.size(); i++) {
			TrailerNode tnode = (TrailerNode) trailers.get(i);
			DictionaryNode trailerDict = tnode.getDictionary();
			PDFNode cryptCandidate = trailerDict.get("encrypt");
			PDFNode rootCandidate = trailerDict.get("root");
			PDFNode infoCandidate = trailerDict.get("info");
			if (cryptRef == null) {
				cryptRef = (ReferenceNode) cryptCandidate;
			}
		//	if (rootRef == null) {
			if(rootCandidate != null) 
				rootRef = (ReferenceNode) rootCandidate;
//			}
			if (infoRef == null) {
				infoRef = (ReferenceNode) infoCandidate;
			}
		}
		// and resolve them
		if (cryptRef != null) {
			ObjectNode foundObj = readObject(cryptRef.getId(), cryptRef
					.getVersion());
			encrypt = (DictionaryNode) foundObj.getValue();

			// read some individual values...
			scheme = encrypt.getString("filter");
			int type = ((NumericalNode) encrypt.get("V")).getIntValue();
			switch (type) {
			case 0:
				schemeType = "Unsupported";
				break;
			case 1:
				schemeType = "Standard 40bits";
				break;
			case 2:
				schemeType = "Standard 40+bits";
				break;
			case 3:
				schemeType = "Unpublished";
				break;
			case 4:
				schemeType = "Special";
				break;
			}
			NumericalNode keyLengthNode = (NumericalNode) encrypt.get("Length");
			if (keyLengthNode != null && (type == 2 || type == 3)) {
				keyLength = keyLengthNode.getIntValue();
			}
			permissions = (IntegerNode) encrypt.get("P");
			if (permissions != null) {
				print = permissions.getBit(2);
				readOnly = permissions.getBit(3);
				allowCopy = permissions.getBit(4);
				allowTextNotes = permissions.getBit(5);
			}
			userToken = encrypt.getString("U");
			ownerToken = encrypt.getString("O");

			securityHandler = new StandardSecurityHandler(this);
		} else {
			securityHandler = new NoEncryptionSecurityHandler();
		}

		if (rootRef != null) {
			ObjectNode foundObj = readObject(rootRef.getId(), rootRef
					.getVersion());
			
			root = (DictionaryNode) foundObj.getValue();

			// read individual values...
			pageLayout = root.getString("PageLayout");
			if (pageLayout == null)
				pageLayout = "SinglePage";
			pageMode = root.getString("PageMode");
			if (pageMode == null)
				pageMode = "UseNone";
			hasOutline = root.get("Outlines") != null;
			hasThreads = root.get("Threads") != null;
			hasForms = root.get("AcroForm") != null;
			hasMetadataStream = root.get("Metadata") != null;
			isTagged = root.get("MarkInfo") != null;
			language = root.getString("Lang");
			if (language == null)
				language = "unknown";
			DictionaryNode legal = (DictionaryNode) root.get("Legal");
			if (legal != null) {
				// there is potentially a lot in here - haven't ever seen a PDF
				// with one of these though...
			}
		}
		if (infoRef != null) {
			ObjectNode foundObj = readObject(infoRef.getId(), infoRef
					.getVersion());
			info = (DictionaryNode) foundObj.getValue();

			securityHandler.decrypt(info);

			// read individual values..
			producer = info.getString("Producer");
			creator = info.getString("Creator");
			title = info.getString("Title");
			subject = info.getString("Subject");
			author = info.getString("Author");
			creationDate = info.getDate("CreationDate");
			modificationDate = info.getDate("ModDate");
			trapped = info.getString("trapped");
			if (trapped == null)
				trapped = "Unknown";
			keywords = info.getString("keywords");

		}
	}

	private void readXRefTables() throws IOException {
		in.seek(in.length()); // go to the end of the file...
		int fpos = PDFUtil.seekBackwards(in, "trailer");

		// read the 1st trailer (which is really the one at the END of the
		// file)...
		in.seek(fpos);
		TrailerNode topTnode = (TrailerNode) PDFUtil.readNodes(in, 1);

		// use the trailer info to read the xref table...
		boolean abandonTrailer = false;
		fpos = topTnode.getXRefOffset();
		while (true) {
			in.seek(fpos);
			// all xrefs nodes are followed by a trailer - so read both...
			NodeList xrefAndTrailer = (NodeList) PDFUtil.readNodes(in, 2);
			CrossReferenceTableNode xref = (CrossReferenceTableNode) xrefAndTrailer
					.get(0);

			TrailerNode tnode = null;
			if (xrefAndTrailer.size() == 1) {
				if (abandonTrailer) {
					break; // that's all there is
				}
				tnode = topTnode;
				abandonTrailer = true;
			} else {
				tnode = (TrailerNode) xrefAndTrailer.get(1);
			}

			DictionaryNode dict = tnode.getDictionary();
			xrefs.add(xref);
			trailers.add(tnode);
			// read where the next one is if there is one...
			PDFNode previous = dict.get("prev");

			if (previous != null) {
				fpos = ((IntegerNode) previous).getIntValue();
			} else {
				// there is not another xref table
				break;
			}
		}
	}

	public CrosReferenceNode getObjectRef(int id, int version)
			throws IOException {
		CrosReferenceNode node = null;
		for (int i = 0; i < xrefs.size(); i++) {
			CrossReferenceTableNode candidate = (CrossReferenceTableNode) xrefs
					.get(i);
			CrosReferenceNode candidateXref = candidate.getObjectRef(id,
					version);
			if (candidateXref != null) {
				node = candidateXref;
				break;
			}
		}
		return node;
	}

	public String toString() {
		return version + "" + ", author=" + author + ", producer=" + producer
				+ ", title=" + title + ", subject=" + subject + ", creator="
				+ creator + ", creation date=" + creationDate
				+ ", modified date=" + modificationDate + ", keywords=["
				+ keywords + "]" + ", encrypted=" + isEncrypted();
	}

	private String decrypt(String in, int obj, int version) {
		return in;
	}

	/**
	 * @return The author of the PDF.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return The creation date of the PDF.
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @return The creator of the PDF.
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @return The PDF File.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return The modification date of the PDF.
	 */
	public Date getModificationDate() {
		return modificationDate;
	}

	/**
	 * @return The producer of the PDF.
	 */
	public String getProducer() {
		return producer;
	}

	/**
	 * @return The subject metadata.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return The title metadata.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return The version.
	 */
	public String getVersion() {
		return version;
	}

	public boolean isEncrypted() {
		return encrypt != null;
	}

	/**
	 * @return true if the PDF has forms.
	 */
	public boolean hasForms() {
		return hasForms;
	}

	/**
	 * @return true if the PDF has a metadata stream.
	 */
	public boolean hasMetadataStream() {
		return hasMetadataStream;
	}

	/**
	 * @return True if the PDF has an outline.
	 */
	public boolean hasOutline() {
		return hasOutline;
	}

	/**
	 * @return True if the PDF has threads.
	 */
	public boolean hasThreads() {
		return hasThreads;
	}

	/**
	 * @return True if the PDF is tagged.
	 */
	public boolean isTagged() {
		return isTagged;
	}

	/**
	 * @return The keywords of the PDF.
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @return The language of the PDF.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @return The page layout identifier.
	 */
	public String getPageLayout() {
		return pageLayout;
	}

	/**
	 * @return The page mode value.
	 */
	public String getPageMode() {
		return pageMode;
	}

	/**
	 * @return ?
	 */
	public String getTrapped() {
		return trapped;
	}

	/**
	 * @return ?
	 */
	public int getKeyLength() {
		return keyLength;
	}

	/**
	 * @return ?
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * @return ?
	 */
	public String getSchemeType() {
		return schemeType;
	}

	/**
	 * @return True if you can copy text from this PDF.
	 */
	public boolean allowCopy() {
		return allowCopy;
	}

	/**
	 * @return True if the PDF allows text notes.
	 */
	public boolean allowTextNotes() {
		return allowTextNotes;
	}

	/**
	 * @return true if the PDF can be printed.
	 */
	public boolean allowPrint() {
		return print;
	}

	/**
	 * @return true if the PDF is read only.
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @return true if the PDF has an owner password.
	 */
	public boolean hasOwnerPassword() {
		return ownerToken != null;
	}

	/**
	 * @return True if the PDF has a user password.
	 */
	public boolean hasUserPassword() {
		return (isEncrypted() && userPassword != null);
	}

	/**
	 * @return The ID base.
	 */
	public String getIdBase() {
		return idBase;
	}

	/**
	 * @return The ID iteration.
	 */
	public String getIdIteration() {
		return idIteration;
	}

	
	public int getPermissions() {
		return permissions.getIntValue();
	}

	public byte[] getIdBytes() {
		return PDFUtil.decodeHexByteString(idBase);
	}

	public byte[] getOwnerTokenBytes() {
		return PDFUtil.getISOBytes(ownerToken);
	}

	public byte[] getUserTokenBytes() {
		return PDFUtil.getISOBytes(userToken);
	}

	public String getUserPassword() {
		return userPassword;
	}

	public boolean isKeyStrength128() {
		return keyLength == 128;
	}

}
