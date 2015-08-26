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

package nz.govt.natlib.adapter.exif;

/**
 * @author Nic Evans
 * @version 1.0
 */

public class EXIFUtil {

	public static String getTagName(int tag) {
		String name = getTIFFTagName(tag);

		if (name == null) {
			switch (tag) {
			case 0x829a:
				name = "ExposureTime";
				break;
			case 0x829d:
				name = "FNumber";
				break;
			case 0x8822:
				name = "ExposureProgram";
				break;
			case 0x8827:
				name = "ISOSpeedRatings";
				break;
			case 0x9000:
				name = "ExifVersion";
				break;
			case 0x9003:
				name = "DateTimeOriginal";
				break;
			case 0x9004:
				name = "DateTimeDigitized";
				break;
			case 0x9101:
				name = "ComponentConfiguration";
				break;
			case 0x9102:
				name = "CompressedBitsPerPixel";
				break;
			case 0x9201:
				name = "ShutterSpeedValue";
				break;
			case 0x9202:
				name = "ApertureValue";
				break;
			case 0x9203:
				name = "BrightnessValue";
				break;
			case 0x9204:
				name = "ExposureBiasValue";
				break;
			case 0x9205:
				name = "MaxApertureValue";
				break;
			case 0x9206:
				name = "SubjectDistance";
				break;
			case 0x9207:
				name = "MeteringMode";
				break;
			case 0x9208:
				name = "LightSource";
				break;
			case 0x9209:
				name = "Flash";
				break;
			case 0x920a:
				name = "FocalLength";
				break;
			case 0x927c:
				name = "MakerNote";
				break;
			case 0x9286:
				name = "UserComment";
				break;
			case 0xa000:
				name = "FlashPixVersion";
				break;
			case 0xa001:
				name = "ColorSpace";
				break;
			case 0xa002:
				name = "ExifImageWidth";
				break;
			case 0xa003:
				name = "ExifImageHeight";
				break;
			case 0xa004:
				name = "RelatedSoundFile";
				break;
			case 0xa005:
				name = "ExifInteroperabilityOffset";
				break;
			case 0xa20e:
				name = "FocalPlaneXResolution";
				break;
			case 0xa20f:
				name = "FocalPlaneYResolution";
				break;
			case 0xa210:
				name = "FocalPlaneResolutionUnit";
				break;
			case 0xa217:
				name = "SensingMethod";
				break;
			case 0xa300:
				name = "FileSource";
				break;
			case 0xa301:
				name = "SceneType";
				break;
			case 0x00fe:
				name = "NewSubfileType";
				break;
			case 0x00ff:
				name = "SubfileTypexx";
				break;
			case 0x012d:
				name = "TransferFunction";
				break;
			case 0x013b:
				name = "Artist";
				break;
			case 0x013d:
				name = "Predictor";
				break;
			case 0x0142:
				name = "TileWidth";
				break;
			case 0x0143:
				name = "TileLength";
				break;
			case 0x0144:
				name = "TileOffsets";
				break;
			case 0x0145:
				name = "TileByteCounts";
				break;
			case 0x014a:
				name = "SubIFDs";
				break;
			case 0x015b:
				name = "JPEGTables";
				break;
			case 0x828d:
				name = "CFARepeatPatternDim";
				break;
			case 0x828e:
				name = "CFAPattern";
				break;
			case 0x828f:
				name = "BatteryLevel";
				break;
			case 0x83bb:
				name = "IPTC_NAA";
				break;
			case 0x8773:
				name = "InterColorProfile";
				break;
			case 0x8824:
				name = "SpectralSensitivity";
				break;
			case 0x8825:
				name = "GPSInfo";
				break;
			case 0x8828:
				name = "OECF";
				break;
			case 0x8829:
				name = "Interlace";
				break;
			case 0x882a:
				name = "TimeZoneOffset";
				break;
			case 0x882b:
				name = "SelfTimerMode";
				break;
			case 0x920b:
				name = "FlashEnergy";
				break;
			case 0x920c:
				name = "SpatialFrequencyResponse";
				break;
			case 0x920d:
				name = "Noise";
				break;
			case 0x9211:
				name = "ImageNumber";
				break;
			case 0x9212:
				name = "SecurityClassification";
				break;
			case 0x9213:
				name = "ImageHistory";
				break;
			case 0x9214:
				name = "SubjectLocation";
				break;
			case 0x9215:
				name = "ExposureIndex";
				break;
			case 0x9216:
				name = "TIFF_EPStandardID";
				break;
			case 0x9290:
				name = "SubSecTime";
				break;
			case 0x9291:
				name = "SubSecTimeOriginal";
				break;
			case 0x9292:
				name = "SubSecTimeDigitized";
				break;
			case 0xa20b:
				name = "FlashEnergy";
				break;
			case 0xa20c:
				name = "SpatialFrequencyResponse";
				break;
			case 0xa214:
				name = "SubjectLocation";
				break;
			case 0xa215:
				name = "ExposureIndex";
				break;
			case 0xa302:
				name = "CFAPattern";
				break;

			// some canon ones...
			case 0x6:
				name = "Canon_ImageType";
				break;
			case 0x7:
				name = "Canon_Firmware";
				break;
			case 0x9:
				name = "Canon_Owner";
				break;
			case 0x8:
				name = "Unknown";
				break;
			case 0x10:
				name = "Unknown";
				break;
			case 0xa401:
				name = "Unknown";
				break;
			case 0xa402:
				name = "Unknown";
				break;
			case 0xa403:
				name = "Unknown";
				break;
			case 0xa404:
				name = "Unknown";
				break;
			case 0xa406:
				name = "Unknown";
				break;

			// some olympus ones
			case 0x0200:
				name = "Olympus_SpecialMode";
				break;
			case 0x0201:
				name = "Olympus_JpegQual";
				break;
			case 0x0202:
				name = "Olympus_Macro";
				break;
			case 0x0203:
				name = "Olympus_Unknown";
				break;
			case 0x0204:
				name = "Olympus_DigiZoom";
				break;
			case 0x0205:
				name = "Olympus_Unknown";
				break;
			case 0x0206:
				name = "Olympus_Unknown";
				break;
			case 0x0207:
				name = "Olympus_SoftwareRelease";
				break;
			case 0x0208:
				name = "Olympus_PictInfo";
				break;
			case 0x0209:
				name = "Olympus_CameraID";
				break;
			case 0x0f00:
				name = "Olympus_DataDump";
				break;

			}
		}
		return name;
	}

	public static String getTIFFTagName(int tag) {
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