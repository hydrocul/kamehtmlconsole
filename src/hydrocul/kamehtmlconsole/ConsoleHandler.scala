package hydrocul.kamehtmlconsole;

class ConsoleHandler {

  trait handlingListener {
    def complete();
  }

  def startHandling(path: String, request: HttpServletRequest, response: HttpServletResponse,
    listener: handlingListener){
    
  }

}
