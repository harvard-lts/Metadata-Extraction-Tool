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

package nz.govt.natlib.adapter.jID3LibMp3;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.farng.mp3.AbstractMP3FragmentBody;
import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.FrameBodyUnsupported;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.lyrics3.AbstractLyrics3;
import org.farng.mp3.lyrics3.Lyrics3v1;
import org.farng.mp3.lyrics3.Lyrics3v2;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.BitFieldUtil;
import nz.govt.natlib.fx.ParserContext;

import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * Adapter for MP3 Audio Files using Java ID3 Tag Library at http://javamusictag.sourceforge.net.
 * 
 * @author Raghu Pushpakath
 * @version 1.0
 */

public class ID3TagLibMP3Adapter extends DataAdapter {

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
		return "Java ID3 Tag Lib based Mp3 Audio Adapter";
	}

	public String getDescription() {
		return "Adapts Mp3 Audio files including ID3v1 and ID3v2 tags using Java ID3 Tag Library at http://javamusictag.sourceforge.net";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		long SECS = 1000;
		long MINS = 60 * SECS;
		long HRS = 60 * MINS;
		ctx.fireStartParseEvent("mp3");
		writeFileInfo(file, ctx);
		try {
			MP3File mp3File;
			try {
				mp3File = new MP3File(file);
			} catch (RuntimeException e) {
				e.printStackTrace();
				ctx.fireEndParseEvent("mp3");
				return;
			}
			mp3File.seekMP3Frame();
			ctx.fireStartParseEvent("header");
			if (mp3File.hasLyrics3Tag()) {
				// Extract Lyrics3 tag details
				AbstractLyrics3 tag = mp3File.getLyrics3Tag();
				extractLyrics3Tags(tag, ctx);
			} else if (mp3File.hasID3v2Tag()) {
				// Extract ID3 v2 tag details
				AbstractID3v2 tag = mp3File.getID3v2Tag();
				extractID3v2Tags(tag, ctx);
			} else if (mp3File.hasID3v1Tag()) {
				// Extract ID3 v1 tag details
				ID3v1 tag = mp3File.getID3v1Tag();
				extractID3v1Tags(tag, ctx);
			}
			ctx.fireEndParseEvent("header");
			ctx.fireStartParseEvent("MPEG");

			int version = mp3File.getMpegVersion();
			int layer = mp3File.getLayer();
			boolean pro = mp3File.isProtection();
			int realBitRate = mp3File.getBitRate();
			int realSampleRate = (int)(mp3File.getFrequency() * 1000);
			int mode = mp3File.getMode();
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
		}
		ctx.fireEndParseEvent("mp3");
	}

	private void extractLyrics3Tags(AbstractLyrics3 tag, ParserContext ctx) {
		/*
		 * Raghu Pushpakath:
		 * The code below is not tested since there was no MP3 with
		 * Lyrics3 tags available to me for testing.
		 */
		if (tag instanceof Lyrics3v1) {
			ctx.fireParseEvent("tag-version", "Lyrics3v1");
		} else if (tag instanceof Lyrics3v2) {
			ctx.fireParseEvent("tag-version", "Lyrics3v1");
		} else {
			ctx.fireParseEvent("tag-version", "Lyrics3 Unknown Version");
		}
		fireParseEvents(tag, ctx);
	}

	private void extractID3v1Tags(ID3v1 tag, ParserContext ctx) {
		ctx.fireParseEvent("tag-version", "ID3v1.x");
		ctx.fireParseEvent("version", "1.x");
		fireParseEvents(tag, ctx);
	}

	private void fireParseEvents(AbstractMP3Tag tag, ParserContext ctx) {
		String[][] elements = null;
		String title = null;
		try{
			title = tag.getSongTitle();
		}catch (Exception e) {
			
			// when an error "This tag does not contain that information" occurs from
			//lyrics3 classes ignore and don't add that tag in the extracted XML.
			// TODO: handle exception
		}
	
		String artist = null;
		try{
			artist = tag.getLeadArtist();
		}catch (Exception e) {
			
			// when an error "This tag does not contain that information" occurs from
			//lyrics3 classes ignore and don't add that tag in the extracted XML.
			//handle exception
			LogManager.getInstance().logMessage(e);
		}
	
		String album = null;
		try{
			album = tag.getAlbumTitle();
		}catch (Exception e) {
		
			// when an error "This tag does not contain that information" occurs from
			//lyrics3 classes ignore and don't add that tag in the extracted XML.
			// handle exception
			LogManager.getInstance().logMessage(e);
		}
		
		String year = null;
		try{
			year = tag.getYearReleased();
		}catch (Exception e) {
		
			// when an error "This tag does not contain that information" occurs from
			//lyrics3 classes ignore and don't add that tag in the extracted XML.
			// handle exception
			LogManager.getInstance().logMessage(e);
		}
		
		String comment = null;
		try{
			comment = tag.getSongComment();
		}catch (Exception e) {
			// when an error "This tag does not contain that information" occurs from
			//lyrics3 classes ignore and don't add that tag in the extracted XML.
			// handle exception
			LogManager.getInstance().logMessage(e);
		}
		
		String track = null;
		try{
			track = 	tag.getTrackNumberOnAlbum();
		}catch (Exception e) {
			// when an error "This tag does not contain that information" occurs from
			//lyrics3 classes ignore and don't add that tag in the extracted XML.
			// handle exception
			LogManager.getInstance().logMessage(e);
		}
		
		String genre = null;
		try{
			genre = 	MP3Util.genreNumberToName("" + tag.getSongGenre());
		}catch (Exception e) {
			// when an error "This tag does not contain that information" occurs from
			//lyrics3 classes ignore and don't add that tag in the extracted XML.
			//handle exception
			LogManager.getInstance().logMessage(e);
		}
				
		
		elements = new String[][] {
			new String[]{"title", 		title},
			new String[]{"artist", 		artist},
			new String[]{"album", 		album},
			new String[]{"year", 		year},
			new String[]{"comment", 	comment},
			new String[]{"track", 		track},
			new String[]{"genre", 		genre}
	};
	
		
		for (int i = 0; i < elements.length; i++) {
			String[] element = elements[i];
			ctx.fireStartParseEvent(element[0]);
			ctx.fireParseEvent(element[1]);
			ctx.fireEndParseEvent(element[0]);
		}
	}

	private void extractID3v2Tags(AbstractID3v2 tag, ParserContext ctx) {
		int version = tag.getMajorVersion();
		int subVersion = tag.getRevision();
		ctx.fireParseEvent("tag-version", "ID3v2." + version + "." + subVersion);
		Iterator iter = tag.getFrameIterator();
		if (iter != null) {
			while (iter.hasNext()) {
				Object anItem = iter.next();
				if (anItem != null && anItem instanceof AbstractID3v2Frame) {
					AbstractID3v2Frame anAbstractID3v2Frame = (AbstractID3v2Frame)anItem;
					AbstractMP3FragmentBody frameBody = anAbstractID3v2Frame.getBody();
					String frameText = frameBody.getBriefDescription();
					if (frameBody instanceof FrameBodyUnsupported) {
						frameText = ((FrameBodyUnsupported) frameBody).toString();
						String separator = " : ";
						int valueLocation = frameText.indexOf(separator);
						if (valueLocation != -1) {
							frameText = frameText.substring(valueLocation + separator.length());
						}
					}
					String frameType = anAbstractID3v2Frame.getIdentifier();
					int nullCharPosition = frameType.indexOf(0);
					if (nullCharPosition != -1) {
						frameType = frameType.substring(0, nullCharPosition);
					}
					ID3v2Tag v2tag = MP3Util.getTag(frameType, frameText);
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
				} else {
					System.out.println("ID3TagLibMP3Adapter: The current frame from AbstractID3v2 is either null or not of type AbstractID3v2Frame: " + anItem);
				}
			}
		}
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

}
