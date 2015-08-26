package nz.govt.natlib.adapter.pdfbox;

public class PDFPermissions {
	private int permissions;
	private int version;
	
	public PDFPermissions(int permissions, int version) {
		this.permissions = permissions;
		this.version = version;
	}
	
	public boolean allowPrint() {
		return (permissions >>> 2 & 0x01) > 0;
	}
	
	public boolean allowCopy() {
		return (permissions >>> 4 & 0x01) > 0;
	}
	
	public boolean allowTextNotes() { 
		return (permissions >>> 5 & 0x01) > 0;
	}
	
	public boolean allowModify() {
		return (permissions >>> 3 & 0x01) > 0;
	}

	
}
