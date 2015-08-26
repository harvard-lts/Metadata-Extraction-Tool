package nz.govt.natlib.adapter;


import nz.govt.natlib.adapter.mp3.MP3Adapter;

public class TestMP3Adapter extends TestDataAdapter {

	@Override
	public DataAdapter getAdapter() {
		return new MP3Adapter();
	}

	@Override
	public String getFilename() {
		return "JonnyGretsch-None-Sign-Myself.mp3";
	}

}
