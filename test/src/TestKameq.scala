
import java.math.BigInteger;

import hydrocul.kameq.scala.Pipe._;

object TestKameq {

  def main(args: Array[String]){
    exec {
      println("test 1");
    }
    exec {
      println("test 2-1");
    } | { (u: Unit) =>
      println("test 2-2");
      2+1;
    } | { (i: Int) =>
      println("test 2-" + i);
    }
    (1 to 5).foreach { i =>
      timer("test 3-" + i, test3);
      timer("test 4-" + i, test4);
    }
  }

  private def timer(msg: String, p: => BigInteger){
    println(msg);
    val start = System.currentTimeMillis;
    val x = p;
    val time = System.currentTimeMillis - start;
    println("time: %s, x: %s".format(time, x.toString.substring(0, 10)));
  }

  private def test3(): BigInteger = {
    var x = BigInteger.ONE;
    (2 to 10000).foreach { i =>
      x = x.multiply(new BigInteger(Integer.toString(i)));
    }
    x;
  }

  private def test4(): BigInteger = {
    val a = new AnyRef;
    var result = BigInteger.ONE;
    val count = new java.util.concurrent.CountDownLatch(100);
    val count2 = new java.util.concurrent.atomic.AtomicInteger(100);
    (0 until 100).foreach { i =>
      exec {
        var x = BigInteger.ONE;
        (1 to 100).foreach { j =>
          if(j % 10 == 0){
//            println("DEBUG " + (i * 100 + j));
          }
          x = x.multiply(new BigInteger(Integer.toString(i * 100 + j)));
        }
        count2.decrementAndGet();
        x;
      } | synpipe(a){ x: BigInteger =>
        result = result.multiply(x);
        count.countDown();
      };
    }
    count.await();
    result;
  }

}
