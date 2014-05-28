package ru.server

import java.util.concurrent.{ThreadFactory, Executors, ExecutorService}
import java.util.concurrent.atomic.AtomicInteger
import com.ning.http.client.{AsyncHttpClientConfig, AsyncHttpClient}

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
}
