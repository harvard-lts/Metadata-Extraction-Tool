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

package nz.govt.natlib.adapter.jpg;

/**
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class JpgUtil {

	public static String getJpgMarkerName(long delim, long type) {
		String name = "";
		if (delim == 0xFF) {
			switch ((int) type) {
			case 0x01:
				name = "TEM";
				break;
			case 0xc0:
				name = "StartOfFrame0";
				break;
			case 0xc1:
				name = "StartOfFrame1";
				break;
			case 0xc2:
				name = "StartOfFrame2";
				break;
			case 0xc3:
				name = "StartOfFrame3";
				break;
			case 0xc5:
				name = "StartOfFrame5";
				break;
			case 0xc6:
				name = "StartOfFrame6";
				break;
			case 0xc7:
				name = "StartOfFrame7";
				break;
			case 0xc9:
				name = "StartOfFrame9";
				break;
			case 0xca:
				name = "StartOfFrame10";
				break;
			case 0xcb:
				name = "StartOfFrame11";
				break;
			case 0xcd:
				name = "StartOfFrame13";
				break;
			case 0xce:
				name = "StartOfFrame14";
				break;
			case 0xcf:
				name = "StartOfFrame15";
				break;
			case 0xc4:
				name = "DefineHuffmanTable";
				break;
			case 0xc8:
				name = "JPG";
				break;
			case 0xcc:
				name = "DefineArithmeticTable";
				break;
			case 0xd0:
				name = "Resynch0";
				break;
			case 0xd1:
				name = "Resynch1";
				break;
			case 0xd2:
				name = "Resynch2";
				break;
			case 0xd3:
				name = "Resynch3";
				break;
			case 0xd4:
				name = "Resynch4";
				break;
			case 0xd5:
				name = "Resynch5";
				break;
			case 0xd6:
				name = "Resynch6";
				break;
			case 0xd7:
				name = "Resynch7";
				break;
			case 0xd8:
				name = "StartOfImage";
				break;
			case 0xd9:
				name = "EndOfImage";
				break;
			case 0xda:
				name = "StartOfScan";
				break;
			case 0xdb:
				name = "DefineQuantizationTable";
				break;
			case 0xdc:
				name = "DNL";
				break;
			case 0xdd:
				name = "DefineRestartInterval";
				break;
			case 0xde:
				name = "DHP";
				break;
			case 0xdf:
				name = "EXP";
				break;
			case 0xf0:
				name = "JPG0";
				break;
			case 0xf1:
				name = "JPG1";
				break;
			case 0xf2:
				name = "JPG2";
				break;
			case 0xf3:
				name = "JPG3";
				break;
			case 0xf4:
				name = "JPG4";
				break;
			case 0xf5:
				name = "JPG5";
				break;
			case 0xf6:
				name = "JPG6";
				break;
			case 0xf7:
				name = "JPG7";
				break;
			case 0xf8:
				name = "JPG8";
				break;
			case 0xf9:
				name = "JPG9";
				break;
			case 0xfa:
				name = "JPG10";
				break;
			case 0xfb:
				name = "JPG11";
				break;
			case 0xfc:
				name = "JPG12";
				break;
			case 0xfd:
				name = "JPG13";
				break;
			case 0xfe:
				name = "COM";
				break;
			case 0xe0:
				name = "APP0";
				break;
			case 0xe1:
				name = "APP1";
				break;
			case 0xe2:
				name = "APP2";
				break;
			case 0xe3:
				name = "APP3";
				break;
			case 0xe4:
				name = "APP4";
				break;
			case 0xe5:
				name = "APP5";
				break;
			case 0xe6:
				name = "APP6";
				break;
			case 0xe7:
				name = "APP7";
				break;
			case 0xe8:
				name = "APP8";
				break;
			case 0xe9:
				name = "APP9";
				break;
			case 0xea:
				name = "APP10";
				break;
			case 0xeb:
				name = "APP11";
				break;
			case 0xec:
				name = "APP12";
				break;
			case 0xed:
				name = "APP13";
				break;
			case 0xee:
				name = "APP14";
				break;
			case 0xef:
				name = "APP15";
				break;
			}
		} else {
			name = "not a JPEG Marker";
		}
		return name;
	}

}