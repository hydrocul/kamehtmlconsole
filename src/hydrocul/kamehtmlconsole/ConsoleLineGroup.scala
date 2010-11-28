package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

trait ConsoleLineGroup {

  def getLine: IndexedSeq[ConsoleLineBuffer];

  def newLine(): ConsoleLineBuffer;

  def newLineBefore(after: ConsoleLineBuffer): ConsoleLineBuffer;

  def newLineAfter(before: ConsoleLineBuffer): ConsoleLineBuffer;

  def size: Int;

  def getLinesInfo: ConsoleLinesInfo;

}

private[kamehtmlconsole] class ConsoleLineGroupImpl(objectPool: ObjectPool,
  listener: ConsoleLineGroupImpl.Listener) extends ConsoleLineGroup {

  @volatile private var lines: IndexedSeq[ConsoleLineBuffer] = Vector();

  def getLine: IndexedSeq[ConsoleLineBuffer] = lines;

  def newLine(): ConsoleLineBuffer = {
    val ret = new ConsoleLineBufferImpl(objectPool);
    lines = lines :+ ret;
    limitLineCount();
    ret;
  }

  def newLineBefore(after: ConsoleLineBuffer): ConsoleLineBuffer = {
    val i = lines.indexOf(after);
    if(i < 0){
      return new ConsoleLineBufferImpl(objectPool);
    }
    val ret = new ConsoleLineBufferImpl(objectPool);
    lines = (lines.take(i) :+ ret) ++ lines.drop(i);
    limitLineCount();
    ret;
  }

  def newLineAfter(before: ConsoleLineBuffer): ConsoleLineBuffer = {
    val i = lines.indexOf(before) + 1;
    if(i <= 0){
      return new ConsoleLineBufferImpl(objectPool);
    }
    val ret = new ConsoleLineBufferImpl(objectPool);
    lines = (lines.take(i) :+ ret) ++ lines.drop(i);
    limitLineCount();
    ret;
  }

  private def limitLineCount(){
    val s = lines.size;
    if(s > ConsoleImpl.maxLineCount){
      lines = lines.drop(s - ConsoleImpl.maxLineCount);
    }
  }

  def size = lines.size;

  def getLinesInfo: ConsoleLinesInfo = {
    val infos = lines.map(_.getLineInfo).toIndexedSeq;
    val counter = if(lines.isEmpty) 0 else infos.map(_.counter).max;
    ConsoleLinesInfo(infos, counter);
  }

}

private[kamehtmlconsole] object ConsoleLineGroupImpl {

  trait Listener {
    def limitLineCount();
  }

}

