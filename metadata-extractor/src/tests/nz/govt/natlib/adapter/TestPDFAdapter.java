package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.pdfbox.PDFBoxAdapter;

public class TestPDFAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new PDFBoxAdapter();
	}

	@Override
	public String getFilename() {
		return "pdfAdapterTest.pdf";
	}

}
