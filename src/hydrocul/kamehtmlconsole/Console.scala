package hydrocul.kamehtmlconsole;

import java.util.concurrent.LinkedBlockingQueue;

import hydrocul.kameq.scala.Pipe.synexec;
import hydrocul.util.ObjectPool;

class Console(console: core.Console){

/*
  def getLoadingHtml: String = console.getLoadingHtml;

  def newLineGroup(): core.LineGroup = {
    val ex = new LinkedBlockingQueue[core.LineGroup];
    synexec(console){
      ex.put(console.newLineGroup);
    }
    return ex.take();
  }

  def newLineGroup(p: core.LineGroup => Unit){
    synexec(console){
      p(console.newLineGroup);
    }
  }

  def newLineGroupBefore(after: LineGroup): core.LineGroup = {
    val ex = new LinkedBlockingQueue[core.LineGroup];
    sysexec(console){
      ex.put(console.newLineGroupBefore(after));
    }
    return ex.take();
  }

  def newLineGroupBefore(after: LineGroup, p: LineGroup => Unit){
    synexec(console){
      p(console.newLineGroupBefore(after));
    }
  }

  def newLineGroupAfter(before: LineGroup): core.LineGroup = {
    val ex = new LinkedBlockingQueue[core.LineGroup];
    sysexec(console){
      ex.put(console.newLineGroupAfter(before));
    }
    return ex.take();
  }

  def newLineGroupAfter(before: LineGroup, p: LineGroup => Unit){
    synexec(console){
      p(console.newLineGroupAfter(before));
    }
  }

  def size: Int = console.size;

  def getLinesInfo: LinesInfo;

  def createScreen(): Screen;
*/

}

