package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

trait Console {

  def getLoadingHtml: String;

  def newLineGroup(): ConsoleLineGroup;

}

private[kamehtmlconsole] class ConsoleImpl(objectPool: ObjectPool, baseUrl: String) extends Console {

  import ConsoleImpl._;

  def getLoadingHtml = "<img src=\"" + baseUrl + "etc/loading.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"loading\" />";

  def newLineGroup(): ConsoleLineGroup = {
    val ret = new ConsoleLineGroupImpl(objectPool);
    // TODO newLineGroup
    return ret;
  }

}

object ConsoleImpl {

  private val maxLineCount = 500;

}
