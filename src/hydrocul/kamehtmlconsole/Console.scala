package hydrocul.kamehtmlconsole;

import hydrocul.kameq.scala.Pipe._;
import hydrocul.util.ObjectPool;

trait Console {

  def getLoadingHtml: String;

  def newLineGroup(): ConsoleLineGroup;

  def newLineGroupBefore(after: ConsoleLineGroup): ConsoleLineGroup;

  def newLineGroupAfter(before: ConsoleLineGroup): ConsoleLineGroup;

  def size: Int;

  def getLinesInfo: ConsoleLinesInfo;

  def createScreen(): ConsoleScreen;

}

private[kamehtmlconsole] class ConsoleImpl(objectPool: ObjectPool, baseUrl: String) extends Console {

  import ConsoleImpl._;

  @volatile private var groups: IndexedSeq[ConsoleLineGroupImpl] = Vector();

  def getLoadingHtml = "<img src=\"" + baseUrl + "etc/loading.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"loading\" />";

  def newLineGroup(): ConsoleLineGroup = {
    val ret = new ConsoleLineGroupImpl(objectPool, groupListener);
    groups = groups :+ ret;
    return ret;
  }

  def newLineGroupBefore(after: ConsoleLineGroup): ConsoleLineGroup = {
    val i = groups.indexOf(after);
    if(i < 0){
      return new ConsoleLineGroupImpl(objectPool, groupListener);
    }
    val ret = new ConsoleLineGroupImpl(objectPool, groupListener);
    groups = (groups.take(i) :+ ret) ++ groups.drop(i);
    ret;
  }

  def newLineGroupAfter(before: ConsoleLineGroup): ConsoleLineGroup = {
    val i = groups.indexOf(before) + 1;
    if(i <= 0){
      return new ConsoleLineGroupImpl(objectPool, groupListener);
    }
    val ret = new ConsoleLineGroupImpl(objectPool, groupListener);
    groups = (groups.take(i) :+ ret) ++ groups.drop(i);
    ret;
  }

  private def limitLineCount(){
    // TODO #limitLineCount
  }

  private val groupListener = new ConsoleLineGroupImpl.Listener {
    def limitLineCount(){
      ConsoleImpl.this.limitLineCount();
    }
  }

  def size: Int = groups.map(_.size).sum;

  def getLinesInfo: ConsoleLinesInfo = {
    val g = groups.map(_.getLinesInfo);
    val infos = g.flatMap(_.lines).toIndexedSeq;
    val counter = if(g.isEmpty) 0 else g.map(_.counter).max;
    ConsoleLinesInfo(infos, counter);
  }

  def createScreen(): ConsoleScreen = new ConsoleScreenImpl(this);

}

private[kamehtmlconsole] object ConsoleImpl {

  val maxLineCount = 500;

}
