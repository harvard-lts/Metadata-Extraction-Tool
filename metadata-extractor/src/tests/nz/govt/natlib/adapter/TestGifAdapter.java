package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.gif.GIFAdapter;

public class TestGifAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new GIFAdapter();
	}

	@Override
	public String getFilename() {
		return "gifAdapterTest.gif";
	}

}
