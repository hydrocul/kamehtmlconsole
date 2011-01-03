package hydrocul.kamehtmlconsole;

import java.util.Timer;

import hydrocul.util.ObjectPool;
import hydrocul.util.ScalaUtil.block2runnable;

/**
 * @param baseUrl ends with "/"
 */
class Container(objectPool: ObjectPool, baseUrl: String, timer: Timer){

  private var listeners: Vector[ConsoleListener] = Vector();

  def addListener(listener: ConsoleListener){
    listeners = listeners :+ listener;
  }

  /**
   * `Console` のインスタンスを生成する。
   */
  def createConsole(): Console = new ConsoleImpl(objectPool, baseUrl, timer);

}

