package hydrocul.kamehtmlconsole;

import hydrocul.kametest.Test;

object KeyEventTest {

  def test(){
    testGetSpecialKeyEvent("A", SpecialKeyEvent(SpecialKeyEvent.A));
    testGetSpecialKeyEvent("Delete", SpecialKeyEvent(SpecialKeyEvent.Delete));
    testGetSpecialKeyEvent("Ctrl+A", SpecialKeyEvent(SpecialKeyEvent.A, false, true, false));
    testGetSpecialKeyEvent("Ctrl+Alt+Esc", SpecialKeyEvent(SpecialKeyEvent.Esc, true, true, false));
    testGetSpecialKeyEvent("Ctrl+Alt+Alt+Up", SpecialKeyEvent(SpecialKeyEvent.Up, true, true, false));
    testGetSpecialKeyEvent("Ctrl+Alt+Shift+Up", SpecialKeyEvent(SpecialKeyEvent.Up, true, true, true));
  }

  private def testGetSpecialKeyEvent(key: String, expected: SpecialKeyEvent){
    val actual = SpecialKeyEvent.getSpecialKeyEvent(key);
    Test.assertEquals(key, expected, actual);
  }

}
