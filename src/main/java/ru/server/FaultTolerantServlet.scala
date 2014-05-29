package ru.server

import javax.ws.rs.HttpMethod
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import org.eclipse.jetty.continuation.ContinuationSupport
import org.slf4j.{LoggerFactory, Logger}

object FaultTolerantServlet {
  def apply() = new FaultTolerantServlet()
}

class FaultTolerantServlet extends HttpServlet {
  private val logger: Logger = LoggerFactory.getLogger("general")

  protected override def service(req: HttpServletRequest, resp: HttpServletResponse) =
    req.getMethod match {
      case HttpMethod.GET => doGet(req, resp)
      case HttpMethod.POST => doPost(req, resp)
    }

  import ru.server.services._
  protected override def doGet(req: HttpServletRequest, resp: HttpServletResponse) =
    respondAsync(ContinuationSupport.getContinuation(req), resp) { wr =>
      val result = BlockingIOCommand(4, 300).execute
      logger.debug("{}", result)
      wr.write(result)
    }
}