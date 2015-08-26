package nz.govt.natlib.adapter;


import org.junit.Ignore;

import nz.govt.natlib.adapter.works.DocAdapter;

public class TestWorksAdapter extends TestDataAdapter {

	
	@Ignore
	@Override
	public void testAcceptsFile() { 
		// Ignore this because we don't have a valid file to test. 
	}
	
	@Ignore
	@Override
	public void testAdapt() { 
		// Ignore this test because we don't a a valid file to test.
	}
	
	@Override
	public DataAdapter getAdapter() {
		return new DocAdapter();
	}

	@Override
	public String getFilename() {
		return "worksAdapterTest.wps";
	}

}
