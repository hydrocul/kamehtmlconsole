package hydrocul.kamehtmlconsole.core;

import hydrocul.util.ObjectPool;

trait LineGroup {

  def getLines: Vector[LineBuffer];

  def newLine(): LineBuffer;

  def newLineBefore(after: LineBuffer): LineBuffer;

  def newLineAfter(before: LineBuffer): LineBuffer;

  def size: Int;

  def getLinesInfo: LinesInfo;

}

private[kamehtmlconsole] class LineGroupImpl(objectPool: ObjectPool,
  listener: LineGroupImpl.Listener) extends LineGroup {

  @volatile private var lines: Vector[LineBuffer] = Vector();

  def getLine: Vector[ConsoleLineBuffer] = lines;

  def newLine(): LineBuffer = {
    val ret = new LineBufferImpl(objectPool);
    lines = lines :+ ret;
    limitLineCount();
    ret;
  }

  def newLineBefore(after: LineBuffer): LineBuffer = {
    val i = lines.indexOf(after);
    if(i < 0){
      return new LineBufferImpl(objectPool);
    }
    val ret = new LineBufferImpl(objectPool);
    lines = (lines.take(i) :+ ret) ++ lines.drop(i);
    limitLineCount();
    ret;
  }

  def newLineAfter(before: LineBuffer): LineBuffer = {
    val i = lines.indexOf(before) + 1;
    if(i <= 0){
      return new LineBufferImpl(objectPool);
    }
    val ret = new LineBufferImpl(objectPool);
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

  def getLinesInfo: LinesInfo = {
    val infos = lines.map(_.getLineInfo).toIndexedSeq;
    val counter = if(lines.isEmpty) 0 else infos.map(_.counter).max;
    LinesInfo(infos, counter);
  }

}

private[kamehtmlconsole] object LineGroupImpl {

  trait Listener {
    def limitLineCount();
  }

}

