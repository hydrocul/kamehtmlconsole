package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

/**
 * @param baseUrl ends with "/"
 */
class Container(objectPool: ObjectPool, baseUrl: String){

  private var listeners: Vector[ConsoleListener] = Vector();

  private val handler = new HttpHandlerImpl(objectPool, baseUrl, new ConsoleListener(){

    def input(event: KeyEvent){
      listeners.foreach(_.input(event));
    }

  });

  def addListener(listener: ConsoleListener){
    listeners = listeners :+ listener;
  }

  def getHandler: HttpHandler = handler;

  /**
   * `Console` のインスタンスを生成する。
   * 
   * ここで返す `Console` はマルチスレッド非対応。`Console` とそれに含まれる
   * `ConsoleLineGroup`, `ConsoleLine` はすべて1つのスレッドからのアクセスが必要。
   */
  def createConsole(): Console = new ConsoleImpl(objectPool, baseUrl);

}

