package hydrocul.kamehtmlconsole.core;

import java.util.concurrent.atomic.AtomicInteger;

import hydrocul.util.ObjectPool;

trait LineBuffer {

  private[kamehtmlconsole] def getLineInfo: LineInfo;

  def updateHtml(html: String);

  def appendJavascript(javascript: String);

  def addLinkedObject(obj: AnyRef);

}

private[kamehtmlconsole] class LineBufferImpl(objectPool: ObjectPool) extends LineBuffer {

  import LineBufferImpl._;

  private val lineId: String = objectPool.getKey(this);
  @volatile private var counter: Int = globalCounter.incrementAndGet();
  @volatile private var html: String = "";
  @volatile private var text: String = "";
  @volatile private var javascript: Vector[String] = Vector();
  @volatile private var linkedObjects: List[AnyRef] = Nil;

  private[kamehtmlconsole] def getLineInfo: LineInfo =
    synchronized { new LineInfo(lineId, counter, getHtml, javascript); }

  private def getHtml: String = {
    val h = html;
    if(h.isEmpty) "&nbsp;" else h;
  }

  def updateHtml(html: String){
    synchronized {
      this.html = html;
      this.text = getTextFromHtml(html);
      counter = globalCounter.incrementAndGet();
    }
  }

  def appendJavascript(javascript: String){
    synchronized {
      this.javascript = this.javascript :+ javascript;
      counter = globalCounter.incrementAndGet();
    }
  }

  def addLinkedObject(obj: AnyRef){
    synchronized {
      linkedObjects = linkedObjects :+ obj;
      counter = globalCounter.incrementAndGet();
    }
  }

}

private[kamehtmlconsole] object LineBufferImpl {

  private val globalCounter = new AtomicInteger(0);

  def getTextFromHtml(html: String): String = {
    var text = html;
    val r = "<[^>]+>".r;
    while({
      r.findFirstMatchIn(text) match {
        case Some(m) =>
          text = text.substring(0, m.start) + text.substring(m.end);
          true;
        case None =>
          false;
      }
    }){}
    text;
  }

}
