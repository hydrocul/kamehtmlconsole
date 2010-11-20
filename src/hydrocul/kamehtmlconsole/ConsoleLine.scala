package hydrocul.kamehtmlconsole;

import java.io.PrintWriter;

import hydrocul.util.StringLib;

case class ConsoleLine(lineId: String, counter: Int, html: String,
  javascript: List[String]){

}

object ConsoleLine {

  def printUpdateJavaScriptCode(writer: PrintWriter,
    oldLine: ConsoleLine, newLine: ConsoleLine){

    assert(oldLine.lineId==newLine.lineId);
    writer.print("$(\"#");
    writer.print(newLine.lineId);
    writer.print("\").html(\"");
    writer.print(StringLib.encodeJavaLiteral(line.html));
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

