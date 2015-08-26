package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.bmp.BitmapAdapter;

public class TestBmpAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new BitmapAdapter();
	}

	@Override
	public String getFilename() {
		return "bmpAdapterTest.bmp";
	}

}
