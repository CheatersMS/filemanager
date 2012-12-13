package filemanager

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._

import com.amazonaws.{AmazonServiceException, AmazonClientException}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{Bucket => _, _}
import com.amazonaws.auth.BasicAWSCredentials

import play.api.Play._
import java.io._

private[filemanager] class S3Storage(accessKey: String, secretKey: String, bucket: String) extends Storage {
  private[this] val awsCredential = new BasicAWSCredentials(accessKey, secretKey)
  private[this] val client = new AmazonS3Client(awsCredential)


  def add(fileName: String, file: File) = {
    val request = new PutObjectRequest(bucket, fileName, file)
    request.setCannedAcl(CannedAccessControlList.PublicRead)

    getResult(client.putObject(request), fileName)
  }


  def add(fileName: String, stream: InputStream) = {
    getResult(client.putObject(bucket, fileName, stream, new ObjectMetadata()), fileName)
  }

  def getList(prefix: String = ""): Future[List[String]] = {
    /* 1000개 제한 있음 */
    future {
      client.listObjects("sparks3test", prefix).getObjectSummaries.asScala map {
        _.getKey
      } toList
    }
  }

  def delete(fileName: String): Future[String] = {
    for {
      success <- future(client.deleteObject(bucket, fileName))
    } yield {
      fileName
    }
  }

  private[this] def getResult(f: => PutObjectResult, fileName: String): Future[String] = {
    for {
      result <- future(f)
    } yield {
      fileName
    }
  }
}

private[filemanager] object S3Storage {
  def apply(bucket: String): S3Storage = {
    {
      for {
        accessKey <- configuration.getString("aws.accessKey")
        secretKey <- configuration.getString("aws.secretKey")
      } yield {
        new S3Storage(accessKey, secretKey, bucket)
      }
    } getOrElse {
      throw new NoSuchFieldException("Connot find awsKeys in application.conf")
    }
  }
}

