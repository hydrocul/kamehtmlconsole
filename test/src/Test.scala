
import scala.testing.SUnit;

import hydrocul.kametest.{ Test => KTest };

object Test {

  def main(args: Array[String]){

    hydrocul.kamehtmlconsole.LineBufferTest.test();
    hydrocul.kamehtmlconsole.KeyEventTest.test();
    hydrocul.kamehtmlconsole.GoogleImeEngineTest.test();
    hydrocul.kamehtmlconsole.html.HtmlConsoleTest.test();

    println("Success: %d / %d".format(KTest.getSuccessCount, KTest.getTotalCount));

    if(KTest.errorExists){
      System.exit(1);
    }
    System.exit(0);

  }

}
