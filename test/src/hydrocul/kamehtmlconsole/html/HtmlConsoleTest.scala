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
    val htmlWriter = htmlConsole.getHtmlPrintWriter;

    testRefreshJavaScriptCode(screen, 1, "");

    writer.print("abc");
    Thread.sleep(200);
    testRefreshJavaScriptCode(screen, 2, "");

    writer.flush();
    Thread.sleep(200);
    testRefreshJavaScriptCode(screen, 3, """$("#console").prepend("<div id=\"1\" class=\"line\">abc</div>");initLine("1");""" + "\n");

    writer.print("def");
    Thread.sleep(200);
    testRefreshJavaScriptCode(screen, 4, "");

    writer.flush();
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 5, """$("#1").html("abcdef");initLine("1");""" + "\n");

    writer.print("\n");
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 6, "");

    writer.flush();
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 7, """$("#1").after("<div id=\"2\" class=\"line\">&nbsp;</div>");initLine("2");""" + "\n");

    writer.print("&");
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 8, "");

    writer.flush();
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 9, """$("#2").html("&amp;");initLine("2");""" + "\n");

    writer.print("<");
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 10, "");

    writer.flush();
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 11, """$("#2").html("&amp;&lt;");initLine("2");""" + "\n");

    writer.print(">\n");
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 12, """$("#2").html("&amp;&lt;&gt;");initLine("2");""" + "\n");

    htmlWriter.print("&lt;&amp;&gt;");
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 13, "");

    htmlWriter.flush();
    Thread.sleep(100);
    testRefreshJavaScriptCode(screen, 14, """$("#2").after("<div id=\"3\" class=\"line\">&lt;&amp;&gt;</div>");initLine("3");""" + "\n");

  }

  private def testRefreshJavaScriptCode(screen: ConsoleScreen, num: Int, expected: String){
    val actual = screen.getRefreshJavaScriptCode();
    Test.assertEquals(num.toString, expected, actual);
  }

}
