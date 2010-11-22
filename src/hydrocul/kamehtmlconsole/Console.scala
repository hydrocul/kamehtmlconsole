package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

trait Console {

  def getLoadingHtml: String;

  def newLineGroup(): ConsoleLineGroup;

  def newLineGroupBefore(after: ConsoleLineGroup): ConsoleLineGroup;

  def newLineGroupAfter(before: ConsoleLineGroup): ConsoleLineGroup;

}

private[kamehtmlconsole] class ConsoleImpl(objectPool: ObjectPool, baseUrl: String) extends Console {

  import ConsoleImpl._;

  @volatile private var groups: IndexedSeq[ConsoleLineGroupImpl] = Vector();

  def getLoadingHtml = "<img src=\"" + baseUrl + "etc/loading.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"loading\" />";

  def newLineGroup(): ConsoleLineGroup = {
    synchronized {
      val ret = new ConsoleLineGroupImpl(objectPool, groupListener);
      groups = groups :+ ret;
      return ret;
    }
  }

  def newLineGroupBefore(after: ConsoleLineGroup): ConsoleLineGroup = {
    synchronized {
      val i = groups.indexOf(after);
      if(i < 0){
        return new ConsoleLineGroupImpl(objectPool, groupListener);
      }
      val ret = new ConsoleLineGroupImpl(objectPool, groupListener);
      groups = (groups.take(i) :+ ret) ++ groups.drop(i);
      ret;
    }
  }

  def newLineGroupAfter(before: ConsoleLineGroup): ConsoleLineGroup = {
    synchronized {
      val i = groups.indexOf(before) + 1;
      if(i <= 0){
        return new ConsoleLineGroupImpl(objectPool, groupListener);
      }
      val ret = new ConsoleLineGroupImpl(objectPool, groupListener);
      groups = (groups.take(i) :+ ret) ++ groups.drop(i);
      ret;
    }
  }

  private def limitLineCount(){
    // TODO
  }

  private val groupListener = new ConsoleLineGroupImpl.Listener {
    def limitLineCount(){
      ConsoleImpl.this.limitLineCount();
    }
  }

  private[kamehtmlconsole] def size: Int = groups.map(_.size).sum;

  private[kamehtmlconsole] def getLines: IndexedSeq[ConsoleLine] = groups.flatMap(_.getLines);

}

private[kamehtmlconsole] object ConsoleImpl {

  val maxLineCount = 500;

}
