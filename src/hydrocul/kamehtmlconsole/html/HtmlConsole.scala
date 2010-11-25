package hydrocul.kamehtmlconsole.html;

import java.io.PrintWriter;
import java.io.Writer;

import hydrocul.util.StringLib;

class HtmlConsole(console: Console, group: ConsoleLineGroup){

  private var line = null;
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
    // TODO
  }

  private def write(ch: Int){
    stask(console){
      // TODO
    } submit;
  }

  // for initializing writer and htmlWriter
  private implicit def writerToPrintWriter(writer: Writer): PrintWriter =
    new PrintWriter(writer, true);

  private val writer: PrintWriter = new Writer(){

    override def write(ch: Int): Unit = stask(console){ HtmlConsole.this.writer(ch); } submit;

    override def write(cbuf: Array[Char], off: Int, len: Int): Unit =
      stask(console){ (off until off + len).foreach(index => HtmlConsole.this.write(cbuf(index))); } submit;

    override def flush(): Unit = stask(console){ HtmlConsole.this.update(); } submit;

    override def close(): Unit = stask(console){ HtmlConsole.this.update(); } submit;

  }

  def getPrintWriter: PrintWriter = writer;

}
