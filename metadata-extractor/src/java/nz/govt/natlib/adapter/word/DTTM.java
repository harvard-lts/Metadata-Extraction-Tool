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
import java.util.Calendar;

import nz.govt.natlib.fx.BitFieldUtil;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;

/**
 * Class for the DTTM element inside a word document.
 * 
 * @author unascribed
 * @version 1.0
 */
public class DTTM extends Element {
	private static final int minMask = 0x003F;

	private static final int hrMask = 0x07C0;

	private static final int dayMask = 0xF800;

	private static final int yrMask = 0x1FF0;

	private static final int monMask = 0x000F;;

	private static final String NOT_SPECIFIED = "";

	public DTTM() {
	}

	public void read(DataSource data, ParserContext ctx) throws IOException {
		int idate = (int) FXUtil.getNumericalValue(data,
				IntegerElement.SHORT_SIZE, false);
		int minutes = BitFieldUtil.getValue(idate, minMask);
		int hours = BitFieldUtil.getValue(idate, hrMask);
		int day = BitFieldUtil.getValue(idate, dayMask);

		idate = (int) FXUtil.getNumericalValue(data, IntegerElement.SHORT_SIZE,
				false);
		int month = BitFieldUtil.getValue(idate, monMask);
		int tmp = yrMask & idate;
		int year = BitFieldUtil.getValue(idate, yrMask);
		// Not filled in if all values are =0
		if (year == 0 && month == 0 && day == 0) {
			ctx.fireParseEvent(NOT_SPECIFIED);
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.YEAR, (year + 1900));
		cal.set(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, minutes);

		ctx.fireParseEvent(cal.getTime());
	}
}