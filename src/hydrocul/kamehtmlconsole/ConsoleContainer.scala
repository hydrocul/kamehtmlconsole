package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

// baseUrl: ends with "/"
class ConsoleContainer(objectPool: ObjectPool, baseUrl: String){

  private var listeners: IndexedSeq[ConsoleListener] = Vector();

  private val handler = new ConsoleHandlerImpl(objectPool, new ConsoleListener(){

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

  private val console = new Console(objectPool, baseUrl);

  def addListener(listener: ConsoleListener){
    listeners = listeners :+ listener;
  }

  def getHandler: ConsoleHandler = handler;

}

