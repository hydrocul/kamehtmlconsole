package hydrocul.kamehtmlconsole;

trait KeyEvent;

case class UserTextKeyEvent(text: String) extends KeyEvent {
}

/**
 * HTML上の $("a.click-event") をクリックした時にこのイベントが発生する。
 *
 * このイベントは ConsoleListener に渡されることなく、直ちに run が呼び出される。
 */
trait ClickKeyEvent extends KeyEvent {

  def run();

}

case class SpecialKeyEvent(key: SpecialKeyEvent.SpecialKey,
  alt: Boolean, ctrl: Boolean, shift: Boolean) extends KeyEvent {

  private[kamehtmlconsole] def getUpdateAlt(alt: Boolean) = SpecialKeyEvent(key, alt, ctrl, shift);

  private[kamehtmlconsole] def getUpdateCtrl(ctrl: Boolean) = SpecialKeyEvent(key, alt, ctrl, shift);

  private[kamehtmlconsole] def getUpdateShift(shift: Boolean) = SpecialKeyEvent(key, alt, ctrl, shift);

}

object SpecialKeyEvent {

  def apply(key: SpecialKey): SpecialKeyEvent = SpecialKeyEvent(key, false, false, false);

  sealed trait SpecialKey;
  object Esc extends SpecialKey;
  object Backspace extends SpecialKey;
  object Tab extends SpecialKey;
  object Enter extends SpecialKey;
  object Space extends SpecialKey;
  object PageUp extends SpecialKey;
  object PageDown extends SpecialKey;
  object End extends SpecialKey;
  object Home extends SpecialKey;
  object Left extends SpecialKey;
  object Up extends SpecialKey;
  object Right extends SpecialKey;
  object Down extends SpecialKey;
  object Insert extends SpecialKey;
  object Delete extends SpecialKey;
  object A extends SpecialKey;
  object B extends SpecialKey;
  object C extends SpecialKey;
  object D extends SpecialKey;
  object E extends SpecialKey;
  object F extends SpecialKey;
  object G extends SpecialKey;
  object H extends SpecialKey;
  object I extends SpecialKey;
  object J extends SpecialKey;
  object K extends SpecialKey;
  object L extends SpecialKey;
  object M extends SpecialKey;
  object N extends SpecialKey;
  object O extends SpecialKey;
  object P extends SpecialKey;
  object Q extends SpecialKey;
  object R extends SpecialKey;
  object S extends SpecialKey;
  object T extends SpecialKey;
  object U extends SpecialKey;
  object V extends SpecialKey;
  object W extends SpecialKey;
  object X extends SpecialKey;
  object Y extends SpecialKey;
  object Z extends SpecialKey;
  object BackspaceForIME extends SpecialKey;

  object KeepAlive extends SpecialKey;
  object ConsoleDead extends SpecialKey;
  object Unknown extends SpecialKey;

  private val keys: Map[String, SpecialKey] = Map(
    "Esc" -> Esc,
    "Backspace" -> Backspace,
    "Tab" -> Tab,
    "Enter" -> Enter,
    "Space" -> Space,
    "PageUp" -> PageUp,
    "PageDown" -> PageDown,
    "End" -> End,
    "Home" -> Home,
    "Left" -> Left,
    "Up" -> Up,
    "Right" -> Right,
    "Down" -> Down,
    "Insert" -> Insert,
    "Delete" -> Delete,
    "A" -> A,
    "B" -> B,
    "C" -> C,
    "D" -> D,
    "E" -> E,
    "F" -> F,
    "G" -> G,
    "H" -> H,
    "I" -> I,
    "J" -> J,
    "K" -> K,
    "L" -> L,
    "M" -> M,
    "N" -> N,
    "O" -> O,
    "P" -> P,
    "Q" -> Q,
    "R" -> R,
    "S" -> S,
    "T" -> T,
    "U" -> U,
    "V" -> V,
    "W" -> W,
    "X" -> X,
    "Y" -> Y,
    "Z" -> Z,
    "KeepAlive" -> KeepAlive,
    "ConsoleDead" -> ConsoleDead,
    "Unknown" -> Unknown
  );

  def getSpecialKeyEvent(key: String): SpecialKeyEvent = {
    val p = key.indexOf('+');
    if(p >= 0){
      key.substring(0, p) match {
        case "Alt" => getSpecialKeyEvent(key.substring(p + 1)).getUpdateAlt(true);
        case "Ctrl" => getSpecialKeyEvent(key.substring(p + 1)).getUpdateCtrl(true);
        case "Shift" => getSpecialKeyEvent(key.substring(p + 1)).getUpdateShift(true);
        case _ => SpecialKeyEvent(Unknown);
      }
    } else {
      SpecialKeyEvent(keys.getOrElse(key, Unknown));
    }
  }

}


