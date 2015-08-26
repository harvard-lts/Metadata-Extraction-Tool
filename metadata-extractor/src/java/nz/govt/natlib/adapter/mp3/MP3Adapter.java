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

package nz.govt.natlib.adapter.mp3;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.BitFieldUtil;
import nz.govt.natlib.fx.CompoundElement;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.EnumeratedElement;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FileDataSource;
import nz.govt.natlib.fx.FixedLengthStringElement;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;

/**
 * Adapter for MP3 Audio Files.
 * 
 * @author unascribed
 * @version 1.0
 */

public class MP3Adapter extends DataAdapter {

	private Element genre = new EnumeratedElement(new IntegerElement(
			IntegerElement.BYTE_SIZE, false, IntegerElement.DECIMAL_FORMAT),
			new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
					"10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
					"20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
					"30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
					"40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
					"50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
					"60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
					"70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
					"80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
					"90", "91", "92", "93", "94", "95", "96", "97", "98", "99",
					"100", "101", "102", "103", "104", "105", "106", "107",
					"108", "109", "110", "111", "112", "113", "114", "115",
					"116", "117", "118", "119", "120", "121", "122", "123",
					"124", "125" }, new String[] { "Blues", "Classic Rock",
					"Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop",
					"Jazz", "Metal", "New Age", "Oldies", "Other", "Pop",
					"R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial",
					"Alternative", "Ska", "Death Metal", "Pranks",
					"Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop",
					"Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical",
					"Instrumental", "Acid", "House", "Game", "Sound Clip",
					"Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk",
					"Space", "Meditative", "Instrumental Pop",
					"Instrumental Rock", "Ethnic", "Gothic", "Darkwave",
					"Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance",
					"Dream", "Southern Rock", "Comedy", "Cult", "Gangsta",
					"Top 40", "Christian Rap", "Pop/Funk", "Jungle",
					"Native American", "Cabaret", "New Wave", "Psychadelic",
					"Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal",
					"Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical",
					"Rock & Roll", "Hard Rock", "Folk", "Folk-Rock",
					"National Folk", "Swing", "Fast Fusion", "Bebob", "Latin",
					"Revival", "Celtic", "Bluegrass", "Avantgarde",
					"Gothic Rock", "Progressive Rock", "Psychedelic Rock",
					"Symphonic Rock", "Slow Rock", "Big Band", "Chorus",
					"Easy Listening", "Acoustic", "Humour", "Speech",
					"Chanson", "Opera", "Chamber Music", "Sonata", "Symphony",
					"Booty Brass", "Primus", "Porn Groove", "Satire",
					"Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad",
					"Poweer Ballad", "Rhytmic Soul", "Freestyle", "Duet",
					"Punk Rock", "Drum Solo", "A Capela", "Euro-House",
					"Dance Hall" }, "unknown");

	// ID3v1 Tag
	private Element id3v1Element = new CompoundElement(new String[] { "title",
			"artist", "album", "year", "comment", "genre" }, new Element[] {
			new FixedLengthStringElement(30), new FixedLengthStringElement(30),
			new FixedLengthStringElement(30), new FixedLengthStringElement(4),
			new FixedLengthStringElement(30), genre, });

	// ID3v1.1 Tag
	private Element id3v11Element = new CompoundElement(new String[] { "title",
			"artist", "album", "year", "comment", "track", "genre" },
			new Element[] {
					new FixedLengthStringElement(30),
					new FixedLengthStringElement(30),
					new FixedLengthStringElement(30),
					new FixedLengthStringElement(4),
					new FixedLengthStringElement(29),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT), genre, });

	/*
	 * The Sync Safe Int logic doesn't seem to work for certain frame types,
	 * such as an embedded picture (frame with "APIC" frame type) or 
	 * the Music CD info ("MCDI" frame type). When we do a sync safe int
	 * conversion on the frame size for these frames, the frame size becomes 
	 * negative and a NegativeArraySizeException is thrown from the application.
	 * 
	 * The list below defines the frame types that shouldn't do a SyncsafeInt 
	 * conversion on the frame size.
	 * 
	 * Note: If certain MP3 files fail the metadata extraction giving a
	 * NegativeArraySizeException or a NullPointerException, it may be due the
	 * fact that certain frame types in the MP3 file don't require/support the
	 * sync safe int conversion of frame size. If so, add those frame types 
	 * to the list below.
	 */
	private final List frameTypesToIgnoreSyncSafeInt = Arrays.asList(new String[]{
		"APIC",
		"MCDI",
	});
	
	public boolean acceptsFile(File file) {
		String name = file.getName().toLowerCase();
		if (ignoreFileExtension || name.endsWith(".mp3")) {
			return true;
		}

		return false;
	}

	public String getVersion() {
		return "1.0";
	}

	public String getOutputType() {
		return "mp3.dtd";
	}

	public String getInputType() {
		return "audio/mpeg";
	}

	public String getName() {
		return "Mp3 Audio Adapter";
	}

	public String getDescription() {
		return "Adapts Mp3 Audio files including ID3v1 and ID3v2 tags";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		long SECS = 1000;
		long MINS = 60 * SECS;
		long HRS = 60 * MINS;
		// Add the MetaData to the tree!
		DataSource ftk = new FileDataSource(file);
		ctx.fireStartParseEvent("mp3");
		writeFileInfo(file, ctx);
		try {
			boolean headFound = false;
			int sizeOfID3Metadata = 0;
			// read the ID3v2.x tag...
			ftk.setPosition(0);
			String id3 = FXUtil.getFixedStringValue(ftk, 3);
			if (id3.equalsIgnoreCase("ID3")) {
				headFound = true;
				ctx.fireStartParseEvent("header");
				byte[] ver = ftk.getData(1);
				byte[] subVer = ftk.getData(1);
				byte[] fg = ftk.getData(1);
				byte[] s = ftk.getData(4);
				int version = (int) FXUtil.getNumericalValue(ver, true);
				int subVersion = (int) FXUtil.getNumericalValue(subVer, true);
				int flags = (int) FXUtil.getNumericalValue(fg, true);
				int size = (int) FXUtil.getNumericalValue(s, true);
				boolean unsync = BitFieldUtil.isSet(flags, 64);
				boolean ext = BitFieldUtil.isSet(flags, 32);
				boolean exp = BitFieldUtil.isSet(flags, 16);
				boolean foot = BitFieldUtil.isSet(flags, 8);
				size = getSyncsafeInt(size);

				ctx.fireParseEvent("tag-version", "ID3v2." + version + "."
						+ subVersion);

				// if there is an extended header then that needs to be read...
				if (ext) {
					byte[] extS = ftk.getData(4);
					int extSize = (int) FXUtil.getNumericalValue(extS, true);
					extSize = getSyncsafeInt(extSize);
					// move past the crap...
					ftk.setPosition(ftk.getPosition() + extSize - 4);
				}

				// allow for the footer to be present
				if (foot) {
					size -= 10;
				}

				// now read the frames...
				int read = 0;
				while (read < size) {
					String frameType = FXUtil.getFixedStringValue(ftk, 4);

					/*
					 * Raghu Pushpakath: As per ID3 spec, It is permitted to
					 * include padding after all the final frame (at the end of
					 * the ID3 tag), making the size of all the frames together
					 * smaller than the size given in the head of the tag.
					 * 
					 * The following code is added in order to skip these
					 * paddings, thus preventing the
					 * java.lang.NegativeArraySizeException for certain MP3
					 * files.
					 */
					if (frameType == null || frameType.trim().length() <= 0) {
						// Skip 10 bytes (4 for the frame name, 4 for frame size and 2 for frame flags
						read += 10;
						// Continue with reading next set of bytes
						continue;
					}
					byte[] fS = ftk.getData(4);
					byte[] frameFlags = ftk.getData(2);
					int frameSize = (int) FXUtil.getNumericalValue(fS, true);
					
					/*
					 * Check whether a sync-safe int conversion to be done on the
					 * frame size. Do the conversion only if the frame type is not
					 * defined in the frameTypesToIgnoreSyncSafeInt list.
					 */
					if (frameTypesToIgnoreSyncSafeInt.contains(frameType) == false)
						frameSize = getSyncsafeInt(frameSize);
					String data = FXUtil.getFixedStringValue(ftk, frameSize);
					ID3v2Tag v2tag = MP3Util.getTag(frameType, data);
					if (v2tag != null) {
						HashMap map = v2tag.getMap();
						if (map != null) {
							ctx.fireParseEvent(v2tag.getName(), v2tag
									.getValue(), false, map);
						} else {
							ctx.fireParseEvent(v2tag.getName(), v2tag
									.getValue());
						}
					}
					read += frameSize + 10;
				}

				ctx.fireEndParseEvent("header");
				sizeOfID3Metadata =  size;
				//System.out.println("Total size of ID3 Frame: " + sizeOfID3Metadata);
			}

			// read the ID3v1.x tag...
			if (!headFound) {
				ftk.setPosition(file.length() - 128);
				String tag = FXUtil.getFixedStringValue(ftk, 3);
				if (tag.equalsIgnoreCase("TAG")) {
					ctx.fireStartParseEvent("header");
					ctx.fireParseEvent("tag-version", "ID3v1.x");
					ctx.fireParseEvent("version", "1.x");
					id3v11Element.read(ftk, ctx);
					ctx.fireEndParseEvent("header");
					headFound = true;
				}
			}

			// what to do if headFound = false?
			if (!headFound) {
				ctx.fireStartParseEvent("header");
				ctx.fireStartParseEvent("!-- no header found--");
				ctx.fireEndParseEvent("header");
			}

			// now scan the music file - as a stream - to find a frame start
			// index point
			// MP3's can be cut in half and still work, like worms!
			boolean seek = true;
			int seekTo = sizeOfID3Metadata; // End of ID3 metadata and start of Mp3 header
			byte previous = 0x00;
			byte current = 0x00;
			while (seekTo < file.length() && seek) {
				ftk.setPosition(seekTo);
				byte[] c = ftk.getData(1024);
				for (int i = 0; i < c.length; i++) {
					previous = current;
					current = c[i];
					seekTo++;
					if (previous == (byte) 0xFF) {
						// posible frame buffer index point...
						// if the current has the most significant 3 bits set
						// then we are in!
						if ((current & 0xE0) == 0xE0) {
							seekTo -= 2;
							seek = false;
							break;
						}
					}
				}
			}
			// the above line finds the next sync index point - now we are good
			// to go!
			ftk.setPosition(seekTo);
			int fsync = (int) FXUtil.getNumericalValue(ftk, 1, false);
			int type = (int) FXUtil.getNumericalValue(ftk, 1, false);
			int version = BitFieldUtil.getNumber(type, 5, 4);
			int layer = BitFieldUtil.getNumber(type, 3, 2);
			boolean pro = BitFieldUtil.isSet(type, 0x01);
			int bitRate = (int) FXUtil.getNumericalValue(ftk, 1, false);
			int bitRateIndex = BitFieldUtil.getNumber(bitRate, 8, 5);
			int realBitRate = getBitRate(version, layer, bitRateIndex);
			int sampleRateIndex = BitFieldUtil.getNumber(bitRate, 4, 3);
			int realSampleRate = getSampleRate(version, layer, sampleRateIndex);
			boolean pad = BitFieldUtil.isSet(bitRate, 0x02);
			boolean priv = BitFieldUtil.isSet(bitRate, 0x01);
			int mode = (int) FXUtil.getNumericalValue(ftk, 1, false);
			int channels = BitFieldUtil.getNumber(mode, 8, 7);
			int modeExt = BitFieldUtil.getNumber(mode, 6, 5);
			boolean copyright = BitFieldUtil.isSet(mode, 0x08);
			boolean original = BitFieldUtil.isSet(mode, 0x04);
			int emph = BitFieldUtil.getNumber(mode, 2, 1);
			long durationMs = getDuration(realBitRate, realSampleRate,
					channels, file.length());
			long durationHrs = durationMs / HRS;
			long durationMins = (durationMs - (durationHrs * HRS)) / MINS;
			long durationSecs = (durationMs - (durationHrs * HRS) - (durationMins * MINS))
					/ SECS;
			long durationMsecs = durationMs - (durationHrs * HRS)
					- (durationMins * MINS) - (durationSecs * SECS);

			ctx.fireStartParseEvent("MPEG");
			ctx.fireParseEvent("version-name", getVersionName(version));
			ctx.fireParseEvent("version", getVersionNumber(version));
			ctx.fireParseEvent("layer-name", getLayerName(layer));
			ctx.fireParseEvent("layer", getLayerNumber(layer));
			ctx.fireParseEvent("protection", pro);
			ctx.fireParseEvent("bit-rate", realBitRate);
			ctx.fireParseEvent("bit-rate-unit", "kbps");
			ctx.fireParseEvent("sample-rate", realSampleRate);
			ctx.fireParseEvent("sample-rate-unit", "Hz");
			ctx.fireParseEvent("mode", getMode(channels));
			ctx.fireParseEvent("channels", getChannels(channels));
			if (channels == 1) {
				ctx.fireParseEvent("mode-extension", getModeExtension(layer,
						modeExt));
			}
			ctx.fireStartParseEvent("duration");
			ctx.fireParseEvent("total-milliseconds", durationMs);
			ctx.fireParseEvent("hours", durationHrs);
			ctx.fireParseEvent("minutes", durationMins);
			ctx.fireParseEvent("seconds", durationSecs);
			ctx.fireParseEvent("ms", durationMsecs);
			ctx.fireEndParseEvent("duration");
			ctx.fireParseEvent("copyright", copyright);
			ctx.fireParseEvent("original", original);
			ctx.fireParseEvent("emph", getEmph(emph));
			ctx.fireEndParseEvent("MPEG");

		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			throw new RuntimeException(ex);
		} finally {
			ftk.close();
		}
		ctx.fireEndParseEvent("mp3");
	}

	/**
	 * @param realBitRate
	 * @param realSampleRate
	 * @param channels
	 * @param l
	 * @return
	 */
	private long getDuration(int realBitRate, int realSampleRate, int channels,
			long blocksize) {
		float ms = ((((blocksize - 196 /* header size */) / (realBitRate * 1000f))) * 8f) * 1000f;
		return (long) ms;
	}

	/**
	 * @param layer
	 * @param modeExt
	 * @return
	 */
	private String getModeExtension(int layer, int modeExt) {
		String result = "n/a";
		// layer 1 & 2
		if (layer == 3 || layer == 2) {
			switch (modeExt) {
			case 0:
				result = "bands 4 to 31";
				break;
			case 1:
				result = "bands 8 to 31";
				break;
			case 2:
				result = "bands 12 to 31";
				break;
			case 3:
				result = "bands 16 to 31";
				break;
			}
		}
		// layer 3
		else if (layer == 1) {
			result = "off";
		}
		return result;
	}

	/**
	 * @param layer
	 * @param emph
	 * @return
	 */
	private String getEmph(int emph) {
		String result = "n/a";
		switch (emph) {
		case 0:
			result = "none";
			break;
		case 1:
			result = "50/15 ms";
			break;
		case 2:
			result = "reserved";
			break;
		case 3:
			result = "CCIT J.17";
			break;
		}
		return result;
	}

	/**
	 * @param channels
	 * @return
	 */
	private Object getChannels(int channels) {
		String result = "n/a";
		switch (channels) {
		case 0:
			result = "2";
			break;
		case 1:
			result = "2";
			break;
		case 2:
			result = "2";
			break;
		case 3:
			result = "1";
			break;
		}
		return result;
	}

	/**
	 * @param channels
	 * @return
	 */
	private Object getMode(int channels) {
		String result = "n/a";
		switch (channels) {
		case 0:
			result = "Stereo";
			break;
		case 1:
			result = "Joint stereo (Stereo)";
			break;
		case 2:
			result = "Dual channel (Stereo)";
			break;
		case 3:
			result = "Single channel (Mono)";
			break;
		}
		return result;
	}

	/**
	 * @param version
	 * @param layer
	 * @param sampleRateIndex
	 * @return
	 */
	private int getSampleRate(int version, int layer, int sampleRateIndex) {
		int[] v1 = new int[] { 44100, 48000, 32000, -1 };
		int[] v2 = new int[] { 22050, 24000, 16000, -1 };
		int[] v25 = new int[] { 11025, 12000, 8000, -1 };

		int[] index = null;
		// version 1;
		if (version == 3) {
			index = v1;
		}
		// version 2;
		if (version == 2) {
			index = v2;
		}
		// version 2.5;
		if (version == 0) {
			index = v25;
		}

		return index[sampleRateIndex];

	}

	/**
	 * @param version
	 * @param layer
	 * @param bitRateIndex
	 * @return
	 */
	private int getBitRate(int version, int layer, int bitRateIndex) {
		int[] v1l1 = new int[] { 0, 32, 64, 96, 128, 160, 192, 224, 256, 288,
				320, 352, 384, 416, 448, -1 };
		int[] v1l2 = new int[] { 0, 32, 48, 56, 64, 80, 96, 112, 128, 160, 192,
				224, 256, 320, 384, -1 };
		int[] v1l3 = new int[] { 0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160,
				192, 224, 256, 320, -1 };
		int[] v2l1 = new int[] { 0, 32, 48, 56, 64, 80, 96, 112, 128, 144, 160,
				176, 192, 224, 256, -1 };
		int[] v2l2 = new int[] { 0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112,
				128, 144, 160, -1 };
		int[] v2l3 = v2l2; // same as above

		int[] index = null;
		// version 1;
		if (version == 3) {
			// layer 3
			if (layer == 1) {
				index = v1l3;
			}
			// layer 2
			if (layer == 2) {
				index = v1l2;
			}
			// layer 1
			if (layer == 3) {
				index = v1l1;
			}
		}
		// version 2;
		if (version == 2) {
			// layer 3
			if (layer == 1) {
				index = v2l3;
			}
			// layer 2
			if (layer == 2) {
				index = v2l2;
			}
			// layer 1
			if (layer == 3) {
				index = v2l1;
			}
		}

		return index[bitRateIndex];
	}

	/**
	 * @param version
	 * @return
	 */
	private String getVersionName(int v) {
		String version = null;
		switch (v) {
		case 0:
			version = "MPEG Version 2.5";
			break;
		case 1:
			version = "reserved";
			break;
		case 2:
			version = "MPEG Version 2 (ISO/IEC 13818-3)";
			break;
		case 3:
			version = "MPEG Version 1 (ISO/IEC 11172-3)";
			break;
		}
		return version;
	}

	/**
	 * @param version
	 * @return
	 */
	private String getVersionNumber(int v) {
		String version = null;
		switch (v) {
		case 0:
			version = "2.5";
			break;
		case 1:
			version = "n/a";
			break;
		case 2:
			version = "2";
			break;
		case 3:
			version = "1";
			break;
		}
		return version;
	}

	/**
	 * @param version
	 * @return
	 */
	private String getLayerName(int l) {
		String layer = null;
		switch (l) {
		case 0:
			layer = "reserved";
			break;
		case 1:
			layer = "Layer III";
			break;
		case 2:
			layer = "Layer II";
			break;
		case 3:
			layer = "Layer I";
			break;
		}
		return layer;
	}

	/**
	 * @param version
	 * @return
	 */
	private Object getLayerNumber(int l) {
		String layer = null;
		switch (l) {
		case 0:
			layer = "n/a";
			break;
		case 1:
			layer = "3";
			break;
		case 2:
			layer = "2";
			break;
		case 3:
			layer = "1";
			break;
		}
		return layer;
	}

	private static int getSyncsafeInt(int in) {
		int out = 0;

		byte[] b = BitFieldUtil.getBytes(in);

		for (int i = b.length - 1; i >= 0; i--) {
			out = out << 7;
			// System.out.println(" "+i+" =
			// "+Integer.toBinaryString((int)b[i])+" = "+b[i]);
			out += b[i];
		}

		return out;
	}

}
