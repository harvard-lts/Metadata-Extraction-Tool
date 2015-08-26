package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.openoffice.OpenOfficeAdapter;

public class TestOpenOfficeAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new OpenOfficeAdapter();
	}

	@Override
	public String getFilename() {
		return "openOfficeTest.odt";
	}

}
