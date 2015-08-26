/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package nz.govt.natlib.meta.test;

import java.io.File;

import nz.govt.natlib.AdapterFactory;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.meta.HarvestSource;
import nz.govt.natlib.meta.HarvestStatus;
import nz.govt.natlib.meta.Harvester;
import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.Configuration;
import nz.govt.natlib.meta.harvester.POCHarvester;

public class Test2 {

	public static void parseFile(File file, int con) throws Exception {

		Config.getInstance();

		DataAdapter adapter = AdapterFactory.getInstance().getAdapter(file);

		if (adapter != null) {
			System.out.println("Adaper");
			System.out.println("  Name :" + adapter.getName());
			System.out.println("  Version :" + adapter.getVersion());
			System.out.println("  Description :" + adapter.getDescription());
			Configuration config = (Configuration) Config.getInstance()
					.getAvailableConfigs().get(con);
			System.out.println("Using Configuration :" + config.getName());
			File outDir = new File(config.getOutputDirectory());
			outDir.mkdirs(); // precaution, in case the directories are not
								// there on this machine...
			System.out.println("Output: " + outDir.getAbsolutePath());

			Harvester harvester = new POCHarvester();
			Timer.start("adapter.handle");
			harvester.harvest(config, new FileWrapper(file), null, null);
			Timer.end("adapter.handle");
		}

		Timer.report();
	}

	private static class FileWrapper implements HarvestSource {

		private File file;

		public int getType() {
			return HarvestSource.SIMPLE;
		}

		public FileWrapper(File file) {
			this.file = file;
		}

		public File getFile() {
			return file;
		}

		public String getName() {
			return file.getName();
		}

		public HarvestSource[] getChildren() {
			return null;
		}

		public void setStatus(HarvestStatus status, String message) {
			return;
		}

	}

	public static void main(String args[]) {
		// System.out.println(HTMLParser.stripQuotes("HELLO"));
		// System.out.println(HTMLParser.stripQuotes("HELLO followed by space
		// "));
		// System.out.println(HTMLParser.stripQuotes("ISO802-33-1"));
		// System.out.println(HTMLParser.stripQuotes(" \"value\""));
		// System.out.println(HTMLParser.stripQuotes(" \"value\" "));
		// System.out.println(HTMLParser.stripQuotes(" \"valfghue\" $%^G %"));
		// System.out.println(HTMLParser.stripQuotes(" ").length());
		// System.out.println(HTMLParser.stripQuotes("").length());
		// System.out.println(HTMLParser.stripQuotes(null));
		// if (true) return;

		try {

			File[] testFiles = new File[] {
					/* 0 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\003856.tif"),
					/* 1 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\003856.tif"),
					/* 2 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\003856.tif"),
					/* 3 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\testmovi1.mpg"),
					/* 4 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\PARTYW2.DOC"),
					/* 5 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\APPLAUSE.WAV"),
					/* 6 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\CARBRAKE.WAV"),
					/* 7 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\CD48Track28.wav"),
					/* 8 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\Coffee Bean.bmp"),
					/* 9 */new File(
							"C:\\Projects\\NLNZ_Metadatavv\\digital masters\\dummy.DOC"),
					/* 10 */new File(
							"C:\\Projects\\NLNZ_Metadata\\harvested\\native\\003856.tif.xml"),
					/* 11 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\jpgs\\DSCF0006.JPG"),
					/* 12 */new File(
							"C:\\Projects\\NLNZ_Metadata\\digital masters\\jpgs\\alien1008.JPG"),

					/* 13 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\A PSALM OF LIFE wordv97-v2000.doc"),
					/* 14 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\wordXP\\Office 2002.doc"),
					/* 15 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\wordXP\\Office 2003.doc"),
					/* 16 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\Works CD (Delete)\\JMSpapers 6.wps"),
					/* 17 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\Works CD (Delete)\\Plate List.wdb"),
					/* 18 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\Works CD (Delete)\\SHEET1.WKS"),
					/* 19 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\Works CD (Delete)\\Works from Word2000.wps"),

					// Excel
					/* 20 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\excel\\test.xls"),

					// Power Point
					/* 21 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\powerpoint\\test.ppt"),

					// BWF
					/* 22 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\bwf\\short1.wav"),

					// OpenOffice
					/* 23 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\openoffice\\openoffice_slideshow_test1.sxw.sxi"),
					/* 24 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\openoffice\\openoffice_ss_test1.sxw.sxc"),
					/* 25 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\openoffice\\openoffice_txt_test1.sxw"),

					// html
					/* 26 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\html\\Alleyway Home Page.htm"),
					/* 27 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\html\\XTRA Home.htm"),
					/* 28 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\html\\Vignette - Content Management and Portal Solutions.htm"),
					/* 29 */new File(
							"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\html\\Dublin Core Metadata Initiative (DCMI).htm"), };

			System.out.println("Adapter Tester v0.1\n");

			// test 1 - everything in a directory...
			File dir = new File(
					"C:\\Projects\\NLNZ_Metadata\\Digital Masters\\error");
			testFiles = dir.listFiles();

			// test 2
			// testFiles = testFiles;

			// test 3 - one file...
			// testFiles = new File[] {new
			// File("C:\\Projects\\NLNZ_Metadata\\Digital
			// Masters\\html\\Converting poorly formed HTML into well-formed
			// XML.htm")};

			for (int i = 0; i < testFiles.length; i++) {
				for (int t = 0; t < 2; t++) {

					// may wish to limit tests...
					if (t == 0) {
						continue;
					}
					if (t == 1) {
						// continue;
					}

					if (testFiles[i].exists()) {
						System.out.println("Testing :" + testFiles[i]
								+ " with config " + t);
						// do test i with config t
						parseFile(testFiles[i], t);
					} else {
						System.err.println("file :" + testFiles[i]
								+ " not found");
					}
				}
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

}
