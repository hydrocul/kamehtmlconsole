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

  def input(event: KeyEvent, onComplete: Runnable){
    import SpecialKeyEvent._;
    event match {
      case UserTextKeyEvent(text) => inputText(event, onComplete, text);
      case SpecialKeyEvent(Backspace, false, false, false) => inputBackspace(event, onComplete);
      case _ => inputOther(event, onComplete);
    }
  }

  def setTextBuffer(text: String){
    atomic { implicit txn =>
      buf() = text;
      searchSuggestions(text);
    }
  }

  private def inputText(event: KeyEvent, onComplete: Runnable, text: String){
    nextListener.input(event, onComplete);
    atomic { implicit txn =>
      buf() = buf() + text;
      searchSuggestions(buf());
    }
  }

  private def inputBackspace(event: KeyEvent, onComplete: Runnable){
    nextListener.input(event, onComplete);
    atomic { implicit txn =>
      val b = buf();
      val len = b.length;
      if(len > 0){
        buf() = b.substring(0, len - 1);
        searchSuggestions(buf());
      }
    }
  }

  private def inputOther(event: KeyEvent, onComplete: Runnable){
    nextListener.input(event, onComplete);
    atomic { implicit txn =>
      buf() = "";
      searchSuggestions("");
    }
  }

  private def searchSuggestions(text: String){
    if(text.length==0){
      displaySuggestions(Nil);
    } else {
      mainImeEngine.searchSuggestions(text, new ImeEngineListener {
        def receiveSuggestions(suggestions: List[ImeEngineSuggestion]){
          if(buf.single()==text){
            displaySuggestions(suggestions);
          }
        }
      });
    }
  }

  private def displaySuggestions(suggestions: List[ImeEngineSuggestion]){
    atomic { implicit txn =>
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

  private def pickupSuggestion(suggestion: ImeEngineSuggestion){
    def exec(events: List[KeyEvent]){
      events match {
        case Nil => executor.execute { suggestion.executed(); }
        case head :: tail => executor.execute { input(head, { exec(tail); }); }
      }
    }
    exec(suggestion.keyEvents);
  }

}

