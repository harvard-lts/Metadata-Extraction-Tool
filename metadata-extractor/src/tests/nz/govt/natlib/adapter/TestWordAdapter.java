package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.word.WordAdapter;

public class TestWordAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new WordAdapter();
	}

	@Override
	public String getFilename() {
		return "wordAdapterTest.doc";
	}

}
