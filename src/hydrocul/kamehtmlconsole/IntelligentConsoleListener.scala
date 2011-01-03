package hydrocul.kamehtmlconsole;

import java.util.concurrent.Executor;

import scala.concurrent.stm.atomic;
import scala.concurrent.stm.Ref;

import hydrocul.util.ObjectPool;
import hydrocul.util.ScalaUtil.block2runnable;

class IntelligentConsoleListener(nextListener: ConsoleListener,
  mainImeEngine: ImeEngine, lineGroup: LineGroup,
  objectPool: ObjectPool, executor: Executor) extends ConsoleListener {

  private val line: LineBuffer = lineGroup.newLine();
  private val buf: Ref[String] = Ref("");
  private val counter: Ref[Int] = Ref[Int](0);

  def input(event: KeyEvent, onComplete: Runnable){
    import SpecialKeyEvent._;
    event match {
      case UserTextKeyEvent(text) => inputText(event, onComplete, text);
      case SpecialKeyEvent(Backspace, false, false, false) => inputBackspace(
        event, onComplete);
      case _ => inputOther(event, onComplete);
    }
  }

  def setTextBuffer(text: String){
    atomic { implicit txn =>
      buf() = text;
      counter() = counter() + 1;
      searchActor ! SearchStart(text, counter());
    }
  }

  private def inputText(event: KeyEvent, onComplete: Runnable, text: String){
    nextListener.input(event, onComplete);
    atomic { implicit txn =>
      buf() = buf() + text;
      counter() = counter() + 1;
      searchActor ! SearchStart(text, counter());
    }
  }

  private def inputBackspace(event: KeyEvent, onComplete: Runnable){
    nextListener.input(event, onComplete);
    atomic { implicit txn =>
      val b = buf();
      val len = b.length;
      if(len > 0){
        buf() = b.substring(0, len - 1);
        counter() = counter() + 1;
        searchActor ! SearchStart(text, counter());
      }
    }
  }

  private def inputOther(event: KeyEvent, onComplete: Runnable){
    nextListener.input(event, onComplete);
    atomic { implicit txn =>
      buf() = "";
      counter() = counter() + 1;
      searchActor ! SearchStart(text, counter());
    }
  }

  import scala.actors.Actor.actor;
  import scala.actors.Actor.loop;
  import scala.actors.Actor.react;

  case class SearchStart(text: String, counter: Int);

  private val searchActor = actor {
    loop {
      react {
        case SearchStart(text, counter) =>
          searchSuggestions(text, counter);
        case e =>
          throw new Error("Unknown message: " + e.toString);
      }
    }
  }

  private def searchSuggestions(text: String, counter: Int){
    if(text.length==0){
      displaySuggestions(Nil, counter);
    } else {
      mainImeEngine.searchSuggestions(text, new ImeEngineListener {
        def receiveSuggestions(suggestions: List[ImeEngineSuggestion]){
          if(buf.single()==text){
            displaySuggestions(suggestions, counter);
          }
        }
      });
    }
  }

  private def displaySuggestions(suggestions: List[ImeEngineSuggestion], counter: Int){
    atomic { implicit txn =>
      if(counter()==counter){
        line.clearLinkedObject();
        val html = suggestions.map { s =>
          val event = new ClickKeyEvent {
            def run(){
              pickupSuggestion(s);
            }
          }
          line.addLinkedObject(event);
          val key = objectPool.getKey(event);
          "<a href=\"#" + key + "\" class=\"ime-suggestion click-event\">" + s.caption + "</a>";
        }.mkString;
        line.updateHtml(html);
      }
    }
  }

  private def pickupSuggestion(suggestion: ImeEngineSuggestion){
    def exec(events: List[KeyEvent]){
      events match {
        case Nil => atomic { suggestion.executed(); }
        case head :: tail => atomic { input(head, { exec(tail); }); }
        // TODO input の同期はどうする？
      }
    }
    exec(suggestion.keyEvents);
  }

}

