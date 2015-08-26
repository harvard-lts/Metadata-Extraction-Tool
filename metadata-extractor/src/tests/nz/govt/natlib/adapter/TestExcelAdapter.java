package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.excel.ExcelAdapter;

public class TestExcelAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new ExcelAdapter();
	}

	@Override
	public String getFilename() {
		return "excelAdapterTest.xls";
	}

}
