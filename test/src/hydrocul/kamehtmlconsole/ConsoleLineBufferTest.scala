package hydrocul.kamehtmlconsole;

import hydrocul.kametest.Test;

object ConsoleLineBufferTest {

  def test(){
    testGetTextFromHtml("abc", "abc");
    testGetTextFromHtml("abc<a>def</a>", "abcdef");
    testGetTextFromHtml("abc<a>de<span class=\"aaa\">f</span></a>", "abcdef");
    testGetTextFromHtml("", "");
  }

  private def testGetTextFromHtml(html: String, expected: String){
    val actual = ConsoleLineBufferImpl.getTextFromHtml(html);
    Test.assertEquals(html, expected, actual);
  }

}
