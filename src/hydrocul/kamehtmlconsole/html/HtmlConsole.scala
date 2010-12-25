package hydrocul.kamehtmlconsole.html;

import java.io.PrintWriter;
import java.io.Writer;

import scala.concurrent.stm.atomic;
import scala.concurrent.stm.Ref;

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

  // for initializing writer and htmlWriter
  private implicit def writerToPrintWriter(writer: Writer): PrintWriter =
    new PrintWriter(writer, true);

  private val writer: PrintWriter = new Writer(){

    override def write(ch: Int): Unit =
      synexec(console){ HtmlConsole.this.write(ch); }

    override def write(cbuf: Array[Char], off: Int, len: Int): Unit =
      synexec(console){ (off until off + len).foreach(index => HtmlConsole.this.write(cbuf(index))); }

    override def flush(): Unit = synexec(console){ HtmlConsole.this.update(); }

    override def close(): Unit = synexec(console){ HtmlConsole.this.update(); }

  }

  private val htmlWriter: PrintWriter = new Writer(){

    override def write(ch: Int): Unit =
      synexec(console){ HtmlConsole.this.writeHtml(ch); }

    override def write(cbuf: Array[Char], off: Int, len: Int): Unit =
      synexec(console){ (off until off + len).foreach(index => HtmlConsole.this.writeHtml(cbuf(index))); }

    override def flush(): Unit = synexec(console){ HtmlConsole.this.update(); }

    override def close(): Unit = synexec(console){ HtmlConsole.this.update(); }

  }

  def getPrintWriter: PrintWriter = writer;

  def getHtmlPrintWriter: PrintWriter = htmlWriter;

}
