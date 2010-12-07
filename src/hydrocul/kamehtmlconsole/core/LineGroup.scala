package hydrocul.kamehtmlconsole.core;

import hydrocul.util.ObjectPool;

trait LineGroup {

  def getLines: Vector[LineBuffer];

  def newLine(): LineBuffer;

  def newLineBefore(after: LineBuffer): LineBuffer;

  def newLineAfter(before: LineBuffer): LineBuffer;

  def size: Int;

  def getLinesInfo: Vector[LineInfo];

}

private[kamehtmlconsole] class LineGroupImpl(objectPool: ObjectPool,
  listener: LineGroupImpl.Listener) extends LineGroup {

  @volatile private var lines: Vector[LineBuffer] = Vector();

  def getLines: Vector[LineBuffer] = lines;

  def newLine(): LineBuffer = {
    val ret = new LineBufferImpl(objectPool);
    synchronized {
      lines = lines :+ ret;
    }
    limitLineCount();
    ret;
  }

  def newLineBefore(after: LineBuffer): LineBuffer = {
    val ret = new LineBufferImpl(objectPool);
    synchronized {
      val i = lines.indexOf(after);
      if(i < 0){
        return ret;
      }
      lines = (lines.take(i) :+ ret) ++ lines.drop(i);
    }
    limitLineCount();
    ret;
  }

  def newLineAfter(before: LineBuffer): LineBuffer = {
    val ret = new LineBufferImpl(objectPool);
    synchronized {
      val i = lines.indexOf(before) + 1;
      if(i <= 0){
        return ret;
      }
      lines = (lines.take(i) :+ ret) ++ lines.drop(i);
    }
    limitLineCount();
    ret;
  }

  private def limitLineCount(){
    synchronized {
      val s = lines.size;
      if(s > ConsoleImpl.maxLineCount){
        lines = lines.drop(s - ConsoleImpl.maxLineCount);
      }
    }
  }

  def size = lines.size;

  def getLinesInfo: Vector[LineInfo] = lines.map(_.getLineInfo);

}

private[kamehtmlconsole] object LineGroupImpl {

  trait Listener {
    def limitLineCount();
  }

}

