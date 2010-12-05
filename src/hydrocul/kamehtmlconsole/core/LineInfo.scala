package hydrocul.kamehtmlconsole.core;

import java.io.PrintWriter;

import hydrocul.util.StringLib;

case class LineInfo(lineId: String, counter: Int, html: String,
  javascript: Vector[String]);

case class LinesInfo(lines: Vector[LineInfo], counter: Int);

private[kamehtmlconsole] object LineInfo {

  def printInsertFirst(writer: PrintWriter, newLine: LineInfo){
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

  def printInsertAfter(writer: PrintWriter, prevLine: LineInfo, newLine: LineInfo){
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

  def printUpdate(writer: PrintWriter, oldLine: LineInfo, newLine: LineInfo){

    assert(oldLine.lineId == newLine.lineId);
    if(oldLine.counter != newLine.counter){
      if(oldLine.html != newLine.html){
        writer.print("$(\"#");
        writer.print(newLine.lineId);
        writer.print("\").html(\"");
        writer.print(StringLib.encodeJavaLiteral(newLine.html));
        writer.print("\");");
        writer.print("initLine(\"");
        writer.print(newLine.lineId);
        writer.print("\");\n");
      }
      newLine.javascript.drop(oldLine.javascript.size).foreach(printJavaScript(
        writer, newLine.lineId, _));
    }
  }

  def printDelete(writer: PrintWriter, oldLine: LineInfo){

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

