
import scala.testing.SUnit;

import hydrocul.kametest.{ Test => KTest };

object Test {

  def main(args: Array[String]){

    hydrocul.kamehtmlconsole.core.LineBufferTest.test();
    hydrocul.kamehtmlconsole.html.HtmlConsoleTest.test();

    println("Errors: %d / %d".format(KTest.getErrorCount, KTest.getTotalCount));

    if(KTest.errorExists){
      System.exit(1);
    }
    System.exit(0);

  }

}
