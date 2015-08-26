package nz.govt.natlib.adapter.pdfbox;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

import org.pdfbox.cos.COSArray;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.cos.COSString;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentCatalog;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.pdmodel.encryption.PDEncryptionDictionary;

public class PDFBoxAdapter extends DataAdapter {
	
	public static final String[] schemeVersionNames = {"Unsupported", "Standard 40bits", "Standard 40+bits", "Unpublished", "Special"};

	public boolean acceptsFile(File file) {
		boolean valid = checkFileHeader(file, toHexFilter("%PDF")) ||
						checkFileHeader(file, toHexFilter("%pdf"));
		//LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER, file.getName() + (valid ? " is " : " is not ") + "a PDF");
		return valid;
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		ctx.fireStartParseEvent("pdf");
		writeFileInfo(file, ctx);
		
		PDDocument doc = null;
		
		try {
			doc = PDDocument.load(file);
			boolean encrypted = doc.isEncrypted();
			
			// Attempt to decrypt the file if it is encrypted
			if(doc.isEncrypted()) {
				try {
					doc.decrypt(null);
				}
				catch(InvalidPasswordException ex) { 
					LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER, "Invalid Password - PDF may be encrypted with a non-empty password");
					LogManager.getInstance().logMessage(ex);
					throw new IOException(ex.getMessage());
				}
				catch(CryptographyException ex) {
					LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER, "Cyptography Exception parsing file.");					
					LogManager.getInstance().logMessage(ex);
					throw new IOException(ex.getMessage());
				}
			}
	
			PDDocumentInformation info = doc.getDocumentInformation();
			PDDocumentCatalog catlog = doc.getDocumentCatalog();
			COSDocument cosDoc = doc.getDocument();		
			
			ctx.fireStartParseEvent("pdf-meta");
	
			COSArray array = cosDoc.getDocumentID();
			if(array != null && array.size() == 2) {
				ctx.fireParseEvent("doc-id", ((COSString) array.get(0)).getHexString());
				ctx.fireParseEvent("iteration-id", ((COSString) array.get(1)).getHexString());
			}
			
			if(array == null || array.size() != 2) {
				ctx.fireParseEvent("original", "unknown");
			} 
			else {
				boolean orig = ((COSString) array.get(0)).getHexString().equals(((COSString) array.get(1)).getHexString());
				ctx.fireParseEvent("original", orig);
			}
			
			fireSpecialNull(ctx, "title", info.getTitle());
			fireSpecialNull(ctx, "language", catlog.getLanguage());
			fireSpecialNull(ctx, "author", info.getAuthor());
			fireSpecialNull(ctx, "creator", info.getCreator());
			fireSpecialNull(ctx, "subject", info.getSubject());
			fireSpecialNull(ctx, "producer", info.getProducer());
			fireSpecialNull(ctx, "keywords", info.getKeywords());		
			
			ctx.fireStartParseEvent("creation-date");
			fireDate(ctx, info.getCreationDate());
			ctx.fireEndParseEvent("creation-date");
			
			ctx.fireStartParseEvent("modified-date");
			fireDate(ctx, info.getModificationDate());
			ctx.fireEndParseEvent("modified-date");
			
			ctx.fireParseEvent("has-forms", catlog.getAcroForm() != null);
			ctx.fireParseEvent("has-metadata-stream", catlog.getMetadata() != null);
			ctx.fireParseEvent("has-outline", catlog.getDocumentOutline() != null);
			ctx.fireParseEvent("has-threads", catlog.getThreads().size() > 0);
			ctx.fireParseEvent("tagged", catlog.getMarkInfo() != null);
			fireSpecialNull(ctx, "page-layout", catlog.getPageLayout());
			fireSpecialNull(ctx, "page-mode", catlog.getPageMode());
			fireSpecialNull(ctx, "trapped", info.getTrapped());
			
			fireSpecialNull(ctx, "version", Float.toString(cosDoc.getVersion()));
			
			ctx.fireStartParseEvent("security");
			ctx.fireParseEvent("encrypted", encrypted);
			if (encrypted) {
				PDEncryptionDictionary encDict = doc.getEncryptionDictionary();
	
				PDFPermissions perms = new PDFPermissions(
						encDict.getCOSDictionary().getInt("P"),
						encDict.getCOSDictionary().getInt("V"));
				
				fireSpecialNull(ctx, "scheme", encDict.getFilter());
				fireSpecialNull(ctx, "scheme-type", schemeVersionNames[encDict.getVersion()]);
				ctx.fireParseEvent("key-length", encDict.getLength());
				ctx.fireParseEvent("readonly", !perms.allowModify());
				ctx.fireParseEvent("allow-print", perms.allowPrint());
				ctx.fireParseEvent("allow-copy", perms.allowCopy());
				ctx.fireParseEvent("allow-notes", perms.allowTextNotes());
				ctx.fireParseEvent("user-password", encDict.getUserKey() != null);
				ctx.fireParseEvent("owner-password", encDict.getOwnerKey() != null);
			}
			ctx.fireEndParseEvent("security");		
			
			ctx.fireEndParseEvent("pdf-meta");
		}
		catch(OutOfMemoryError er) { 
			LogManager.getInstance().logMessage(LogMessage.CRITICAL, "Out of memory processing PDF.");
			throw new IOException("Out of memory");
			//throw er;
		}
		catch(RuntimeException ex) { 
			LogManager.getInstance().logMessage(LogMessage.CRITICAL, ex.getMessage());
			throw ex;
		}
		catch(Error er) { 
			LogManager.getInstance().logMessage(LogMessage.CRITICAL, "Out of memory processing PDF.");
			throw er;
		}
		finally {
			if(doc != null) { 
				doc.close();
			}
			ctx.fireEndParseEvent("pdf");			
		}
	}
	
	private static void fireDate(ParserContext ctx, Calendar cal) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		if (cal != null) {
			dateFormatter.applyPattern(FXUtil.dateFormat);
			ctx.fireParseEvent("DATE", dateFormatter.format(cal.getTime()));
			ctx.fireParseEvent("DATEPATTERN", FXUtil.dateFormat);
			dateFormatter.applyPattern(FXUtil.timeFormat);
			ctx.fireParseEvent("TIME", dateFormatter.format(cal.getTime()));
			ctx.fireParseEvent("TIMEPATTERN", FXUtil.timeFormat);
		} else {
			ctx.fireParseEvent("unavailable");
		}		
	}
	
	
	private static void fireSpecialNull(ParserContext ctx, String name, String value) {
		if (value == null) {
			ctx.fireParseEvent(name, "");
		} else {
			ctx.fireParseEvent(name, value);
		}
	}	


	public String getVersion() {
		return "1.0";
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
}
