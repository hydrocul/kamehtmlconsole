package hydrocul.kamehtmlconsole.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import hydrocul.util.ObjectPool;

trait HttpHandler {

  import HttpHandler._;

  /**
   *
   *
   * @param path request path, empty string or string which starts with "/"
   */
  def handle(path: String, request: Request, response: Response);

}

object HttpHandler {

  trait Request {

    def getParameter(name: String): String;

  }

  trait Response {

    def setContentType(contentType: String);

    def setStatus(statucCode: Int);

    def getWriter(charsetEncoding: String): PrintWriter;

    def getOutputStream(): OutputStream;

    def sendError(statucCode: Int, msg: String);

  }

}

private[kamehtmlconsole] class HttpHandlerImpl(objectPool: ObjectPool, baseUrl: String,
    listener: ConsoleListener) extends HttpHandler {

  import HttpHandler._;

  def handle(path: String, request: Request, response: Response){
    if(path.startsWith("/etc/")){
      handleEtc(path.substring(4), request, response);
    } else if(path.startsWith("/i/")){
      handleUserInput(path.substring(2), request, response);
    } else if(path.startsWith("/c/")){
      handleConsole(path.substring(2), request, response);
    } else {
      handleHome(path, request, response);
    }
  }

  /**
   * handle favicon.ico, loading.gif or other images, css,,,.
   */
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
      val buf = new Array[Byte](1024);
      while({
        val len = bufferedResourceIp.read(buf, 0, buf.length);
        if(len < 0){
          false;
        } else {
          responseOp.write(buf, 0, len);
          true;
        }
      }){};
    } finally {
      responseOp.close();
      bufferedResourceIp.close();
    }
  }

  private def handleUserInput(path: String, request: Request, response: Response){
    // TODO handleUserInput
  }

  private def handleConsole(path: String, request: Request, response: Response){
    // TODO handleConsole
  }

  private def handleHome(path: String, request: Request, response: Response){
    // TODO handleHome
  }

}
