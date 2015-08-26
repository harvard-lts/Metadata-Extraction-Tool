package nz.govt.natlib.adapter.mpeg4;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;

import com.coremedia.iso.BoxFactory;

import com.coremedia.iso.FileRandomAccessDataSource;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoInputStream;
import com.coremedia.iso.boxes.Box;

import nz.govt.natlib.adapter.AdapterUtils;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

public class MPEG4Adapter extends DataAdapter {

	public MPEG4Adapter() {
		// TODO Auto-generated constructor stub
		// Initialize all the Elements to represent all the chunks in the mpeg
		// file.

	}

	/**
	 * The method checks if input file has at least one ISO type box defined. it makes use of the
	 * mp4parser library to identify if the file is a standard MPEG file.
	 */

	public boolean acceptsFile(File file) {
		FileRandomAccessDataSource ds = null;
		try {
			ds = new FileRandomAccessDataSource(file);
			IsoFile isoFile = new IsoFile(ds);
			BoxFactory boxFactory = new BoxFactory();
			IsoInputStream isoIn = new IsoInputStream(isoFile.getOriginalIso());
			Box box = boxFactory.parseBox(isoIn, isoFile, null);
			if (box == null) {
				LogManager.getInstance().logMessage(LogMessage.ERROR,
					"The input file does not implement ISO Mpeg4 Standards");
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (ds != null) ds.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void adapt(File file, ParserContext ctx) {
		FileRandomAccessDataSource ds = null;
		try {
			ctx.fireStartParseEvent("MPEG4");
			writeFileInfo(file, ctx);
			ctx.fireEndParseEvent("MPEG4");
			ds = new FileRandomAccessDataSource(file);
			IsoFile isoFile = new IsoFile(ds);
			BoxFactory boxFactory = new BoxFactory();
			IsoInputStream isoIn = new IsoInputStream(isoFile.getOriginalIso());
			Box box = boxFactory.parseBox(isoIn, isoFile, null);
			LogManager.getInstance().logMessage(LogMessage.DEBUG, box.getDisplayName());
			LogManager.getInstance().logMessage(LogMessage.DEBUG, box.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ds != null) ds.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public String getDescription() {
		return "Adapts MPEG4 files including mp4 and m4a using the mp4parser library http://code.google.com/p/mp4parser/";
	}

	public String getInputType() {

		return "video/mp4";
	}

	public String getName() {

		return "MPEG4 format adapter";
	}

	public String getOutputType() {

		return "mpeg4.dtd";
	}

	public String getVersion() {

		return "1.0";
	}

}
