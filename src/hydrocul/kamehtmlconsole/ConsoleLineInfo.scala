package hydrocul.kamehtmlconsole;

import java.io.PrintWriter;

import scala.collection.immutable.IndexedSeq;

import hydrocul.util.StringLib;

case class ConsoleLineInfo(lineId: String, counter: Int, html: String,
  javascript: IndexedSeq[String]);

case class ConsoleLinesInfo(lines: IndexedSeq[ConsoleLineInfo], counter: Int);

private[kamehtmlconsole]
object ConsoleLineInfo {

  def printInsertFirst(writer: PrintWriter, newLine: ConsoleLineInfo){
    writer.print("$(\"#console\").prepend(\"<div id=\\\"");
    writer.print(newLine.lineId);
    writer.print("\\\" class=\\\"line\\\">");
    writer.print(StringLib.encodeJavaLiteral(newLine.html));
    writer.print("</div>\");");
    writer.print("initLine(\"");
    writer.print(newLine.lineId);
    writer.print("\");\n");
    newLine.javascript.foreach(printJavaScript(
      writer, newLine.lineId, _));
  }

  def printInsertAfter(writer: PrintWriter, prevLine: ConsoleLineInfo, newLine: ConsoleLineInfo){
    writer.print("$(\"#");
    writer.print(prevLine.lineId);
    writer.print("\").after(\"<div id=\\\"");
    writer.print(newLine.lineId);
    writer.print("\\\" class=\\\"line\\\">");
    writer.print(StringLib.encodeJavaLiteral(newLine.html));
    writer.print("</div>\");");
    writer.print("initLine(\"");
    writer.print(newLine.lineId);
    writer.print("\");\n");
    newLine.javascript.foreach(printJavaScript(
      writer, newLine.lineId, _));
  }

  def printUpdate(writer: PrintWriter,
    oldLine: ConsoleLineInfo, newLine: ConsoleLineInfo){

    assert(oldLine.lineId==newLine.lineId);
    writer.print("$(\"#");
    writer.print(newLine.lineId);
    writer.print("\").html(\"");
    writer.print(StringLib.encodeJavaLiteral(newLine.html));
    writer.print("\");\n");
    writer.print("initLine(\"");
    writer.print(newLine.lineId);
    writer.print("\");\n");
    newLine.javascript.drop(oldLine.javascript.size).foreach(printJavaScript(
      writer, newLine.lineId, _));
  }

  def printDelete(writer: PrintWriter,
    oldLine: ConsoleLineInfo){

    writer.print("$(\"#");
    writer.print(oldLine.lineId);
    writer.print("\").remove();\n");
  }

  private def printJavaScript(writer: PrintWriter, lineId: String, javascript: String){
    val s = javascript.replaceAll("\\$\\$\\$", lineId);
    writer.print(s);
    if(!s.endsWith("\n")){
      writer.print("\n");
    }
  }

}

