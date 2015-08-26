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
package nz.govt.natlib.adapter.word;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.adapter.word.OLE.WordOLEAdapter;
import nz.govt.natlib.adapter.word.word2.Word2Adapter;
import nz.govt.natlib.fx.ParserContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aspose.words.Document;
import com.aspose.words.FieldStart;
import com.aspose.words.FieldType;
import com.aspose.words.NodeList;
import com.aspose.words.UnsupportedFileFormatException;

/**
 * @author nevans
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WordAdapter extends DataAdapter {

	// knows about two kinds of word file...
	WordOLEAdapter oleAdapter = new WordOLEAdapter();

	Word2Adapter word2Adapter = new Word2Adapter();

	private final static Log LOG = LogFactory.getLog(WordAdapter.class);

	/**
	 * @see nz.govt.natlib.adapter.DataAdapter#acceptsFile(java.io.File)
	 */
	public boolean acceptsFile(File file) {
		return oleAdapter.acceptsFile(file, ignoreFileExtension)
				|| word2Adapter.acceptsFile(file, ignoreFileExtension);
	}

	public String getName() {
		return "Microsoft Word Adapter";
	}

	public String getDescription() {
		return "Adapts all Microsoft Word files from version 2.0 to XP/2003";
	}

	public String getVersion() {
		return "3.2";
	}

	/**
	 * @see nz.govt.natlib.adapter.DataAdapter#adapt(java.io.File,
	 *      nz.govt.natlib.fx.ParserContext)
	 */
	public void adapt(File file, ParserContext ctx) throws IOException {
		boolean word2 = word2Adapter.acceptsFile(file, ignoreFileExtension);
		boolean wordOLE = oleAdapter.acceptsFile(file, ignoreFileExtension);
		
		if (word2 || wordOLE) {
			ctx.fireStartParseEvent("Word");
			writeFileInfo(file, ctx);
			
			try {
				if (word2) {
					ctx.fireParseEvent("Version", "2.0");
					word2Adapter.process(file, ctx);
				}
				// it's an else just in case they both think they can do it or some reason (shouldn't happen)
				else if (wordOLE) {
					ctx.fireParseEvent("Version", "OLE");
					oleAdapter.process(file, ctx);
				}

				// Adding additional properties for a word document using 'ASPOSE'
				Document doc = new Document(file.getAbsolutePath());
				ctx.fireStartParseEvent("Properties");
				// ctx.fireParseEvent("application", doc.getBuiltInDocumentProperties().getNameOfApplication());
				ctx.fireParseEvent("editTime", doc.getBuiltInDocumentProperties().getTotalEditingTime());
				ctx.fireParseEvent("security", doc.getBuiltInDocumentProperties().getSecurity());
				ctx.fireParseEvent("company", doc.getBuiltInDocumentProperties().getCompany());
				ctx.fireParseEvent("lines", doc.getBuiltInDocumentProperties().getLines());
				ctx.fireParseEvent("paragraphs", doc.getBuiltInDocumentProperties().getParagraphs());
				ctx.fireParseEvent("characterswithspaces", doc.getBuiltInDocumentProperties().getCharactersWithSpaces());
				ctx.fireParseEvent("linksUpToDate", doc.getBuiltInDocumentProperties().getLinksUpToDate());

				ArrayList<Object> titleOfPartsList = new ArrayList<Object>();
				for (Object y : doc.getBuiltInDocumentProperties().getTitlesOfParts()) {
					titleOfPartsList.add(y);
				}
				String titleOfParts = StringUtils.join(titleOfPartsList, " ");
				titleOfParts = Arrays.asList(titleOfParts).toString().substring(1).replaceFirst("]", "");
				ctx.fireParseEvent("titleOfParts", titleOfParts);

				ArrayList<Object> headingPairsList = new ArrayList<Object>();
				for (Object x : doc.getBuiltInDocumentProperties().getHeadingPairs()) {
					headingPairsList.add(x);
				}
				String headingPairs = StringUtils.join(headingPairsList, ", ");
				headingPairs = Arrays.asList(headingPairs).toString().substring(1).replaceFirst("]", "");
				ctx.fireParseEvent("headingPairs", headingPairs);

				NodeList<FieldStart> fieldStarts = doc.selectNodes("//FieldStart");
				int count = 0;
				boolean isHyperlink = false;
				for (FieldStart fieldStart : (Iterable<FieldStart>) fieldStarts) {
					if (fieldStart.getFieldType() == FieldType.FIELD_HYPERLINK) {
						count++;
					}
				}
				if (count > 0) {
					isHyperlink = true;
				}
				ctx.fireParseEvent("hyperlinks", isHyperlink);
				// ctx.fireParseEvent("tagPidGuid",
				// doc.getCustomDocumentProperties().get("_PID_GUID"));
				ctx.fireEndParseEvent("Properties");

			} catch (UnsupportedFileFormatException e) {
				// Aspose doesn't support pre-Word97 format documents and will throw UnsupportedFileFormatException and this catch block will handle that exception.
				LOG.warn("Some additional properties may not be extracted because " + e.getMessage());
			} catch (Exception ex) {
				LOG.error("Error extracting WORD properties.");
				throw new IOException("Word Adapter couldn't extract metadata: - " + ex.getMessage(), ex);
			}

			ctx.fireEndParseEvent("Word");
		} else {
			throw new RuntimeException("Word Adapter cannot adapt this file " + file);
		}
	}

	public String getOutputType() {
		return "word.dtd";
	}

	public String getInputType() {
		return "application/ms-word";
	}

	/*public static void main(String args[]) {
		try {
			File testFile = new File("C:\\AppDev\\NDHA\\MetaDataExtractor\\Test Files\\Office extractor\\Word\\V1-FL745325.doc");
			ParserContext ctx = new ParserContext();
			WordAdapter wordAdapter = new WordAdapter();

			System.out.println("Extracting WORD properties from Word Adapter....\n");

			wordAdapter.adapt(testFile, ctx);
			ctx.printAttributes();

			System.out.println("\n ####END####");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

}
