package ru.server

import java.util.concurrent.{ThreadFactory, Executors, ExecutorService}
import java.util.concurrent.atomic.AtomicInteger
import com.ning.http.client.{AsyncHttpClientConfig, AsyncHttpClient}
import rx.functions.Action1
import org.eclipse.jetty.continuation.Continuation
import java.io.PrintWriter
import javax.servlet.http.HttpServletResponse

package object services {

  private[services] var timeout: Int = 2000
  private[services] final val URL = "http://www.ya.ru"

  private[services] val externalIOExecutor: ExecutorService = Executors.newFixedThreadPool(20, new ThreadFactory {
    final val counter: AtomicInteger = new AtomicInteger

    def newThread(r: Runnable) = {
      val t: Thread = new Thread(r, "remote-io-thread-" + counter.incrementAndGet)
      t setDaemon(true)
      t
    }
  })

  private[services] final val executionCtx = scala.concurrent.ExecutionContext.fromExecutor(externalIOExecutor)

  private[services] val WS: AsyncHttpClient#BoundRequestBuilder =
    new AsyncHttpClient(new AsyncHttpClientConfig.Builder()
      .setConnectionTimeoutInMs(timeout)
      .setExecutorService(externalIOExecutor).build)
      .prepareGet(URL)

  implicit def action1ToFunc[T](f: T => Unit) = {
    new Action1[T] {
      override def call(a: T) = f(a)
    }
  }

  def respondAsync(c: Continuation, resp: HttpServletResponse)(f: PrintWriter => Unit) = {
    c.suspend(resp)
    val writer = c.getServletResponse.getWriter
    try {
      f(writer)
    } finally {
      writer.flush
      writer.close
      c.complete
    }
  }

  //http://www.jroller.com/ouertani/entry/scala_try_with_resources
  def tryWith[T <% AutoCloseable,E](w: T, c: Continuation)(f: T => Unit) = {
    try {
      f(w)
    }
    finally {
      w.close
      c.complete
    }
  }
}
