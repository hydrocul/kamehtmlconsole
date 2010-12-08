package hydrocul.kamehtmlconsole;

trait KeyEvent;

case class UserTextKeyEvent(text: String) extends KeyEvent {
}

case class SpecialKeyEvent(key: String) extends KeyEvent {
}

object SpecialKeyEvent {


}


