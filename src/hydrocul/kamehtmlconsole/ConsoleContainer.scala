package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

/**
 * @param baseUrl ends with "/"
 */
class ConsoleContainer(objectPool: ObjectPool, baseUrl: String){

  private var listeners: IndexedSeq[ConsoleListener] = Vector();

  private val handler = new ConsoleHandlerImpl(objectPool, baseUrl, new ConsoleListener(){

    def userText(count: Int, text: String){
      listeners.foreach(_.userText(count, text));
    }

    def specialKey(count: Int, key: String){
      listeners.foreach(_.specialKey(count, key));
    }

    def specialCommand(count: Int, command: String){
      listeners.foreach(_.specialCommand(count, command));
    }

  });

  def addListener(listener: ConsoleListener){
    listeners = listeners :+ listener;
  }

  def getHandler: ConsoleHandler = handler;

  /**
   * `Console` のインスタンスを生成する。
   * 
   * ここで返す `Console` はマルチスレッド非対応。`Console` とそれに含まれる
   * `ConsoleLineGroup`, `ConsoleLine` はすべて1つのスレッドからのアクセスが必要。
   */
  def createConsole(): Console = new ConsoleImpl(objectPool, baseUrl);

}

