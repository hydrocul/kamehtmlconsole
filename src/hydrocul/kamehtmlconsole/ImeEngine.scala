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

}

class StdImeEngineSuggestion(val caption: String, len: Int) extends ImeEngineSuggestion {

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

}
