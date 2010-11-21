package hydrocul.kamehtmlconsole;

import java.util.concurrent.atomic.AtomicInteger;

import hydrocul.util.ObjectPool;

trait ConsoleLineBuffer {

  private[kamehtmlconsole] def getConsoleLine: ConsoleLine;

  def updateHtml(html: String);

  def appendJavascript(javascript: String);

  def addLinkedObject(obj: AnyRef);

}

private[kamehtmlconsole] class ConsoleLineBufferImpl(objectPool: ObjectPool) extends ConsoleLineBuffer {

  private val lineId: String = objectPool.getKey(this);
  @volatile private var counter: Int = 0;
  @volatile private var html: String = "";
  @volatile private var javascript: IndexedSeq[String] = Vector();
  @volatile private var linkedObjects: List[AnyRef] = Nil;

  private[kamehtmlconsole] def getConsoleLine: ConsoleLine =
    new ConsoleLine(lineId, counter, getHtml, javascript);

  private def getHtml: String = if(html.isEmpty) "&nbsp;" else html;

  def updateHtml(html: String){
    this.html = html;
  }

  def appendJavascript(javascript: String){
    this.javascript = this.javascript :+ javascript;
  }

  def addLinkedObject(obj: AnyRef){
    linkedObjects = linkedObjects :+ obj;
  }

}

object ConsoleLineBuffer {

  private val globalCounter = new AtomicInteger(0);

}
