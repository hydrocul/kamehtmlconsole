package hydrocul.kamehtmlconsole;

import java.util.Timer;
import java.util.TimerTask;

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

  case class KeyEventStart(event: KeyEvent, onComplete: Runnable);
  case class KeyEventProcessing(event: KeyEvent, onComplete: Runnable,
    listeners: List[ConsoleListener], counter: Int);
  case class KeyEventComplete(counter: Int);
  case class KeyEventTimeOut(counter: Int);

  private val inputListenerActor = actor {
    import hydrocul.util.ScalaUtil.block2runnable;
    var currentCounter: Int = 0;
    var running: Boolean = false;
    var eventQueue = Vector[(KeyEvent, Runnable)]();
    def startNext(event: KeyEvent, onComplete: Runnable){
      currentCounter = currentCounter + 1;
      running = true;
      self ! KeyEventProcessing(event, onComplete, listeners, currentCounter);
      timer.schedule(new TimerTask(){
        override def run(){
          self ! KeyEventTimeOut(currentCounter);
        }
      }, 100); // 100ミリ秒後には次のイベントを処理できることにする
    }
    loop {
      react {
        case KeyEventStart(event, onComplete) => // handler の input から呼び出される
          if(!running){
            // 他のイベントを処理中でない場合
            startNext(event, onComplete);
          } else {
            // まだ他のイベントを処理中の場合、キューにためる
            eventQueue = eventQueue :+ (event, onComplete);
          }
        case KeyEventProcessing(event, onComplete, listeners, counter) =>
          // イベントを listeners に次々に渡す
          listeners match {
            case head :: tail =>
              actor {
                head.input(event, {
                  self ! KeyEventProcessing(event, onComplete, tail, counter);
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
          if(counter == currentCounter && running){
            running = false;
            if(eventQueue.size > 0){
              // まだ次のイベント処理を開始していない場合で、かつ、
              // 次のイベントがキューに待機している場合
              val next: (KeyEvent, Runnable) = eventQueue(0);
              eventQueue = eventQueue.drop(1);
              startNext(next._1, next._2);
            }
          }
        case KeyEventTimeOut(counter) =>
          if(counter == currentCounter && running){
            running = false;
            if(eventQueue.size > 0){
              // まだ次のイベント処理を開始していない場合で、かつ、
              // 次のイベントがキューに待機している場合
              val next: (KeyEvent, Runnable) = eventQueue(0);
              eventQueue = eventQueue.drop(1);
              startNext(next._1, next._2);
            }
          }
        case e =>
          throw new Error("Unknown message: " + e.toString);
      }
    }
  }

  private val handler = new ConsoleListener(){

    def input(event: KeyEvent, onComplete: Runnable){
      inputListenerActor ! KeyEventStart(event, onComplete);
    }

  }

  def addListener(listener: ConsoleListener){
    listeners = listeners ::: ( listener :: Nil);
  }

  def getHandler: ConsoleListener = handler;

  def createScreen(): Screen = new ScreenImpl(this);

}

private[kamehtmlconsole] object ConsoleImpl {

  val maxLineCount = 500;

}
