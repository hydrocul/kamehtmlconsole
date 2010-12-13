package hydrocul.kamehtmlconsole;

import java.util.{ List => JList }

import scala.collection.JavaConversions._;
import scala.io.BufferedSource;
import scala.io.Source;

import net.arnx.jsonic.JSON;

import hydrocul.kameq.scala.Pipe.ioexec;
import hydrocul.util.StringLib;

object GoogleImeEngine extends ImeEngine {

  def searchSuggestions(text: String, listener: ImeEngineListener){

    val text2 = text; // TODO

    ioexec {
      accessGoogleAPI(text2);
    } | { result: List[APIResponseItem] =>
      null; // TODO
    }

  }

  private[kamehtmlconsole] def extractRoma(text: String): String = {
    "" // TODO extractRoma
  }

  private val romaPattern = "\\A.*?([a-z][-a-z])\\z".r;

  private[kamehtmlconsole] def accessGoogleAPI(text: String): List[APIResponseItem] = {

    def using[A <: { def close(); }, B](resource: A)(p: A=>B): B = {
      try {
        p(resource);
      } finally {
        resource.close();
      }
    }

    val apiUrl = "http://www.google.com/transliterate?langpair=ja-Hira|ja&text=" +
      StringLib.encodeUrl(text);
    val response: String = using(Source.fromURL(apiUrl, "UTF-8")){ s: BufferedSource =>
      s.getLines.mkString("\n"); }
    val list: List[AnyRef] = JSON.decode[JList[AnyRef]](response).toList.filter(_ != null);

    list.map(_.asInstanceOf[JList[AnyRef]]).map { a: JList[AnyRef] =>
      val word = a(0).asInstanceOf[String];
      val kanji = a(1).asInstanceOf[JList[String]];
      APIResponseItem(word, kanji.toList);
    }

  }

  private[kamehtmlconsole] case class APIResponseItem(word: String, kanji: List[String]);

}
