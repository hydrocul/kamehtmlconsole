package hydrocul.kamehtmlconsole;

import hydrocul.kameq.scala.Pipe.exec;
import hydrocul.kameq.scala.Pipe.synexec;
import hydrocul.util.ObjectPool;

class IntelligentConsoleListener(nextListener: ConsoleListener,
  mainImeEngine: ImeEngine, lineGroup: LineGroup,
  objectPool: ObjectPool) extends ConsoleListener {

  private val line: LineBuffer = lineGroup.newLine();
  private val buf: StringBuilder = new StringBuilder();

  def input(event: KeyEvent){
    import SpecialKeyEvent._;
    event match {
      case UserTextKeyEvent(text) => inputText(event,text);
      case SpecialKeyEvent(Backspace, false, false, false) => inputBackspace(event);
      case _ => inputOther(event);
    }
  }

  def setTextBuffer(text: String){
    synexec(this){
      val len = buf.length;
      buf.delete(0, len);
      buf.append(text);
      searchSuggestions();
    }
  }

  private def inputText(event: KeyEvent, text: String){
    nextListener.input(event);
    synexec(this){
      buf.append(text);
      searchSuggestions();
    }
  }

  private def inputBackspace(event: KeyEvent){
    nextListener.input(event);
    synexec(this){
      val len = buf.length;
      if(len > 0){
        buf.delete(len - 1, len);
        searchSuggestions();
      }
    }
  }

  private def inputOther(event: KeyEvent){
    nextListener.input(event);
    synexec(this){
      val len = buf.length;
      buf.delete(0, len);
      searchSuggestions();
    }
  }

  private def searchSuggestions(){
    val t = buf.toString;
    if(t.length==0){
      displaySuggestions(Nil);
    } else {
      mainImeEngine.searchSuggestions(t, new ImeEngineListener {
        def receiveSuggestions(suggestions: List[ImeEngineSuggestion]){
          if(buf.toString==t){
            displaySuggestions(suggestions);
          }
        }
      });
    }
  }

  private def displaySuggestions(suggestions: List[ImeEngineSuggestion]){
    synexec(line){
      line.clearLinkedObject();
      val html = suggestions.map { s =>
        val event = new ClickKeyEvent {
          def run(){
            // ここは他のキーボードイベントと同時に実行されることはない
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
    suggestion.keyEvents.foreach { e =>
      input(e);
    }
    exec {
      suggestion.executed();
    }
  }

}

