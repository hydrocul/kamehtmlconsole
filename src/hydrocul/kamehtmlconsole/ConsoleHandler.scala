package hydrocul.kamehtmlconsole;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
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

    def sendError(statucCode: Int, msg: String);

  }

  def handle(path: String, request: Request, response: Response){
    // path: request path, empty string or string which starts with "/"

    if(path.startsWith("/etc/")){
      handleEtc(path.substring(4), request, response);
    } else if(path.startsWith("/i/")){
      handleUserInput(path.substring(2), request. response);
    } else if(path.startsWith("/c/")){
      handleConsole(path.substring(2), request, response);
    } else {
      handleHome(path, request, response);
    }

  }

  private def handleEtc(path: String, request: Request, response: Response){
    val fname = path.substring(1);
    val resourceIp = getClass.getResourceAsStream(fname);
    if(resourceIp==null){
      response.sendError(404, "Not Found");
      return;
    }
    response.setStatus(200);
    if(path.endsWith(".html")){
      response.setContentType("text/html;charset=utf-8");
    } else if(path.endsWith(".js")){
      response.setContentType("text/javascript;charset=utf-8");
    } else if(path.endsWith(".css")){
      response.setContentType("text/css;charset=utf-8");
    } else if(path.endsWith(".png")){
      response.setContentType("image/png");
    } else if(path.endsWith(".gif")){
      response.setContentType("image/gif");
    } else if(path.endsWith(".jpg")){
      response.setContentType("image/jpeg");
    }
    val bufferedResourceIp = new BufferedInputStream(resourceIp);
    val responseOp = new BufferedOutputStream(response.getOutputStream);
    try {
      val buf = new Array[byte](1024);
      while(true){
        val len = bufferedResourceIp.read(buf, 0, buf.length);
        if(len < 0)
          break;
        responseOp.write(buf, 0, len);
      }
    } finally {
      responseOp.close();
      bufferedResourceIp.close();
    }
  }

  private def handleUserInput(path: String, request: Request, response: Response){
  }

  private def handleConsole(path: String, request: Request, response: Response){
  }

  private def handleHome(path: String, request: Request, response: Response){
  }

}
