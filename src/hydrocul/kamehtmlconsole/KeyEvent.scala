package hydrocul.kamehtmlconsole;

trait KeyEvent;

case class UserTextKeyEvent(text: String) extends KeyEvent {
}

case class SpecialKeyEvent(key: SpecialKeyEvent.SpecialKey,
  alt: Boolean, ctrl: Boolean, shift: Boolean) extends KeyEvent {
}

object SpecialKeyEvent {

  private val keys: Map[String, SpecialKey] = Map();

  sealed trait SpecialKey;
  object Tab extends SpecialKey; keys = keys + ("Tab" -> Tab);
  object Enter extends SpecialKey; keys = keys + ("Enter" -> Enter);

  def getSpecialKey(key: String): SpecialKey;

}


