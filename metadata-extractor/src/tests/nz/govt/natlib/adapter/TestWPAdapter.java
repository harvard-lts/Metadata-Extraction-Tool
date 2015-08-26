package nz.govt.natlib.adapter;

import nz.govt.natlib.adapter.wordperfect.WPAdapter;

public class TestWPAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new WPAdapter();
	}

	@Override
	public String getFilename() {
		return "wpAdapterTest.wpd";
	}
}
