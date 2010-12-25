package hydrocul.util;

object ScalaUtil {

  def using[A <: { def close(); }, B](resource: A)(p: A=>B): B = {
    try {
      p(resource);
    } finally {
      resource.close();
    }
  }

  implicit def block2runnable(p: =>Unit): Runnable = new Runnable {
    def run(){
      p;
    }
  }

}

