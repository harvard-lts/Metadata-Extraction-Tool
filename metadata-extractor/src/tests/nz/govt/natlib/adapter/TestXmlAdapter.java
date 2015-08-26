package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.xml.XMLAdapter;

public class TestXmlAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new XMLAdapter();
	}

	@Override
	public String getFilename() {
		return "xmlAdapterTest.xml";
	}

}
