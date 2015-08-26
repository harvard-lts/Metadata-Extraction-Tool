package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.wav.WaveAdapter;

public class TestWavAdapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new WaveAdapter();
	}

	@Override
	public String getFilename() {
		return "wavAdapterTest.wav";
	}
}
