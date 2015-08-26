package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.tiff.TIFFAdapter;

public class TestTiffAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new TIFFAdapter();
	}

	@Override
	public String getFilename() {
		return "tiffAdapterTest.tif";
	}

}
