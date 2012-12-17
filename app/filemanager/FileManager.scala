package filemanager

import play.api.mvc.MultipartFormData.FilePart
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.Files.TemporaryFile

import java.util.UUID
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io._
import org.imgscalr.Scalr

object FileManager {
  def getStorage(storage: String = "s3", bucketName: String): Storage = storage.toLowerCase match {
    case "s3" => S3Storage(bucketName)
  }


  def resizeImage(filePart: FilePart[TemporaryFile], maxium_width_and_height: Int): Future[Array[Byte]] = {
    val file = filePart.ref.file
    val contentType: String = filePart.contentType.getOrElse("application/octet-stream")

    resizeImage(contentType, file, maxium_width_and_height)
  }

  def resizeImage(contentType: String, byteArray: Array[Byte], maxium_width_and_height: Int): Future[Array[Byte]] = {
    for {
      bufferedImage <- future(ImageIO.read(new ByteArrayInputStream(byteArray)))
      result <- resizeImage(contentType, bufferedImage, maxium_width_and_height)
    } yield {
      result
    }
  }

  def resizeImage(contentType: String, file: File, maxium_width_and_height: Int): Future[Array[Byte]] = {
    for {
      bufferedImage <- future(ImageIO.read(file))
      result <- resizeImage(contentType, bufferedImage, maxium_width_and_height)
    } yield {
      result
    }
  }

  private def resizeImage(contentType: String, bufferedImage: BufferedImage, maxium_width_and_height: Int): Future[Array[Byte]] =
    future {
      val ext = contentType.split("/")(1)

      // Image I/O has built-in support for GIF, PNG, JPEG, BMP, and WBMP
      List("GIF", "PNG", "JPEG", "BMP", "WBMP").contains(ext.toUpperCase) match {
        case false =>
          throw new IllegalArgumentException("Unmatchable content type")
        case true =>
          val resize = bufferedImage.getHeight() min bufferedImage.getWidth() min maxium_width_and_height

          val thumbnail: BufferedImage = Scalr.resize(bufferedImage, resize)

          val byteArrayOutputStream: ByteArrayOutputStream = new ByteArrayOutputStream()

          ImageIO.write(thumbnail, ext, byteArrayOutputStream)

          byteArrayOutputStream.toByteArray()
      }
    }


  def upload[A](filePart: FilePart[A], path: String = "")(implicit storage: Storage): Future[String] =
    filePart.ref match {
      case tempFile:TemporaryFile => storage.add(path + filePart.filename, tempFile.file)
      case _ => Future.failed(new IllegalArgumentException("file part is not a file"))
    }

  def upload(fileName: String, byteArray: Array[Byte], contentType: String)(implicit storage: Storage): Future[String] = {
    storage.add(fileName, new ByteArrayInputStream(byteArray), contentType)
  }

  def delete(fileName: String, path: String = "")(implicit storage: Storage): Future[String] = {
    storage.delete(path + fileName)
  }

  def getList(path: String = "")(implicit storage: Storage): Future[List[String]] = {
    storage.getList(path)
  }

}
