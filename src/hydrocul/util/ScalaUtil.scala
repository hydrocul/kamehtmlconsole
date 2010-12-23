package hydrocul.util;

object ScalaUtil {

  implicit def block2runnable(p: =>Unit): Runnable = new Runnable {
    def run(){
      p;
    }
  }

}

