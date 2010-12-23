package hydrocul.kamehtmlconsole;

import scala.concurrent.stm.atomic;
import scala.concurrent.stm.Ref;

import hydrocul.util.ObjectPool;

/**
 * 一連の Line を表すクラス。
 * 
 * コンソールに表示される文字などを操作するには、
 * newLine, newLineBefore, newLineAfter, LineBuffer#updateHtml などを呼び出す。
 * PrintWriter を使って操作したい場合は、 このLineGroup を HtmlConsole でラップする。
 */
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

  private val lines: Ref[Vector[LineBuffer]] = Ref(Vector());

  def getLines: Vector[LineBuffer] = lines.single();

  def newLine(): LineBuffer = {
    val ret = new LineBufferImpl(objectPool);
    atomic { implicit txn =>
      lines() = lines() :+ ret;
    }
    limitLineCount();
    ret;
  }

  def newLineBefore(after: LineBuffer): LineBuffer = {
    val ret = new LineBufferImpl(objectPool);
    atomic { implicit txn =>
      val i = lines().indexOf(after);
      if(i >= 0){
        lines() = (lines().take(i) :+ ret) ++ lines().drop(i);
      }
    }
    limitLineCount();
    ret;
  }

  def newLineAfter(before: LineBuffer): LineBuffer = {
    val ret = new LineBufferImpl(objectPool);
    atomic { implicit txt =>
      val i = lines().indexOf(before) + 1;
      if(i > 0){
        lines() = (lines().take(i) :+ ret) ++ lines().drop(i);
      }
    }
    limitLineCount();
    ret;
  }

  private def limitLineCount(){
    atomic { implicit txn =>
      val s = lines().size;
      if(s > ConsoleImpl.maxLineCount){
        lines() = lines().drop(s - ConsoleImpl.maxLineCount);
      }
    }
  }

  def size = lines.single().size;

  def getLinesInfo: Vector[LineInfo] = lines.single().map(_.getLineInfo);

}

private[kamehtmlconsole] object LineGroupImpl {

  trait Listener {
    def limitLineCount();
  }

}

