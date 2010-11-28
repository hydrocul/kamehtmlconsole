package hydrocul.kamehtmlconsole;

trait ConsoleScreen {

  def getRefreshJavaScriptCode(): String;

}

private[kamehtmlconsole] class ConsoleScreenImpl(console: Console) extends ConsoleScreen {

  private var lines: ConsoleLinesInfo = console.getLinesInfo

  def getRefreshJavaScriptCode(): String = {
    val prevLines = lines;
    val nextLines = console.getLinesInfo;
    lines = nextLines;
    if(prevLines.counter==nextLines.counter){
      "";
    } else {
      getRefreshJavaScriptCodeSub(prevLines.lines, nextLines.lines);
    }
  }

  private def getRefreshJavaScriptCodeSub(prevLines: IndexedSeq[ConsoleLineInfo],
    nextLines: IndexedSeq[ConsoleLineInfo]): String = {
    "";
    // TODO
  }

}

