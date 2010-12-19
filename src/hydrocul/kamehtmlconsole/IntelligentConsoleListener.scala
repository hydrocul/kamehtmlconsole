package hydrocul.kamehtmlconsole;

import hydrocul.kameq.scala.Pipe.synexec;
import hydrocul.util.ObjectPool;

class IntelligentConsoleListener(nextListener: ConsoleListener,
  mainImeEngine: ImeEngine, lineGroup: LineGroup,
  objectPool: ObjectPool) extends ConsoleListener {

  private val line: LineBuffer = lineGroup.newLine();

  def input(event: KeyEvent){
    import SpecialKeyEvent._;
    event match {
      case UserTextKeyEvent(text) => inputText(text);
      case _ => nextListener.input(event);
    }
  }

  private def inputText(text: String){
    mainImeEngine.searchSuggestions(text, new ImeEngineListener {
      def receiveSuggestions(suggestions: List[ImeEngineSuggestion]) =
        displaySuggestions(suggestions);
    });
  }

  private def displaySuggestions(suggestions: List[ImeEngineSuggestion]){
    synexec(line){
      line.clearLinkedObject();
      val html = suggestions.map { s =>
        val event = new ScriptEvent {
          def run(){
            pickupSuggestion(s);
          }
        }
        line.addLinkedObject(event);
        val key = objectPool.getKey(event);
        "<a href=\"#" + key + "\" class=\"ime-suggestion script-event\">" + s.caption + "</a>";
      }.mkString;
      line.updateHtml(html);
    }
  }

  private def pickupSuggestion(suggestion: ImeEngineSuggestion){
    // TODO
  }

}

object IntelligentConsoleListener {



}
