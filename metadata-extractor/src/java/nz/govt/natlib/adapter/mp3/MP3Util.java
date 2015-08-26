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
package nz.govt.natlib.adapter.mp3;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class MP3Util {

	public static ID3v2Tag getTag(String type, String value) {
		ID3v2Tag result = new ID3v2Tag();
		// from id3v1.x -->
		// "title","artist","album","year","comment","genre","track"
		result.setValue(value);

		if (type.equalsIgnoreCase("TALB")) {
			result.setName("album");
		} else if (type.equalsIgnoreCase("COMM")) {
			result.setName("comment");
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
		} else if (type.equalsIgnoreCase("TIT1")) {
			result.setName("Group");
		} else if (type.equalsIgnoreCase("TIT2")) {
			result.setName("Title");
		} else if (type.equalsIgnoreCase("TIT3")) {
			result.setName("Subtitle");
		} else if (type.equalsIgnoreCase("TOAL")) {
			result.setName("Movie");
		} else if (type.equalsIgnoreCase("TRCK")) {
			result.setName("Track");
		} else if (type.equalsIgnoreCase("TPOS")) {
			result.setName("part-of-set");
		} else if (type.equalsIgnoreCase("TSST")) {
			result.setName("part-of-set");
		} else if (type.equalsIgnoreCase("TSRC")) {
			result.setName("standard-recording-code");
		} else if (type.equalsIgnoreCase("TPE1")) {
			result.setName("artist");
		} else if (type.equalsIgnoreCase("TPE2")) {
			result.setName("band");
		} else if (type.equalsIgnoreCase("TPE3")) {
			result.setName("conductor");
		} else if (type.equalsIgnoreCase("TPE4")) {
			result.setName("remixed-by");
		} else if (type.equalsIgnoreCase("TPE4")) {
			result.setName("remixed-by");
		} else if (type.equalsIgnoreCase("TEXT")) {
			result.setName("lyricist");
		} else if (type.equalsIgnoreCase("TOLY")) {
			result.setName("original-lyricist");
		} else if (type.equalsIgnoreCase("TCOM")) {
			result.setName("composer");
		} else if (type.equalsIgnoreCase("TMCL")) {
			result.setName("credit-list");
		} else if (type.equalsIgnoreCase("TIPL")) {
			result.setName("people-involved");
		} else if (type.equalsIgnoreCase("TENC")) {
			result.setName("encoded-by");
		} else if (type.equalsIgnoreCase("TBPM")) {
			result.setName("beats-per-minute");
		} else if (type.equalsIgnoreCase("TLEN")) {
			result.setName("length");
		} else if (type.equalsIgnoreCase("TKEY")) {
			result.setName("initial-key");
		} else if (type.equalsIgnoreCase("TLAN")) {
			result.setName("language");
		} else if (type.equalsIgnoreCase("TCON")) {
			result.setName("content-type");
		} else if (type.equalsIgnoreCase("TFLT")) {
			result.setName("file-type");
		} else if (type.equalsIgnoreCase("TMED")) {
			result.setName("media-type");
		} else if (type.equalsIgnoreCase("TMOO")) {
			result.setName("mood");
		} else if (type.equalsIgnoreCase("TCOP")) {
			result.setName("copyright");
		} else if (type.equalsIgnoreCase("TPRO")) {
			result.setName("produced-notice");
		} else if (type.equalsIgnoreCase("TPUB")) {
			result.setName("publisher");
		} else if (type.equalsIgnoreCase("TOWN")) {
			result.setName("owner");
		} else if (type.equalsIgnoreCase("TRSN")) {
			result.setName("inet-radio-station-name");
		} else if (type.equalsIgnoreCase("TRSO")) {
			result.setName("inet-radio-station-owner");
		} else if (type.equalsIgnoreCase("TOFN")) {
			result.setName("original-filename");
		} else if (type.equalsIgnoreCase("TDLY")) {
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
		} else if (type.equalsIgnoreCase("TSSE")) {
			result.setName("encoder-settings");
		} else if (type.equalsIgnoreCase("TSOA")) {
			result.setName("album-sort-order");
		} else if (type.equalsIgnoreCase("TSOP")) {
			result.setName("performer-sort-order");
		} else if (type.equalsIgnoreCase("TSOT")) {
			result.setName("title-sort-order");
		} else if (type.equalsIgnoreCase("WCOM")) {
			result.setName("commercial-url");
		} else if (type.equalsIgnoreCase("WCOP")) {
			result.setName("copyright-url");
		} else if (type.equalsIgnoreCase("WOAF")) {
			result.setName("file-url");
		} else if (type.equalsIgnoreCase("WOAR")) {
			result.setName("artist-url");
		} else if (type.equalsIgnoreCase("WOAS")) {
			result.setName("source-url");
		} else if (type.equalsIgnoreCase("WORS")) {
			result.setName("inet-radio-station-url");
		} else if (type.equalsIgnoreCase("WPAY")) {
			result.setName("payment-url");
		} else if (type.equalsIgnoreCase("WPUB")) {
			result.setName("publisher-url");
		} else if (type.equalsIgnoreCase("MCDI")) {
			result.setName("music-cd-id");
		} else if (type.equalsIgnoreCase("USLT")) {
			result.setName("lyrics");
		} else if (type.equalsIgnoreCase("SYLT")) {
			result.setName("lyrics");
		} else if (type.equalsIgnoreCase("RVA2")) {
			result.setName("relative-volume-adjustment");
		} else if (type.equalsIgnoreCase("RVA2")) {
			result.setName("equalisation");
			result.setValue("yes");
		} else if (type.equalsIgnoreCase("Reverb")) {
			result.setName("reverb");
			result.setValue("yes");
		} else if (type.equalsIgnoreCase("APIC")) {
			result.setName("picture");
			result.setValue("true");
		} else if (type.equalsIgnoreCase("AENC")) {
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
}
