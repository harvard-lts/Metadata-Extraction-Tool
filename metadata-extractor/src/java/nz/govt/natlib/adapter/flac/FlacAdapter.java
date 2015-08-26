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

package nz.govt.natlib.adapter.flac;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import nz.govt.natlib.adapter.AdapterUtils;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FileDataSource;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.FrameListener;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.metadata.Application;
import org.kc7bfi.jflac.metadata.CueIndex;
import org.kc7bfi.jflac.metadata.CueSheet;
import org.kc7bfi.jflac.metadata.CueTrack;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.Padding;
import org.kc7bfi.jflac.metadata.Picture;
import org.kc7bfi.jflac.metadata.SeekPoint;
import org.kc7bfi.jflac.metadata.SeekTable;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.metadata.VorbisComment;

/**
 * FlacAdapter is responsible for extracting metadata out of FLAC (Free Lossless Audio Codec) files.
 * 
 * For specification of FLAC files, see the URL:
 * http://flac.sourceforge.net/format.html
 * 
 * This class makes a heavy use of the jFLAC (Java FLAC decoder) library available from:
 * http://jflac.sourceforge.net/
 * http://sourceforge.net/projects/jflac/files/
 * 
 * @author Raghu Pushpakath
 * @version 1.0
 */
public class FlacAdapter extends DataAdapter {
	private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
	
	/*
	 * A combined list of all Vorbis comment field names as defined by:
	 * http://xiph.org/vorbis/doc/v-comment.html
	 * and
	 * http://age.hobba.nl/audio/mirroredpages/ogg-tagging.html
	 */
	private static final String[] vorbisCommentFieldNames = new String[] {
		"ALBUM",
		"ARRANGER",
		"ARTIST",
		"AUTHOR",
		"COMMENT",
		"COMPOSER",
		"CONDUCTOR",
		"CONTACT",
		"COPYRIGHT",
		"DATE",
		"DESCRIPTION",
		"DISCNUMBER",
		"EAN-UPN", // This one needs to be searched in Vorbis comments as "EAN/UPN"
		"ENCODED-BY",
		"ENCODING",
		"ENSEMBLE",
		"GENRE",
		"ISRC",
		"LABEL",
		"LABELNO",
		"LICENSE",
		"LOCATION",
		"LYRICIST",
		"OPUS",
		"ORGANIZATION",
		"PART",
		"PARTNUMBER",
		"PERFORMER",
		"PUBLISHER",
		"SOURCEMEDIA",
		"TITLE",
		"TRACKNUMBER",
		"VERSION",
	};

	public FlacAdapter() {
	}

	public boolean acceptsFile(File file) {
		boolean isFlac = false;
		DataSource ftk = null;
		
		try {
			/*
			 * Make sure that the first 4 bytes form the string "fLaC" which
			 * is the signature for a FLAC file
			 */
			ftk = new FileDataSource(file);
			String header = FXUtil.getFixedStringValue(ftk, 4);
			isFlac = header.equalsIgnoreCase("fLaC");
		} catch (Exception ex) {
			isFlac = false;
		} finally {
			if (ftk != null)
				AdapterUtils.close(ftk);
		}

		// Log a message if this isn't a real FLAC file.
		if (!isFlac) {
			LogManager.getInstance().logMessage(LogMessage.INFO,
					file.getName() + " is not a FLAC file");
		}
		// Return true if we believe this is a FLAC; otherwise false.
		return isFlac;
	}

	public String getOutputType() {
		return "flac.dtd";
	}

	public String getInputType() {
		return "audio/x-flac";
	}

	public String getName() {
		return "FLAC File Adapter";
	}

	public String getDescription() {
		return "Adapts FLAC files";
	}

