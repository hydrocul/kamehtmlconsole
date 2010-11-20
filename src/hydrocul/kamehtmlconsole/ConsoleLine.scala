package hydrocul.kamehtmlconsole;

import java.io.PrintWriter;

import hydrocul.util.StringLib;

private[kamehtmlconsole] case class ConsoleLine(lineId: String, counter: Int, html: String,
  javascript: List[String]){

}

private[kamehtmlconsole] object ConsoleLine {

  def printInsertFirst(writer: PrintWriter, newLine: ConsoleLine){
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

  def printInsertAfter(writer: PrintWriter, prevLine: ConsoleLine, newLine: ConsoleLine){
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

  def printUpdateJavaScriptCode(writer: PrintWriter,
    oldLine: ConsoleLine, newLine: ConsoleLine){

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

  private def printJavaScript(writer: PrintWriter, lineId: String, javascript: String){
    val s = javascript.replaceAll("\\$\\$\\$", lineId);
    writer.print(s);
    if(!s.endsWith("\n")){
      writer.print("\n");
    }
  }

}

