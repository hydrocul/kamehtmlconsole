
import scala.testing.SUnit;

object Test {

  def main(args: Array[String]){

    val r = new SUnit.TestResult();

    hydrocul.kamehtmlconsole.ConsoleLineBufferTest.testCases.foreach(_.run(r));

    r.failures.foreach(f => println(f.toString));
    if(r.failures.size > 0){
      System.exit(1);
    }

  }

}
