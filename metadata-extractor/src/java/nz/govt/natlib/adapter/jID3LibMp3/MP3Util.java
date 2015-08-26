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
 * Created on 8/06/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.adapter.jID3LibMp3;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class MP3Util {
	private static String[] genreCodes =
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
					"124", "125" };

	private static String[] genreNames = 
		new String[] { "Blues", "Classic Rock",
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
					"Dance Hall" };

	private static Map genre;
	static {
		if (genreCodes.length != genreNames.length) {
			throw new RuntimeException("map names do not match map count");
		}
		genre = new HashMap(genreCodes.length, 100);
		for (int i = 0; i < genreCodes.length; i++) {
			genre.put(genreCodes[i], genreNames[i]);
		}
	}

	public static ID3v2Tag getTag(String type, String value) {
		ID3v2Tag result = new ID3v2Tag();
		// from id3v1.x -->
		// "title","artist","album","year","comment","genre","track"
		result.setValue(value);

		if (type.equalsIgnoreCase("TALB") || type.equalsIgnoreCase("TAL")) {
			result.setName("album");
		} else if (type.equalsIgnoreCase("COMM") || type.equalsIgnoreCase("COM")) {
			result.setName("comment");
			try {
				byte enc = (byte) value.charAt(0);
				int valPos = 1;
				String lang = value.substring(1, 4);
				String sht = "";
				valPos = 4;
				for (; valPos < value.length(); valPos++) {
					char c = value.charAt(valPos);
					if (c == 0x00) {
						break;
					}
					sht += c;
				}
				String full = value.substring(valPos + 1).trim();
				result.setAttributes(new String[] { "language", "name" });
				result.setValues(new String[] { lang, sht });
				result.setValue(full);
			} catch (RuntimeException e) {
				result.setValue(value);
			}
		} else if (type.equalsIgnoreCase("TIT1") || type.equalsIgnoreCase("TT1")) {
			result.setName("Group");
		} else if (type.equalsIgnoreCase("TIT2") || type.equalsIgnoreCase("TT2")) {
			result.setName("Title");
		} else if (type.equalsIgnoreCase("TIT3") || type.equalsIgnoreCase("TT3")) {
			result.setName("Subtitle");
		} else if (type.equalsIgnoreCase("TOAL")) {
			result.setName("Movie");
		} else if (type.equalsIgnoreCase("TRCK") || type.equalsIgnoreCase("TRK")) {
			result.setName("Track");
		} else if (type.equalsIgnoreCase("TPOS") || type.equalsIgnoreCase("TPA")) {
			result.setName("part-of-set");
		} else if (type.equalsIgnoreCase("TSST")) {
			result.setName("part-of-set");
		} else if (type.equalsIgnoreCase("TSRC") || type.equalsIgnoreCase("TRC")) {
			result.setName("standard-recording-code");
		} else if (type.equalsIgnoreCase("TPE1") || type.equalsIgnoreCase("TP1")) {
			result.setName("artist");
		} else if (type.equalsIgnoreCase("TPE2") || type.equalsIgnoreCase("TP2")) {
			result.setName("band");
		} else if (type.equalsIgnoreCase("TPE3") || type.equalsIgnoreCase("TP3")) {
			result.setName("conductor");
		} else if (type.equalsIgnoreCase("TPE4") || type.equalsIgnoreCase("TP4")) {
			result.setName("remixed-by");
		} else if (type.equalsIgnoreCase("TPE4") || type.equalsIgnoreCase("TP4")) {
			result.setName("remixed-by");
		} else if (type.equalsIgnoreCase("TEXT") || type.equalsIgnoreCase("TXT")) {
			result.setName("lyricist");
		} else if (type.equalsIgnoreCase("TOLY") || type.equalsIgnoreCase("TOL")) {
			result.setName("original-lyricist");
		} else if (type.equalsIgnoreCase("TCOM") || type.equalsIgnoreCase("TCM")) {
			result.setName("composer");
		} else if (type.equalsIgnoreCase("TMCL")) {
			result.setName("credit-list");
		} else if (type.equalsIgnoreCase("TIPL")) {
			result.setName("people-involved");
		} else if (type.equalsIgnoreCase("TENC") || type.equalsIgnoreCase("TEN")) {
			result.setName("encoded-by");
		} else if (type.equalsIgnoreCase("TBPM") || type.equalsIgnoreCase("TBP")) {
			result.setName("beats-per-minute");
		} else if (type.equalsIgnoreCase("TLEN") || type.equalsIgnoreCase("TLE")) {
			result.setName("length");
		} else if (type.equalsIgnoreCase("TKEY") || type.equalsIgnoreCase("TKE")) {
			result.setName("initial-key");
		} else if (type.equalsIgnoreCase("TLAN") || type.equalsIgnoreCase("TLA")) {
			result.setName("language");
		} else if (type.equalsIgnoreCase("TCON") || type.equalsIgnoreCase("TCO")) {
			result.setName("content-type");
			result.setValue(tryTranslating(value));
		} else if (type.equalsIgnoreCase("TFLT") || type.equalsIgnoreCase("TFT")) {
			result.setName("file-type");
		} else if (type.equalsIgnoreCase("TMED") || type.equalsIgnoreCase("TMT")) {
			result.setName("media-type");
		} else if (type.equalsIgnoreCase("TMOO")) {
			result.setName("mood");
		} else if (type.equalsIgnoreCase("TCOP") || type.equalsIgnoreCase("TCR")) {
			result.setName("copyright");
		} else if (type.equalsIgnoreCase("TPRO")) {
			result.setName("produced-notice");
		} else if (type.equalsIgnoreCase("TPUB") || type.equalsIgnoreCase("TPB")) {
			result.setName("publisher");
		} else if (type.equalsIgnoreCase("TOWN")) {
			result.setName("owner");
		} else if (type.equalsIgnoreCase("TRSN")) {
			result.setName("inet-radio-station-name");
		} else if (type.equalsIgnoreCase("TRSO")) {
			result.setName("inet-radio-station-owner");
		} else if (type.equalsIgnoreCase("TOFN") || type.equalsIgnoreCase("TOF")) {
			result.setName("original-filename");
		} else if (type.equalsIgnoreCase("TDLY") || type.equalsIgnoreCase("TDY")) {
			result.setName("playlist-delay");
		} else if (type.equalsIgnoreCase("TDEN")) {
			result.setName("encoding-time");
		} else if (type.equalsIgnoreCase("TDOR")) {
			result.setName("Original-Release");
		} else if (type.equalsIgnoreCase("TDRC")) {
			result.setName("recording-time");
		} else if (type.equalsIgnoreCase("TDRL")) {
			result.setName("release-time");
		} else if (type.equalsIgnoreCase("TDTG")) {
			result.setName("tagging-time");
		} else if (type.equalsIgnoreCase("TSSE") || type.equalsIgnoreCase("TSS")) {
			result.setName("encoder-settings");
		} else if (type.equalsIgnoreCase("TSOA")) {
			result.setName("album-sort-order");
		} else if (type.equalsIgnoreCase("TSOP")) {
			result.setName("performer-sort-order");
		} else if (type.equalsIgnoreCase("TSOT")) {
			result.setName("title-sort-order");
		} else if (type.equalsIgnoreCase("WCOM") || type.equalsIgnoreCase("WCM")) {
			result.setName("commercial-url");
		} else if (type.equalsIgnoreCase("WCOP") || type.equalsIgnoreCase("WCP")) {
			result.setName("copyright-url");
		} else if (type.equalsIgnoreCase("WOAF") || type.equalsIgnoreCase("WAF")) {
			result.setName("file-url");
		} else if (type.equalsIgnoreCase("WOAR") || type.equalsIgnoreCase("WAR")) {
			result.setName("artist-url");
		} else if (type.equalsIgnoreCase("WOAS") || type.equalsIgnoreCase("WAS")) {
			result.setName("source-url");
		} else if (type.equalsIgnoreCase("WORS")) {
			result.setName("inet-radio-station-url");
		} else if (type.equalsIgnoreCase("WPAY")) {
			result.setName("payment-url");
		} else if (type.equalsIgnoreCase("WPUB") || type.equalsIgnoreCase("WPB")) {
			result.setName("publisher-url");
		} else if (type.equalsIgnoreCase("MCDI") || type.equalsIgnoreCase("MCI")) {
			result.setName("music-cd-id");
		} else if (type.equalsIgnoreCase("USLT") || type.equalsIgnoreCase("ULT")) {
			result.setName("lyrics");
		} else if (type.equalsIgnoreCase("SYLT") || type.equalsIgnoreCase("SLT")) {
			result.setName("lyrics");
		} else if (type.equalsIgnoreCase("RVA2") || type.equalsIgnoreCase("RVA")) {
			result.setName("relative-volume-adjustment");
		} else if (type.equalsIgnoreCase("EQUA") || type.equalsIgnoreCase("EQU")) {
			result.setName("equalisation");
			result.setValue("yes");
		} else if (type.equalsIgnoreCase("RVRB") || type.equalsIgnoreCase("REV")) {
			result.setName("reverb");
			result.setValue("yes");
		} else if (type.equalsIgnoreCase("APIC") || type.equalsIgnoreCase("PIC")) {
			result.setName("picture");
			result.setValue("true");
		} else if (type.equalsIgnoreCase("AENC") || type.equalsIgnoreCase("CRA")) {
			result.setName("encryped");
			result.setValue("true");
		} else if (type.equalsIgnoreCase("USER")) {
			result.setName("terms-of-use");
		} else if (type.equalsIgnoreCase("OWNE")) {
			result.setName("ownership-details");
		} else {
			return null;
		}

		return result;
	}

	private static String tryTranslating(String value) {
		if (value == null) return null;
		// Translate an ID3v2.0 TCO into a detailed genre text
		/*
		 * The genre from v2.0 can be of the form: a numeric string
		 * inside a ( and ). For example, (21) or several references
		 * like (51)(39).
		 * Reference: http://www.id3.org/id3v2-00
		 */
		StringBuffer translatedValue = new StringBuffer();
		if (value.contains("(")) {
			StringTokenizer st = new StringTokenizer(value, "(");
			while (st.hasMoreElements()) {
				String token = st.nextToken();
				if (token != null && token.length() > 1 && token.contains(")")) {
					String genreNumber = token.substring(0, token.indexOf(")"));
					String genreName = genreNumberToName(genreNumber);
					if (genreName != null) {
						translatedValue.append(genreName);
						translatedValue.append(" ");
					}
				}
			}
		}
		if (translatedValue.length() > 0)
			return translatedValue.toString().trim();
		else return value;
	}

	static String genreNumberToName(String genreNumber) {
		String name = (String)genre.get(genreNumber);
		return (name != null) ? name : genreNumber;
	}
}
