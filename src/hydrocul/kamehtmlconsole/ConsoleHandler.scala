package hydrocul.kamehtmlconsole;

import java.io.PrintWriter;

import hydrocul.util.ObjectPool;

class ConsoleHandler(objectPool: ObjectPool){

  trait Request {

    def getParameter(name: String): String;

  }

  trait Response {

    def setContentType(contentType: String);

    def setStatus(statucCode: Int);

    def getWriter(): PrintWriter;

  }

  def handle(path: String, request: Request, response: Response){
    // path: request path, empty string or string which starts with "/"

    if(path.startsWith("/etc/")){
      handleEtc(path.substring(5), request, response);
    } else if(path.startsWith("/i/")){
    }


  }

}
