package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

trait ConsoleLineGroup {

  def newLine(): ConsoleLineBuffer;

  def newLineBefore(after: ConsoleLineBuffer): ConsoleLineBuffer;

  def newLineAfter(before: ConsoleLineBuffer): ConsoleLineBuffer;

}

private[kamehtmlconsole] class ConsoleLineGroupImpl(objectPool: ObjectPool,
  listener: ConsoleLineGroupImpl.Listener) extends ConsoleLineGroup {

  @volatile private var lines: IndexedSeq[ConsoleLineBuffer] = Vector();

  def newLine(): ConsoleLineBuffer = {
    synchronized {
      val ret = new ConsoleLineBufferImpl(objectPool);
      lines = lines :+ ret;
      limitLineCount();
      ret;
    }
  }

  def newLineBefore(after: ConsoleLineBuffer): ConsoleLineBuffer = {
    synchronized {
      val i = lines.indexOf(after);
      if(i < 0){
        return new ConsoleLineBufferImpl(objectPool);
      }
      val ret = new ConsoleLineBufferImpl(objectPool);
      lines = (lines.take(i) :+ ret) ++ lines.drop(i);
      limitLineCount();
      ret;
    }
  }

  def newLineAfter(before: ConsoleLineBuffer): ConsoleLineBuffer = {
    synchronized {
      val i = lines.indexOf(before) + 1;
      if(i <= 0){
        return new ConsoleLineBufferImpl(objectPool);
      }
      val ret = new ConsoleLineBufferImpl(objectPool);
      lines = (lines.take(i) :+ ret) ++ lines.drop(i);
      limitLineCount();
      ret;
    }
  }

  private def limitLineCount(){
    val s = lines.size;
    if(s > ConsoleImpl.maxLineCount){
      lines = lines.drop(s - ConsoleImpl.maxLineCount);
    }
  }

  private[kamehtmlconsole] def size = lines.size;

  private[kamehtmlconsole] def getLines: IndexedSeq[ConsoleLine] = lines.map(_.getConsoleLine);

}

private[kamehtmlconsole] object ConsoleLineGroupImpl {

  trait Listener {
    def limitLineCount();
  }

}

