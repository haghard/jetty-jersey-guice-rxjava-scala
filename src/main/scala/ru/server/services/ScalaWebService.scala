package ru.server.services

import scala.util.Try
import org.slf4j.LoggerFactory
import scala.concurrent.Promise
import rx.lang.scala.Observable
import java.util.concurrent.TimeUnit

object ScalaWebService {
  def apply() = new ScalaWebService
}

/**
 *
 *
 */
class ScalaWebService extends WebService {

  private val logger = LoggerFactory.getLogger("general")
  import rx.lang.scala.JavaConversions.toJavaObservable
  implicit val executionCtx = scala.concurrent.ExecutionContext.fromExecutor(externalIOExecutor)

  def stream(): rx.Observable[_ <:String] = {
    val p = Promise[String]
    val future = WS.execute()
    future.addListener(new Runnable {
      override def run = {
        logger.info("IO:" + Thread.currentThread.getName)
        val response =
          Try(future.get) map { r =>
            r.getStatusCode match {
              case c => { if (c < 400) r.getResponseBody else "ResponseBodyError" }
            }
          } recover { case e: Exception =>
            e.getMessage
          }

        p.success(response.get)
      }
    }, externalIOExecutor)

    Observable.from(p.future)(executionCtx)
  }
}