	public String getVersion() {
		return "1.1";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		FLACDecoder decoder = null;
		InputStream stream = null; 
		try {
			stream = new FileInputStream(file);
			decoder = new FLACDecoder(stream);
			decoder.addFrameListener(new FlacFrameListener(ctx));
			ctx.fireStartParseEvent("FLAC");
			writeFileInfo(file, ctx);
			ctx.fireStartParseEvent("FLACMETADATA");
			try {
				decoder.decode();
			} finally {
				ctx.fireEndParseEvent("FLACMETADATA");
				ctx.fireEndParseEvent("FLAC");
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

	private void populateStreamInfo(ParserContext ctx, StreamInfo streamInfo) {
		ctx.fireStartParseEvent("STREAMINFO");
		ctx.fireParseEvent("MINBLOCKSIZE", streamInfo.getMinBlockSize());
		ctx.fireParseEvent("MAXBLOCKSIZE", streamInfo.getMaxBlockSize());
		ctx.fireParseEvent("MINFRAMESIZE", streamInfo.getMinFrameSize());
		ctx.fireParseEvent("MAXFRAMESIZE", streamInfo.getMaxFrameSize());
		ctx.fireParseEvent("SAMPLE-RATE", streamInfo.getSampleRate());
		ctx.fireParseEvent("SAMPLE-RATE-UNIT", "Hz");
		ctx.fireParseEvent("CHANNELS", streamInfo.getChannels());
		ctx.fireParseEvent("BITSPERSAMPLE", streamInfo.getBitsPerSample());
		ctx.fireParseEvent("TOTALSAMPLES", streamInfo.getTotalSamples());
		ctx.fireParseEvent("MD5", getMD5(streamInfo));
		ctx.fireEndParseEvent("STREAMINFO");
	}

	private void populateApplicationData(ParserContext ctx, Application application) {
		ctx.fireStartParseEvent("APPLICATION");
		ctx.fireParseEvent("APPLICATION-ID", getStringFromByteArrayOfInaccessibleField("id", application));
		ctx.fireEndParseEvent("APPLICATION");
	}

	private void populatePaddingData(ParserContext ctx, Padding padding) {
		ctx.fireStartParseEvent("PADDING");
		ctx.fireParseEvent("LENGTH", getValueOfInaccessibleField("length", padding));
		ctx.fireEndParseEvent("PADDING");
	}

	private void populateSeekTableData(ParserContext ctx, SeekTable seekTable) {
		ctx.fireStartParseEvent("SEEKTABLE");
		int numSeekPoints = seekTable.numberOfPoints();
		if (numSeekPoints > 0) {
			for (int i = 0; i < numSeekPoints; i++) {
				SeekPoint seekPoint = seekTable.getSeekPoint(i);
				ctx.fireStartParseEvent("SEEKPOINT");
				ctx.fireParseEvent("SAMPLE-NUMBER", getValueOfInaccessibleField("sampleNumber", seekPoint));
				ctx.fireParseEvent("STREAM-OFFSET", getValueOfInaccessibleField("streamOffset", seekPoint));
				ctx.fireParseEvent("FRAME-SAMPLES", getValueOfInaccessibleField("frameSamples", seekPoint));
				ctx.fireEndParseEvent("SEEKPOINT");
			}
		}
		ctx.fireEndParseEvent("SEEKTABLE");
	}

	private void populateVorbisCommentData(ParserContext ctx, VorbisComment vorbisComment) {
		ctx.fireStartParseEvent("VORBIS-COMMENT");
		ctx.fireParseEvent("VENDOR-STRING", getStringFromByteArrayOfInaccessibleField("vendorString", vorbisComment));
		for (int i = 0; i < vorbisCommentFieldNames.length; i++) {
			String commentFieldName = vorbisCommentFieldNames[i];
			String commentFieldNameToSearch = "EAN-UPN".equals(commentFieldName) ? "EAN/UPN" : commentFieldName;
			String[] comments = null;
			try {
				// This could throw NullPointerException if there are no Vorbis comments
				comments = vorbisComment.getCommentByName(commentFieldNameToSearch);
			} catch (RuntimeException e) {
			}
			StringBuffer commentSb = new StringBuffer();
			if (comments != null && comments.length > 0) {
				for (int commentIndex = 0; commentIndex < comments.length; commentIndex++) {
					String aComment = comments[commentIndex];
					if (aComment != null) {
						commentSb.append(aComment.trim()).append(" ");
					}
				}
				ctx.fireParseEvent(commentFieldName, commentSb.toString().trim());
			}
		}
		ctx.fireEndParseEvent("VORBIS-COMMENT");
	}

	private void populateCueSheetData(ParserContext ctx, CueSheet cueSheet) {
		ctx.fireStartParseEvent("CUESHEET");
		ctx.fireParseEvent("MEDIA-CATALOG-NUMBER", getStringFromByteArrayOfInaccessibleField("mediaCatalogNumber", cueSheet));
		ctx.fireParseEvent("LEAD-IN-SAMPLES", getValueOfInaccessibleField("leadIn", cueSheet));

		ctx.fireParseEvent("IS-CD", getValueOfInaccessibleField("isCD", cueSheet));
		ctx.fireParseEvent("NUMBER-OF-TRACKS", getValueOfInaccessibleField("numTracks", cueSheet));
		Object obj = getValueOfInaccessibleField("tracks", cueSheet);
		if (obj != null && obj instanceof CueTrack[]) {
			CueTrack[] tracks = (CueTrack[]) obj;
			for (int i = 0; i < tracks.length; i++) {
				ctx.fireStartParseEvent("CUETRACK");
				CueTrack track = tracks[i];
				ctx.fireParseEvent("TRACK-OFFSET", getValueOfInaccessibleField("offset", track));

				ctx.fireParseEvent("TRACK-NUMBER", getValueOfInaccessibleField("number", track));
				ctx.fireParseEvent("ISRC", getStringFromByteArrayOfInaccessibleField("isrc", track));
				ctx.fireParseEvent("IS-AUDIO", getValueOfInaccessibleField("type", track));
				ctx.fireParseEvent("PRE-EMPHASIS", getValueOfInaccessibleField("preEmphasis", track));
				ctx.fireParseEvent("TRACK-INDEX-POINTS", getValueOfInaccessibleField("numIndices", track));
				Object indicesObj = getValueOfInaccessibleField("indices", track);
				if (indicesObj != null && indicesObj instanceof CueIndex[]) {
					CueIndex[] cueIndices = (CueIndex[]) indicesObj;
					for (int j = 0; j < cueIndices.length; j++) {
						ctx.fireStartParseEvent("CUETRACKINDEX");
						CueIndex cueIndex = cueIndices[j];
						ctx.fireParseEvent("OFFSET", getValueOfInaccessibleField("offset", cueIndex));
						ctx.fireParseEvent("NUMBER", getValueOfInaccessibleField("number", cueIndex));
						ctx.fireEndParseEvent("CUETRACKINDEX");
					}
				}
				ctx.fireEndParseEvent("CUETRACK");
			}
		}
		ctx.fireEndParseEvent("CUESHEET");
	}

	private void populatePictureData(ParserContext ctx, Picture picture) {
		ctx.fireStartParseEvent("PICTURE");
		ctx.fireParseEvent("PICTURE-TYPE", getValueOfInaccessibleField("pictureType", picture));
		ctx.fireParseEvent("MIME-TYPE", getValueOfInaccessibleField("mimeString", picture));
		ctx.fireParseEvent("DESCRIPTION", getValueOfInaccessibleField("descString", picture));
		ctx.fireParseEvent("WIDTH", getValueOfInaccessibleField("picPixelWidth", picture));
		ctx.fireParseEvent("HEIGHT", getValueOfInaccessibleField("picPixelHeight", picture));
		ctx.fireParseEvent("COLOUR-DEPTH", getValueOfInaccessibleField("picBitsPerPixel", picture));
		ctx.fireParseEvent("COLOUR-DEPTH-UNIT", "Bits Per Pixel");
		ctx.fireParseEvent("NUM-COLOURS", getValueOfInaccessibleField("picColorCount", picture));
		ctx.fireParseEvent("DATA-LENGTH", getValueOfInaccessibleField("picByteCount", picture));
		ctx.fireEndParseEvent("PICTURE");
	}

	private static String getStringFromByteArrayOfInaccessibleField(String fieldName, Object instance) {
		Object obj = getValueOfInaccessibleField(fieldName, instance);
		if (obj != null && obj instanceof byte[]) {
			return new String((byte[]) obj);
		}
		return "";
	}

	private static String getMD5(StreamInfo streamInfo) {
		Object obj = getValueOfInaccessibleField("md5sum", streamInfo);
		if (obj != null && obj instanceof byte[]) {
			return toHexadecimal((byte[]) obj);
		}
		return "";
	}

    private static String toHexadecimal(byte[] buf) {
		char[] chars = new char[2 * buf.length];
		for (int i = 0; i < buf.length; ++i) {
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}

	private static Object getValueOfInaccessibleField(String fieldName, Object instance) {
		try {
			Class theClass = instance.getClass();
			Field privateField = theClass.getDeclaredField(fieldName);
			privateField.setAccessible(true);
			return privateField.get(instance);
		} catch (Exception ex) {
			LogManager.getInstance().logMessage(LogMessage.ERROR, "Could not get value of the field " 
					+ fieldName + " from instance " + instance + " through reflection" + ex);
			return null;
		}
	}

    private class FlacFrameListener implements FrameListener {
		private final ParserContext ctx;
		FlacFrameListener(ParserContext ctx) {
			this.ctx = ctx;
		}
		public void processError(String s) {
	        //System.out.println("Error: " + s);
		}
		public void processFrame(Frame frame) {
			// TODO: Do we need to extract the frame metadata?
	        //System.out.println("Frame: " + frame);
		}
		public void processMetadata(Metadata metadata) {
	        System.out.println("Metadata: " + metadata.toString());
	        if (metadata == null) return;
	        if (metadata instanceof StreamInfo) {
				populateStreamInfo(ctx, (StreamInfo) metadata);
	        } else if (metadata instanceof Application) {
				populateApplicationData(ctx, (Application) metadata);
	        } else if (metadata instanceof Padding) {
				populatePaddingData(ctx, (Padding) metadata);
	        } else if (metadata instanceof SeekTable) {
				populateSeekTableData(ctx, (SeekTable) metadata);
	        } else if (metadata instanceof VorbisComment) {
				populateVorbisCommentData(ctx, (VorbisComment) metadata);
	        } else if (metadata instanceof CueSheet) {
				populateCueSheetData(ctx, (CueSheet) metadata);
	        } else if (metadata instanceof Picture) {
				populatePictureData(ctx, (Picture) metadata);
	        }
		}
	}
}