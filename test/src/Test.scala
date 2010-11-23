
import scala.testing.SUnit;

import hydrocul.kametest.{ Test => KTest };

object Test {

  def main(args: Array[String]){

    hydrocul.kamehtmlconsole.ConsoleLineBufferTest.test();

    if(KTest.errorExists){
      System.exit(1);
    }

  }

}
