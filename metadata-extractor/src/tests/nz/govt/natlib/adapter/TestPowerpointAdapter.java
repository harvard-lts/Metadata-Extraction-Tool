package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.powerpoint.PowerPointAdapter;

public class TestPowerpointAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new PowerPointAdapter();
	}

	@Override
	public String getFilename() {
		return "powerpointAdapterTest.ppt";
	}

}
