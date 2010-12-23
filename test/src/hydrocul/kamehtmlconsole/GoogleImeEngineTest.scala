package hydrocul.kamehtmlconsole;

import hydrocul.kametest.Test;

object GoogleImeEngineTest {

  def test(){
    testExtractRoma("aiueo", "aiueo");
    testExtractRoma("aa go-ka-to", "go-ka-to");
    testExtractRoma("あいうえおaiueo", "aiueo");
    testExtractRoma("aiueo ", "");
    testExtractRoma("aiueo.", "");
    testGoogleAPI("にほんご", "にほんご", "日本語");
    testGoogleAPI("ここではきものを", "きものを", "着物を");
  }

  private def testSearchSuggestions(text: String, expected: String){
    
  }

  private def testExtractRoma(text: String, roma: String){
    val actual = GoogleImeEngine.extractRoma(text);
    Test.assertEquals(text, roma, actual);
  }

  private def testGoogleAPI(text: String, text2: String, expected: String){
    val result: List[GoogleImeEngine.APIResponseItem] = GoogleImeEngine.accessGoogleAPI(text);
    Test.assertEquals(text, text2, result.last.word);
    Test.assertEquals(text, expected, result.last.kanji.head);
  }

}
