package hydrocul.kamehtmlconsole;

import java.util.Timer;

import scala.concurrent.stm.atomic;
import scala.concurrent.stm.Ref;

import hydrocul.util.ObjectPool;

trait Console {

  def getLoadingHtml: String;

  def newLineGroup(): LineGroup;

  def newLineGroupBefore(after: LineGroup): LineGroup;

  def newLineGroupAfter(before: LineGroup): LineGroup;

  def size: Int;

  def getLinesInfo: Vector[LineInfo];

  def getHandler: ConsoleListener;

  def createScreen(): Screen;

}

private[kamehtmlconsole] class ConsoleImpl(objectPool: ObjectPool, baseUrl: String, timer: Timer) extends Console {

  import ConsoleImpl._;

  private val groups: Ref[Vector[LineGroupImpl]] = Ref(Vector());

  def getLoadingHtml = "<img src=\"" + baseUrl + "etc/loading.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"loading\" />";

  def newLineGroup(): LineGroup = {
    val ret = new LineGroupImpl(objectPool, groupListener);
    atomic { implicit txn =>
      groups() = groups() :+ ret;
    }
    return ret;
  }

  def newLineGroupBefore(after: LineGroup): LineGroup = {
    val ret = new LineGroupImpl(objectPool, groupListener);
    atomic { implicit txn =>
      val i = groups().indexOf(after);
      if(i >= 0){
        groups() = (groups().take(i) :+ ret) ++ groups().drop(i);
      }
    }
    ret;
  }

  def newLineGroupAfter(before: LineGroup): LineGroup = {
    val ret = new LineGroupImpl(objectPool, groupListener);
    atomic { implicit txn =>
      val i = groups().indexOf(before) + 1;
      if(i > 0){
        groups() = (groups().take(i) :+ ret) ++ groups().drop(i);
      }
    }
    ret;
  }

  private def limitLineCount(){
    // TODO #limitLineCount
  }

  private val groupListener = new LineGroupImpl.Listener {
    def limitLineCount(){
      ConsoleImpl.this.limitLineCount();
    }
  }

  def size: Int = groups.single().map(_.size).sum;

  def getLinesInfo: Vector[LineInfo] = groups.single().flatMap(_.getLinesInfo);



  private var listeners: List[ConsoleListener] = Nil;

  import scala.actors.Actor.actor;
  import scala.actors.Actor.loop;
  import scala.actors.Actor.react;
  import scala.actors.Actor.self;

  case class KeyEventInfo(event: KeyEvent, onComplete: Runnable, counter: Int);

  case class KeyEventStart(event: KeyEvent, onComplete: Runnable);
  case class KeyEventProcessing(info: KeyEventInfo,
    listeners: List[ConsoleListener]);
  case class KeyEventComplete(info: KeyEventInfo);
  case class KeyEventTimeOut(info: KeyEventInfo);

  private val inputListenerActor = actor {
/*
    var currentCounter: Int = 0;
    var eventQueue = Vector[KeyEvent]();
    def addEvent(event: KeyEvent, onComplete: Runnable){
      if(eventQueue
    }
    loop {
      react {
        case KeyEventStart(event, onComplete) => // handler の input から呼び出される
          if(eventQueue.size == 0){
            // 他のイベントを処理中でない場合
            currentCounter = currentCounter + 1;
            self ! KeyEventProcessing(KeyEventInfo(event, onComplete, currentCounter),
              listeners);
            timer.schedule(new TimerTask(){
              override def run(){
                self ! KeyEventTimeOut(currentCounter);
              }
            }, 100); // 100ミリ秒後には
          } else {
            // まだ他のイベントを処理中の場合、キューにためる
            eventQueue = eventQueue :+ ev;
          }
        case KeyEventProcessing(event, onComplete, listeners, counter) =>
          // イベントを listeners に次々に渡す
          listeners match {
            case head :: tail =>
              actor {
                head.input(event, {
                  self ! KeyEventInfo(event, tail, onComplete, counter);
                });
              }
            case Nil => // listeners での処理がすべて終了したとき、
              // onComplete を実行する
              actor {
                onComplete.run();
                self ! KeyEventComplete(counter);
              }
          }
        case KeyEventComplete(counter) => // onComplete の実行が終了したとき、
          if(counter == currentCounter && eventQueue.size > 0){
            // まだ次のイベント処理を開始していない場合で、かつ、
            // 次のイベントがキューに待機している場合
            currentCounter = currentCounter + 1;
            val next = eventQueue(0);
            eventQueue = eventQueue.drop(1);
            self ! KeyEventProcessing(next.event, next.onComplete,
              listeners, currentCounter);
          }
        case e =>
          throw new Error("Unknown message: " + e.toString);
      }
    }
*/
  }

  private val handler = new ConsoleListener(){

    def input(event: KeyEvent, onComplete: Runnable){
      inputListenerActor ! KeyEventStart(event, onComplete);
    }

  }

  def addListener(listener: ConsoleListener){
    listeners = listeners ::: ( listener :: Nil);
  }

  def getHandler: ConsoleListener;

  def createScreen(): Screen = new ScreenImpl(this);

}

private[kamehtmlconsole] object ConsoleImpl {

  val maxLineCount = 500;

}
