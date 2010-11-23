package hydrocul.kamehtmlconsole;

import scala.testing.SUnit;

object ConsoleLineBufferTest {

  def testCases: IndexedSeq[SUnit.Test] = {
    Vector(
      testGetTextFromHtml("abc", "abc"),
      testGetTextFromHtml("abc<a>def</a>", "abcdef"),
      testGetTextFromHtml("abc<a>de<span class=\"aaa\">f</span></a>", "abcdef"),
      testGetTextFromHtml("", "")
    );
  }

  private def testGetTextFromHtml(html: String, expected: String): SUnit.Test = new SUnit.TestCase(html){
    override def enableStackTrace = false;
    def runTest(){
      val actual = ConsoleLineBufferImpl.getTextFromHtml(html);
      assertEquals(expected, actual);
    }
  }

}
