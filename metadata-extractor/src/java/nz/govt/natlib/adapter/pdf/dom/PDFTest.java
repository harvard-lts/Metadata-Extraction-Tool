package nz.govt.natlib.adapter.pdf.dom;

import java.io.File;

public class PDFTest {
	public static void main(String[] args) throws Exception {
		PDFDocument doc = new PDFDocument(new File(args[0]));
		System.out.println(doc.getPageLayout());
	}
}
