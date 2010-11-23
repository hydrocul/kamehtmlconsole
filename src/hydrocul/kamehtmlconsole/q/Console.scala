package hydrocul.kamehtmlconsole.q;

import hydrocul.kameq.scala.Pipe;
import hydrocul.kameq.scala.Pipe._;

class Console(console: hydrocul.kamehtmlconsole.Console){

  def getLoadingHtml: String = console.getLoadingHtml;

  def taskNewLineGroup(): Pipe[hydrocul.kamehtmlconsole.ConsoleLineGroup] = stask[hydrocul.kamehtmlconsole.ConsoleLineGroup](console){
    console.newLineGroup();
  }

//  def newLineGroupBefore(after: ConsoleLineGroup): ConsoleLineGroup;

//  def newLineGroupAfter(before: ConsoleLineGroup): ConsoleLineGroup;

}
