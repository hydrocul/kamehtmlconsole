package hydrocul.kamehtmlconsole.core;

import hydrocul.kametest.Test;

object LineBufferTest {

  def test(){
    testGetTextFromHtml("abc", "abc");
    testGetTextFromHtml("abc<a>def</a>", "abcdef");
    testGetTextFromHtml("abc<a>de<span class=\"aaa\">f</span></a>", "abcdef");
    testGetTextFromHtml("", "");
  }

  private def testGetTextFromHtml(html: String, expected: String){
    val actual = LineBufferImpl.getTextFromHtml(html);
    Test.assertEquals(html, expected, actual);
  }

}
