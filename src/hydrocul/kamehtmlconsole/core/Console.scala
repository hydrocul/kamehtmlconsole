package hydrocul.kamehtmlconsole.core;

import hydrocul.util.ObjectPool;

trait Console {

  def getLoadingHtml: String;

  def newLineGroup(): LineGroup;

  def newLineGroupBefore(after: LineGroup): LineGroup;

  def newLineGroupAfter(before: LineGroup): LineGroup;

  def size: Int;

  def getLinesInfo: LinesInfo;

  def createScreen(): Screen;

}

private[kamehtmlconsole] class ConsoleImpl(objectPool: ObjectPool, baseUrl: String) extends Console {

  import ConsoleImpl._;

  @volatile private var groups: Vector[LineGroupImpl] = Vector();

  def getLoadingHtml = "<img src=\"" + baseUrl + "etc/loading.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"loading\" />";

  def newLineGroup(): LineGroup = {
    val ret = new LineGroupImpl(objectPool, groupListener);
    groups = groups :+ ret;
    return ret;
  }

  def newLineGroupBefore(after: LineGroup): LineGroup = {
    val i = groups.indexOf(after);
    if(i < 0){
      return new LineGroupImpl(objectPool, groupListener);
    }
    val ret = new LineGroupImpl(objectPool, groupListener);
    groups = (groups.take(i) :+ ret) ++ groups.drop(i);
    ret;
  }

  def newLineGroupAfter(before: LineGroup): LineGroup = {
    val i = groups.indexOf(before) + 1;
    if(i <= 0){
      return new LineGroupImpl(objectPool, groupListener);
    }
    val ret = new LineGroupImpl(objectPool, groupListener);
    groups = (groups.take(i) :+ ret) ++ groups.drop(i);
    ret;
  }

  private def limitLineCount(){
    // TODO #limitLineCount
  }

  private val groupListener = new LineGroupImpl.Listener {
    def limitLineCount(){
      ConsoleImpl.this.limitLineCount();
    }
  }

  def size: Int = groups.map(_.size).sum;

  def getLinesInfo: LinesInfo = {
    val g = groups.map(_.getLinesInfo);
    val infos = g.flatMap(_.lines);
    val counter = if(g.isEmpty) 0 else g.map(_.counter).max;
    LinesInfo(infos, counter);
  }

  def createScreen(): Screen = new ScreenImpl(this);

}

private[kamehtmlconsole] object ConsoleImpl {

  val maxLineCount = 500;

}
