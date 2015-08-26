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

package nz.govt.natlib.adapter.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.adapter.pdf.dom.PDFDocument;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FileDataSource;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * Adapter to parse Open Office Documents.
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class PDFAdapter extends DataAdapter {

	public boolean acceptsFile(File file) {
		boolean pdf = false;
		try {
			DataSource ftk = new FileDataSource(file);
			// Header and default information
			String head = FXUtil.getFixedStringValue(ftk, 4);
			if ((head.toLowerCase().equals("%pdf"))) {
				// version follows - but this adpater will do all!
				pdf = true;
			}
			ftk.close();
		} catch (IOException ex) {
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"IO Exception determining PDF file type");
		}
		return pdf;
	}

	public String getVersion() {
		return "1.1";
	}

	public String getOutputType() {
		return "pdf.dtd";
	}

	public String getInputType() {
		return "application/pdf";
	}

	public String getName() {
		return "PDF Text Adapter";
	}

	public String getDescription() {
		return "Adapts all PDF Formats from 1.1 to 1.5.  Handles encrypted PDFs with no user password set";
	}

	public void adapt(File oFile, ParserContext ctx) throws IOException {
		// Header and default information
		ctx.fireStartParseEvent("pdf");
		writeFileInfo(oFile, ctx);
		try {
			InputStream in = new FileInputStream(oFile);
			ctx.fireStartParseEvent("pdf-meta");
			// the first thing to do is to open a PDF 'Stream'
			PDFDocument doc = new PDFDocument(oFile, null); // if we could pass
															// passwords there
															// is where to do
															// it...

			String idBase = doc.getIdBase();
			ctx.fireParseEvent("doc-id", doc.getIdBase());
			ctx.fireParseEvent("iteration-id", doc.getIdIteration());
			if (idBase == null) {
				ctx.fireParseEvent("original", "unknown");
			} else {
				ctx.fireParseEvent("original", doc.getIdIteration().equals(
						idBase));
			}

			fireSpecialNull(ctx, "title", doc.getTitle());
			fireSpecialNull(ctx, "language", doc.getLanguage());
			fireSpecialNull(ctx, "author", doc.getAuthor());
			fireSpecialNull(ctx, "creator", doc.getCreator());
			fireSpecialNull(ctx, "subject", doc.getSubject());
			fireSpecialNull(ctx, "producer", doc.getProducer());
			fireSpecialNull(ctx, "keywords", doc.getKeywords());

			// created...
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			Date date = doc.getCreationDate();
			ctx.fireStartParseEvent("creation-date");
			if (date != null) {
				dateFormatter.applyPattern(FXUtil.dateFormat);
				ctx.fireParseEvent("DATE", dateFormatter.format(date));
				ctx.fireParseEvent("DATEPATTERN", FXUtil.dateFormat);
				dateFormatter.applyPattern(FXUtil.timeFormat);
				ctx.fireParseEvent("TIME", dateFormatter.format(date));
				ctx.fireParseEvent("TIMEPATTERN", FXUtil.timeFormat);
			} else {
				ctx.fireParseEvent("unavailable");
			}
			ctx.fireEndParseEvent("creation-date");

			// modified
			date = doc.getModificationDate();
			ctx.fireStartParseEvent("modified-date");
			if (date != null) {
				dateFormatter.applyPattern(FXUtil.dateFormat);
				ctx.fireParseEvent("DATE", dateFormatter.format(date));
				ctx.fireParseEvent("DATEPATTERN", FXUtil.dateFormat);
				dateFormatter.applyPattern(FXUtil.timeFormat);
				ctx.fireParseEvent("TIME", dateFormatter.format(date));
				ctx.fireParseEvent("TIMEPATTERN", FXUtil.timeFormat);
			} else {
				ctx.fireParseEvent("unavailable");
			}
			ctx.fireEndParseEvent("modified-date");

			ctx.fireParseEvent("has-forms", doc.hasForms());
			ctx.fireParseEvent("has-metadata-stream", doc.hasMetadataStream());
			ctx.fireParseEvent("has-outine", doc.hasOutline());
			ctx.fireParseEvent("has-threads", doc.hasThreads());
			ctx.fireParseEvent("tagged", doc.isTagged());
			fireSpecialNull(ctx, "page-layout", doc.getPageLayout());
			fireSpecialNull(ctx, "page-mode", doc.getPageMode());
			fireSpecialNull(ctx, "trapped", doc.getTrapped());
			fireSpecialNull(ctx, "version", doc.getVersion());

			boolean encrypted = doc.isEncrypted();
			ctx.fireStartParseEvent("security");
			ctx.fireParseEvent("encrypted", encrypted);
			if (encrypted) {
				fireSpecialNull(ctx, "scheme", doc.getScheme());
				fireSpecialNull(ctx, "scheme-type", doc.getSchemeType());
				ctx.fireParseEvent("key-length", doc.getKeyLength());
				ctx.fireParseEvent("readonly", doc.isReadOnly());
				ctx.fireParseEvent("allow-print", doc.allowPrint());
				ctx.fireParseEvent("allow-copy", doc.allowCopy());
				ctx.fireParseEvent("allow-notes", doc.allowTextNotes());
				ctx.fireParseEvent("user-password", doc.hasUserPassword());
				ctx.fireParseEvent("owner-password", doc.hasOwnerPassword());
			}
			ctx.fireEndParseEvent("security");

			doc.close();
			ctx.fireEndParseEvent("pdf-meta");
			in.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			ctx.fireEndParseEvent("pdf");
		}
	}

	private static void fireSpecialNull(ParserContext ctx, String name,
			String value) {
		if (value == null) {
			ctx.fireParseEvent(name, "");
		} else {
			ctx.fireParseEvent(name, value);
		}
	}
}