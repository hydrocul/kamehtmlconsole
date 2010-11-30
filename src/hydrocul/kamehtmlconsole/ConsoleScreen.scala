package hydrocul.kamehtmlconsole;

import java.io.PrintWriter;

trait ConsoleScreen {

  def getRefreshJavaScriptCode(): String;

}

private[kamehtmlconsole] class ConsoleScreenImpl(console: Console) extends ConsoleScreen {

  private var lines: ConsoleLinesInfo = console.getLinesInfo

  def getRefreshJavaScriptCode(): String = {
    val oldLines = lines;
    val newLines = console.getLinesInfo;
    lines = newLines;
    if(oldLines.counter==newLines.counter){
      "";
    } else {
      getRefreshJavaScriptCodeSub(oldLines.lines, newLines.lines);
    }
  }

  private def getRefreshJavaScriptCodeSub(oldLines: IndexedSeq[ConsoleLineInfo],
    newLines: IndexedSeq[ConsoleLineInfo]): String = {

    import scala.annotation.tailrec;

    /**
     * Create javascript code. List of PrintWriter=>Unit を連続して呼び出すことで、
     * JavaScript code を生成できる。
     */
    @tailrec
    def sub(oldLines: IndexedSeq[ConsoleLineInfo], newLines: IndexedSeq[ConsoleLineInfo], deleteIndex: Int, prevLine: ConsoleLineInfo): List[PrintWriter=>Unit] = {
      if(oldLines.isEmpty){
        // TODO
      } else {
        val oldFirst = oldLines(0);
        if(deleteIndex > 0){
          ((writer: PrintWriter) => ConsoleLineInfo.printDelete(writer, oldFirst)) :: sub(oldLines.drop(1), newLines, i - 1, prevLine);
        } else if(newLines.isEmpty){
          // TODO
        } else {
          val newFirst = newLines(0);
          if(oldFirst.lineId==newFirst.lineId){
            ((writer: PrintWriter) => ConsoleLineInfo.printUpdate(writer, oldLine, newLine)) :: sub(oldLines.drop(1), newLines.drop(1), 0, newFirst);
          } else {
            val i = oldLines.findIndexOf(_.lineId==newFirst.lineId);
            if(i >= 0){
              ((writer: PrintWriter) => ConsoleLineInfo.printDelete(writer, oldFirst)) :: sub(oldLines.drop(1), newLines, i - 1, prevLine);
            } else if(prevLine==null){
              ((writer: PrintWriter) => ConsoleLineInfo.printInsertFirst(writer, newFirst)) :: sub(oldLines, newLines.drop(1), 0, newFirst);
            } else {
              ((writer: PrintWriter) => ConsoleLineInfo.printInsertAfter(writer, prevLine, newFirst)) :: sub(oldLines, newLines.drop(1), 0, newFirst);
            }
          }
        }
      }
    }

    "def";
    // TODO getRefreshJavaScriptCodeSub
  }

}

