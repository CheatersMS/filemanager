package filemanager

import scala.concurrent.Future
import java.io._

private[filemanager] trait Storage {
  def add(fileName: String, file: File):Future[String]

  def add(fileName: String, stream: InputStream):Future[String]

  def getList(prefix:String = ""):Future[List[String]]

  def delete(fileName:String):Future[String]
}
