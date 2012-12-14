package filemanager

import play.api.mvc.MultipartFormData.FilePart
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.Files.TemporaryFile

object FileManager {
  def getStorage(storage:String = "s3",bucketName:String):Storage = storage.toLowerCase match {
    case "s3" => S3Storage(bucketName)
  }

  def add(filePart: FilePart[TemporaryFile], path: String = "")(implicit storage: Storage): Future[String] = {
    storage.add(path + filePart.filename, filePart.ref.file)
  }

  def delete(fileName: String, path: String = "")(implicit storage: Storage): Future[String] = {
    storage.delete(path + fileName)
  }

  def getList(path: String = "")(implicit storage: Storage): Future[List[String]] = {
    storage.getList(path)
  }

}
