package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

class ConsoleContainer(objectPool: ObjectPool, listener: ConsoleListener){

  private val handler = new ConsoleHandlerImpl(objectPool);

  def getHandler: ConsoleHandler = handler;

}

