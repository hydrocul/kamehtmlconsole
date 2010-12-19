package hydrocul.kamehtmlconsole;

trait ConsoleListener {

  /**
   * ユーザからの入力などを受け付ける。
   *
   * 複数のスレッドから同時に呼び出されることはない。
   */
  def input(event: KeyEvent);

}
