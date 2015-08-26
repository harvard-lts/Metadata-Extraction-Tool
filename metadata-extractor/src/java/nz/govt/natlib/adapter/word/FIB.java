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

package nz.govt.natlib.adapter.word;

import java.io.IOException;

import nz.govt.natlib.fx.BitFieldUtil;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;

/**
 * @author unascribed
 * @version 1.0
 */
public class FIB extends Element implements FIBConstants {

	private int dopOffset;

	private LanguageMap langMap;

	private FilePosition fp = new FilePosition();

	public FIB(LanguageMap langMap, int dopOffset) {
		this.langMap = langMap;
		this.dopOffset = dopOffset;
	}

	// a flexible variable length record that delves into other parts of the
	// file.
	public void read(DataSource ftk, ParserContext ctx) throws IOException {
		ftk.setPosition(FIBConstants.oLid);
		int lid = (int) FXUtil.getNumericalValue(ftk,
				IntegerElement.SHORT_SIZE, false);
		ftk.setPosition(FIBConstants.oDocDesc);
		short docDesc = (short) FXUtil.getNumericalValue(ftk,
				IntegerElement.SHORT_SIZE, false);
		boolean isTemplate = BitFieldUtil.isSet(docDesc, 0x0001);
		boolean isGlossary = BitFieldUtil.isSet(docDesc, 0x0002);
		boolean isComplex = BitFieldUtil.isSet(docDesc, 0x0004);
		boolean hasPictures = BitFieldUtil.isSet(docDesc, 0x0008);
		boolean isEncrypted = BitFieldUtil.isSet(docDesc, 0x0100);
		int iTextPos = fp.getFP(ftk, FIBConstants.oTextPos);
		int iEndTextPos = fp.getFP(ftk, FIBConstants.oLastText);
		int iDop = fp.getFP(ftk, dopOffset);

		// output the results...
		ctx.fireParseEvent("lid", Integer.toHexString(lid));
		ctx.fireParseEvent("lang", langMap.get(lid));
		ctx.fireParseEvent("dopOffset", iDop);
		// ctx.fireParseEvent("lidFE",lidFE);
		ctx.fireParseEvent("isTemplate", isTemplate);
		ctx.fireParseEvent("isGlossary", isGlossary);
		ctx.fireParseEvent("hasPictures", hasPictures);
		ctx.fireParseEvent("isComplex", isComplex);
		ctx.fireParseEvent("isEncrypted", isEncrypted);

	}

}