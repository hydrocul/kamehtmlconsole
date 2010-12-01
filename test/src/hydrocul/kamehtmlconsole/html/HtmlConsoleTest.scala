package hydrocul.kamehtmlconsole.html;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import hydrocul.kametest.Test;
import hydrocul.util.NumberObjectPool;

import hydrocul.kamehtmlconsole._;

object HtmlConsoleTest {

  def test(){
    val executor = new ScheduledThreadPoolExecutor(4);
    val objectPool = new NumberObjectPool(executor);
    val consoleContainer = new ConsoleContainer(objectPool, "/");
    val console = consoleContainer.createConsole();
    val screen = console.createScreen();
    val htmlConsole = new HtmlConsole(console, console.newLineGroup());
    val writer = htmlConsole.getPrintWriter;
    testRefreshJavaScriptCode(screen, 1, "");
    writer.print("abc");
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 2, "");
    writer.flush();
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 3, """$("#console").prepend("<div id=\"1\" class=\"line\">abc</div>");initLine("1");""" + "\n");
  }

  private def testRefreshJavaScriptCode(screen: ConsoleScreen, num: Int, expected: String){
    val actual = screen.getRefreshJavaScriptCode();
    Test.assertEquals(num.toString, expected, actual);
  }

}
