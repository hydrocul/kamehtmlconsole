package hydrocul.kamehtmlconsole;

import hydrocul.kametest.Test;

object GoogleImeEngineTest {

  def test(){
    testSub("にほんご", "にほんご", "日本語");
    testSub("ここではきものを", "きものを", "着物を");
  }

  private def testSub(text: String, text2: String, expected: String){
    val result: List[GoogleImeEngine.APIResponseItem] = GoogleImeEngine.accessGoogleAPI(text);
    Test.assertEquals(text, text2, result.last.word);
    Test.assertEquals(text, expected, result.last.kanji.head);
  }

}
