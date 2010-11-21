package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

class Console(objectPool: ObjectPool, baseUrl: String){

  import Console._;

  def getLoadingHtml = "<img src=\"" + baseUrl + "etc/loading.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"loading\" />";

}

object Console {

  val maxLineCount = 500;

}
