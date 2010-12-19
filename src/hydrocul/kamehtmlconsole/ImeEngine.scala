package hydrocul.kamehtmlconsole;

import scala.annotation.tailrec;

trait ImeEngine {

  def searchSuggestions(text: String, listener: ImeEngineListener);

}

trait ImeEngineListener {

  def receiveSuggestions(suggestions: List[ImeEngineSuggestion]);

}

trait ImeEngineSuggestion {

  def caption: String;

  def keyEvents: List[KeyEvent];

  def executed(): Unit;

}

class StdImeEngineSuggestion(val caption: String, len: Int,
  executedProcess: =>Unit) extends ImeEngineSuggestion {

  def this(caption: String, len: Int) = this(caption, len, ());

  def keyEvents: List[KeyEvent] = {

    @tailrec
    def sub(len: Int, tail: List[KeyEvent]): List[KeyEvent] = {
      if(len==0){
        tail;
      } else {
        sub(len - 1, SpecialKeyEvent(SpecialKeyEvent.Backspace) :: tail);
      }
    }

    sub(len, UserTextKeyEvent(caption) :: Nil);

  }

  def executed(): Unit = executedProcess;

}



