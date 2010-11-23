package hydrocul.kametest;

import java.io.PrintWriter;
import java.io.StringWriter;

object Test {

  private var _errorExists: Boolean = false;

  def errorExists: Boolean = _errorExists;

  def assertEquals[A](name: String, expected: A, actual: A){
    if(actual==expected){
      return;
    }
    val msg = "[ERROR] %s: expedted: %s, actual: %s %s".format(name, expected, actual, {
      val sw = new StringWriter();
      val pw = new PrintWriter(sw);
      (new AssertionError()).printStackTrace(pw);
      sw.toString.split("\n")(2).trim;
    });
    println(msg);
    _errorExists = true;
  }

}
