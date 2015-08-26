package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.html.HTMLAdapter;

public class TestHtmlAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new HTMLAdapter();
	}

	@Override
	public String getFilename() {
		return "htmlAdapterTest.html";
	}

}
