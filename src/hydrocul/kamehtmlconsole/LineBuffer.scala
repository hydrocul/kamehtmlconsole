package hydrocul.kamehtmlconsole;

import java.util.concurrent.atomic.AtomicInteger;

import scala.concurrent.stm.atomic;
import scala.concurrent.stm.Ref;

import hydrocul.util.ObjectPool;

trait LineBuffer {

  private[kamehtmlconsole] def getLineInfo: LineInfo;

  def updateHtml(html: String);

  def appendJavascript(javascript: String);

  def addLinkedObject(obj: AnyRef);

  def clearLinkedObject();

}

private[kamehtmlconsole] class LineBufferImpl(objectPool: ObjectPool) extends LineBuffer {

  import LineBufferImpl._;

  private val lineId: String = objectPool.getKey(this);
  private val counter: Ref[Int] = Ref(globalCounter.incrementAndGet());
  private val html: Ref[String] = Ref("");
  private val text: Ref[String] = Ref("");
  private val javascript: Ref[Vector[String]] = Ref(Vector());
  private val linkedObjects: Ref[List[AnyRef]] = Ref(Nil);

  private[kamehtmlconsole] def getLineInfo: LineInfo = {
    atomic { implicit txn =>
      val h = {
        val h = html();
        if(h.isEmpty) "&nbsp;" else h;
      }
      new LineInfo(lineId, counter(), h, javascript());
    }
  }

  def updateHtml(html: String){
    atomic { implicit txn =>
      this.html() = html;
      this.text() = getTextFromHtml(html);
      counter() = globalCounter.incrementAndGet();
    }
  }

  def appendJavascript(javascript: String){
    atomic { implicit txn =>
      this.javascript() = this.javascript() :+ javascript;
      counter() = globalCounter.incrementAndGet();
    }
  }

  def addLinkedObject(obj: AnyRef){
    atomic { implicit txn =>
      linkedObjects() = linkedObjects() :+ obj;
      counter() = globalCounter.incrementAndGet();
    }
  }

  def clearLinkedObject(){
    atomic { implicit txn =>
      linkedObjects() = Nil;
      counter() = globalCounter.incrementAndGet();
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
