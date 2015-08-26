package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.any.DefaultAdapter;

public class TestDefaultAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new DefaultAdapter();
	}

	@Override
	public String getFilename() {
		return "defaultAdapterTest.txt";
	}

}
