package hydrocul.kamehtmlconsole;

import hydrocul.util.ObjectPool;

trait ConsoleLineGroup {

  def newLine(): ConsoleLineBuffer;

}

private[kamehtmlconsole] class ConsoleLineGroupImpl(objectPool: ObjectPool) extends ConsoleLineGroup {

  @volatile private var lines: IndexedSeq[ConsoleLineBuffer] = Vector[ConsoleLineBuffer]();

  def newLine(): ConsoleLineBuffer = {
    val ret = new ConsoleLineBufferImpl(objectPool);
    lines = lines :+ ret;
    ret;
  }

  private[kamehtmlconsole] def getLines: IndexedSeq[ConsoleLine] = lines.map(_.getConsoleLine);

}
