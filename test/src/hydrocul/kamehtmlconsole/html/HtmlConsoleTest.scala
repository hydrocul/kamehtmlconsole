package hydrocul.kamehtmlconsole.html;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import hydrocul.kametest.Test;
import hydrocul.util.ObjectPool;

import hydrocul.kamehtmlconsole._;

object HtmlConsoleTest {

  def test(){
    val executor = new ScheduledThreadPoolExecutor(4);
    val objectPool = new ObjectPool(executor);
    val consoleContainer = new ConsoleContainer(objectPool, "/");
    val console = consoleContainer.createConsole();
    val htmlConsole = new HtmlConsole(console, console.newLineGroup());
    
  }

  private def testGetTextFromHtml(html: String, expected: String){
//    val actual = ConsoleLineBufferImpl.getTextFromHtml(html);
//    Test.assertEquals(html, expected, actual);
  }

}
