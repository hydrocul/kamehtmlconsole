package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

// baseUrl: ends with "/"
class ConsoleContainer(objectPool: ObjectPool, listener: ConsoleListener, baseUrl: String){

  private val handler = new ConsoleHandlerImpl(objectPool);

  def getHandler: ConsoleHandler = handler;

}

