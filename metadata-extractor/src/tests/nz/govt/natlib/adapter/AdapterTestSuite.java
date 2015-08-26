package nz.govt.natlib.adapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
  TestBmpAdapter.class,
  TestExcelAdapter.class,
  TestGifAdapter.class,
  TestHtmlAdapter.class,
  TestJpgAdapter.class,
  TestPDFAdapter.class,
  TestPowerpointAdapter.class,
  TestTiffAdapter.class,
  TestWavAdapter.class,
  TestWordAdapter.class,
  TestXmlAdapter.class,
  TestOpenOfficeAdapter.class,
  TestMP3Adapter.class,
  TestDefaultAdapter.class,
  TestWPAdapter.class,
  TestWorksAdapter.class
})
public class AdapterTestSuite {
}
