package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.jpg.JpgAdapter;

public class TestJpgAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new JpgAdapter();
	}

	@Override
	public String getFilename() {
		return "jpgAdapterTest.jpg";
	}

}
