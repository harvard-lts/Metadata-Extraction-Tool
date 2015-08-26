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

package nz.govt.natlib.adapter.word.word2;

import java.io.File;
import java.io.IOException;

import nz.govt.natlib.adapter.word.FIB;
import nz.govt.natlib.adapter.word.WordUtils;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FileDataSource;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.fx.PascalStringElement;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class Word2Adapter {
	// File position offset to the table of strings holding the Summary
	// information
	private static final int Offset_To_sttbfAssoc = 0x118;

	// File position offset to the Document Properties
	private static final int Offset_To_DOP = 0x112;

	private PascalStringElement tmpStringElm = new PascalStringElement();

	public boolean acceptsFile(File file, boolean ignoreFileExtension) {

		// BUGFIX 8-Jun-04
		if (file.length() == 0) {
			return false;
		}
		
		String name = file.getName().toLowerCase();
		if (ignoreFileExtension || (WordUtils.isDocFile(file) == true)) {
			try {
				return WordUtils.isWord2(file);
			} catch (Exception ex) {
				throw new RuntimeException("Word2Adpator:acceptsFile: "
						+ ex.getMessage());
			}
		}
		return false;
	}

	public void process(File file, ParserContext ctx) throws IOException {
		DataSource ftk = new FileDataSource(file);
		FIB pFib = new FIB(Word2LanguageMap.getLanguageMap(),
				Word2Adapter.Offset_To_DOP);

		ctx.fireStartParseEvent("header");
		WordUtils.getHeader(ftk, ctx);
		ctx.fireEndParseEvent("header");

		ctx.fireStartParseEvent("FIB");
		pFib.read(ftk, ctx);
		ctx.fireEndParseEvent("FIB");

		// Check fib is good
		ftk.setPosition(Offset_To_sttbfAssoc);
		int iSttbAssocFP = (int) FXUtil.getNumericalValue(ftk,
				IntegerElement.INT_SIZE, false);

		// Set position in file to the summary strings start
		// Skip first bytes
		ftk.setPosition(iSttbAssocFP + 2);

		String assocFileNext = FXUtil.getPascalStringValue(ftk);
		String assocDot = FXUtil.getPascalStringValue(ftk);
		String assocTitle = FXUtil.getPascalStringValue(ftk);
		String subject = FXUtil.getPascalStringValue(ftk);
		String AssocKeysWords = FXUtil.getPascalStringValue(ftk);
		String Comments = FXUtil.getPascalStringValue(ftk);
		String Author = FXUtil.getPascalStringValue(ftk);
		String LastRevBy = FXUtil.getPascalStringValue(ftk);

		ctx.fireStartParseEvent("summary");
		ctx.fireParseEvent("template", assocDot);
		ctx.fireParseEvent("title", assocTitle);
		ctx.fireParseEvent("subject", subject);
		ctx.fireParseEvent("keywords", AssocKeysWords);
		ctx.fireParseEvent("comments", Comments);
		ctx.fireParseEvent("lastReviewedBy", LastRevBy);
		ctx.fireParseEvent("author", Author);
		ctx.fireEndParseEvent("summary");

		ctx.fireStartParseEvent("Properties");
		long dopPos = ctx.getIntAttribute("Word.FIB.dopOffset");
		WordUtils.getDOPProperties(ftk, (int) dopPos, ctx);
		ctx.fireParseEvent("Application", "Word 2.0");
		ctx.fireEndParseEvent("Properties");
		ftk.close();
	}

}