package hydrocul.kamehtmlconsole;

import java.util.Timer;

import hydrocul.util.ObjectPool;
import hydrocul.util.ScalaUtil.block2runnable;

/**
 * @param baseUrl ends with "/"
 */
class Container(objectPool: ObjectPool, baseUrl: String, timer: Timer){

  private var listeners: Vector[ConsoleListener] = Vector();

/*
  private val handler = new HttpHandlerImpl(objectPool, baseUrl, new ConsoleListener(){

    def input(event: KeyEvent){
      listeners.foreach(_.input(event, {})); // TODO 入力を複数同時に処理しない仕組みが必要
    }

  });
*/

  def addListener(listener: ConsoleListener){
    listeners = listeners :+ listener;
  }

//  def getHandler: HttpHandler = handler;

  /**
   * `Console` のインスタンスを生成する。
   */
  def createConsole(): Console = new ConsoleImpl(objectPool, baseUrl, timer);

}

