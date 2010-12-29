package hydrocul.kamehtmlconsole.html;

import java.io.PrintWriter;
import java.io.Writer;

import scala.actors.Actor.actor;
import scala.actors.Actor.loop;
import scala.actors.Actor.react;

import hydrocul.util.StringLib;

import hydrocul.kamehtmlconsole._;

class HtmlConsole(console: Console, group: LineGroup){

  private var line: LineBuffer = null;
  private var buf1 = new StringBuilder; // HTML
  private var buf2 = new StringBuilder; // temporary text
  private var isLastCr = false;

  private def update(){
    if(line==null){
      line = group.newLine();
    }
    line.updateHtml(buf1.toString + StringLib.encodeHtmlLong(buf2.toString));
  }

  private def println(){
    update();
    line = null;
    buf1 = new StringBuilder;
    buf2 = new StringBuilder;
    isLastCr = false;
  }

  private def write(ch: Int){
    if(ch=='\n'){
      if(!isLastCr){
        println();
      } else {
        isLastCr = false;
      }
    } else if(ch=='\r'){
      isLastCr = true;
      println();
    } else {
      isLastCr = false;
      buf2.append(ch.asInstanceOf[Char]);
    }
  }

  private def writeHtml(ch: Int){
    if(buf2.length > 0){
      buf1.append(StringLib.encodeHtmlLong(buf2.toString));
      buf2 = new StringBuilder;
    }
    buf1.append(ch.asInstanceOf[Char]);
  }

  private case class WriteChar(ch: Int);
  private case class WriteChars(cbuf: Array[Char], off: Int, len: Int);
  private case class WriteHtmlChar(ch: Int);
  private case class WriteHtmlChars(cbuf: Array[Char], off: Int, len: Int);
  private case class Update();

  private val writerActor = actor {
    loop {
      react {
        case WriteChar(ch) =>
          write(ch);
        case WriteChars(cbuf, off, len) =>
          (off until off + len).foreach(ii => write(cbuf(ii)));
        case WriteHtmlChar(ch) =>
          writeHtml(ch);
        case WriteHtmlChars(cbuf, off, len) =>
          (off until off + len).foreach(ii => writeHtml(cbuf(ii)));
        case Update() =>
          update();
      }
    }
  }

  // for initializing writer and htmlWriter
  private implicit def writerToPrintWriter(writer: Writer): PrintWriter =
    new PrintWriter(writer, true);

  private val writer: PrintWriter = new Writer(){

    override def write(ch: Int): Unit =
      writerActor ! WriteChar(ch);

    override def write(cbuf: Array[Char], off: Int, len: Int): Unit =
      writerActor ! WriteChars(cbuf, off, len);

    override def flush(): Unit =
      writerActor ! Update();

    override def close(): Unit =
      writerActor ! Update();

  }

  private val htmlWriter: PrintWriter = new Writer(){

    override def write(ch: Int): Unit =
      writerActor ! WriteHtmlChar(ch);

    override def write(cbuf: Array[Char], off: Int, len: Int): Unit =
      writerActor ! WriteHtmlChars(cbuf, off, len);

    override def flush(): Unit =
      writerActor ! Update();

    override def close(): Unit =
      writerActor ! Update();

  }

  def getPrintWriter: PrintWriter = writer;

  def getHtmlPrintWriter: PrintWriter = htmlWriter;

}
