package hydrocul.kamehtmlconsole;

import java.util.concurrent.Exchanger;

import hydrocul.kameq.scala.Pipe.synexec;
import hydrocul.util.ObjectPool;

class Console(console: core.Console){

  def getLoadingHtml: String = console.getLoadingHtml;

  def newLineGroup(): core.LineGroup = {
    val ex = new Exchanger[core.LineGroup];
    synexec(console){
      ex.exchange(console.newLineGroup);
    }
    return ex.exchange(null);
  }

  def newLineGroup(p: core.LineGroup=>Unit){
    synexec(console){
      p(console.newLineGroup);
    }
  }

/*
  def newLineGroupBefore(after: LineGroup): LineGroup;

  def newLineGroupAfter(before: LineGroup): LineGroup;

  def size: Int;

  def getLinesInfo: LinesInfo;

  def createScreen(): Screen;
*/
}

