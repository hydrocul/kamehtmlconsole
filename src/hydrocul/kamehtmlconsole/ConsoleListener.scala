package hydrocul.kamehtmlconsole;

trait ConsoleListener {

  def userText(count: Int, text: String);

  def specialKey(count: Int, key: String);

  def specialCommand(count: Int, command: String);

}
