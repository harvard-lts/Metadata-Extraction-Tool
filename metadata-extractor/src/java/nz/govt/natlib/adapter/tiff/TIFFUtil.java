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

package nz.govt.natlib.adapter.tiff;

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

public class TIFFUtil {

	public static long getUnitLength(int type) {
		long length = 1;
		switch (type) {
		case 0x1:
			length = 1;
			break;
		case 0x2:
			length = 1;
			break;
		case 0x3:
			length = 2;
			break;
		case 0x4:
			length = 4;
			break;
		case 0x5:
			length = 8;
			break;
		}
		return length;
	}

	public static String getTagName(int tag) {
		String name = null;
		switch (tag) {
		case 0x0FE:
			name = "NewSubfileType";
			break;
		case 0x0FF:
			name = "SubfileType";
			break;
		case 0x100:
			name = "ImageWidth";
			break;
		case 0x101:
			name = "ImageLength";
			break;
		case 0x102:
			name = "BitsPerSample";
			break;
		case 0x103:
			name = "Compression";
			break;
		case 0x106:
			name = "PhotometricInterpretation";
			break;
		case 0x107:
			name = "Threshholding";
			break;
		case 0x108:
			name = "CellWidth";
			break;
		case 0x109:
			name = "CellLength";
			break;
		case 0x10A:
			name = "FillOrder";
			break;
		case 0x10D:
			name = "DocumentName";
			break;
		case 0x10E:
			name = "ImageDescription";
			break;
		case 0x10F:
			name = "Make";
			break;
		case 0x110:
			name = "Model";
			break;
		case 0x111:
			name = "StripOffsets";
			break;
		case 0x112:
			name = "Orientation";
			break;
		case 0x115:
			name = "SamplesPerPixel";
			break;
		case 0x116:
			name = "RowsPerStrip";
			break;
		case 0x117:
			name = "StripByteCounts";
			break;
		case 0x118:
			name = "MinSampleValue";
			break;
		case 0x119:
			name = "MaxSampleValue";
			break;
		case 0x11A:
			name = "XResolution";
			break;
		case 0x11B:
			name = "YResolution";
			break;
		case 0x11C:
			name = "PlanarConfiguration";
			break;
		case 0x11D:
			name = "PageName";
			break;
		case 0x11E:
			name = "XPosition";
			break;
		case 0x11F:
			name = "YPosition";
			break;
		case 0x120:
			name = "FreeOffsets";
			break;
		case 0x121:
			name = "FreeByteCounts";
			break;
		case 0x122:
			name = "GrayResponseUnit";
			break;
		case 0x123:
			name = "GrayResponseCurve";
			break;
		case 0x124:
			name = "t4Options";
			break;
		case 0x125:
			name = "t6Options";
			break;
		case 0x128:
			name = "ResolutionUnit";
			break;
		case 0x129:
			name = "PageNumber";
			break;
		case 0x12D:
			name = "TransferFunction";
			break;
		case 0x131:
			name = "Software";
			break;
		case 0x132:
			name = "DateTime";
			break;
		case 0x13B:
			name = "Artist";
			break;
		case 0x13C:
			name = "HostComputer";
			break;
		case 0x13D:
			name = "Predictor";
			break;
		case 0x13E:
			name = "WhitePoint";
			break;
		case 0x13F:
			name = "PrimaryChromaticities";
			break;
		case 0x140:
			name = "ColorMap";
			break;
		case 0x141:
			name = "HalftoneHints";
			break;
		case 0x142:
			name = "TileWidth";
			break;
		case 0x143:
			name = "TileLength";
			break;
		case 0x144:
			name = "TileOffsets";
			break;
		case 0x145:
			name = "TileByteCounts";
			break;
		case 0x14C:
			name = "InkSet";
			break;
		case 0x14D:
			name = "InkNames";
			break;
		case 0x14E:
			name = "NumberOfInks";
			break;
		case 0x150:
			name = "DotRange";
			break;
		case 0x151:
			name = "TargetPrinter";
			break;
		case 0x152:
			name = "ExtraSamples";
			break;
		case 0x153:
			name = "SampleFormat";
			break;
		case 0x154:
			name = "SMinSampleValue";
			break;
		case 0x155:
			name = "SMaxSampleValue";
			break;
		case 0x156:
			name = "TransferRange";
			break;
		case 0x200:
			name = "JPEGProc";
			break;
		case 0x201:
			name = "JPEGInterchangeFormat";
			break;
		case 0x202:
			name = "JPEGInterchangeFormatLngth";
			break;
		case 0x203:
			name = "JPEGRestartInterval";
			break;
		case 0x205:
			name = "JPEGLosslessPredictors";
			break;
		case 0x206:
			name = "JPEGPointTransforms";
			break;
		case 0x207:
			name = "JPEGQTables";
			break;
		case 0x208:
			name = "JPEGDCTables";
			break;
		case 0x209:
			name = "JPEGACTables";
			break;
		case 0x211:
			name = "YCbCrCoefficients";
			break;
		case 0x212:
			name = "YCbCrSubSampling";
			break;
		case 0x213:
			name = "YCbCrPositioning";
			break;
		case 0x214:
			name = "ReferenceBlackWhite";
			break;
		case 0x8298:
			name = "Copyright";
			break;

		// custom tags that I know of...
		case 32932:
			name = "MSAnnotation";
			break;

		default:
			name = null;
			break;

		/*
		 */
		}
		return name;
	}
}