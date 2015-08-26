package nz.govt.natlib.adapter;

import java.io.File;
import java.util.StringTokenizer;

import org.junit.Test;

import nz.govt.natlib.adapter.mpeg4.MPEG4Adapter;

public class TestMpeg4Adapter extends TestDataAdapter {


	@Override
	public DataAdapter getAdapter() {
		return new MPEG4Adapter();
	}

	@Override
	public String getFilename() {
	return "MPEG4AdapterTest.mp4";
	}	

}
