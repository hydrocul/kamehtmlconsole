package hydrocul.kamehtmlconsole;

trait ConsoleListener {

  /**
   * ユーザからの入力などを受け付ける。
   */
  def input(event: KeyEvent, onComplete: Runnable);

}
