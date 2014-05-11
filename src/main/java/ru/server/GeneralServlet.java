package ru.server;


import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.server.services.api.WebService;
import rx.functions.Action1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.io.Writer;

public class GeneralServlet extends HttpServlet {

  private WebService webService;

  private static Logger logger = LoggerFactory.getLogger("general");

  public GeneralServlet(WebService webService) {
    this.webService = webService;
  }

  public void init() {}

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    switch (req.getMethod()) {
      case HttpMethod.GET: doGet(req, resp); return;
      case HttpMethod.POST: doPost(req, resp); return;
      default: throw new IllegalArgumentException("unexpected method");
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    logger.info("doGet:" + Thread.currentThread().getName());
    final Continuation cnt = ContinuationSupport.getContinuation(req);

    //aka retry
    //cnt.setTimeout(5000);
    cnt.suspend(resp);

    webService.stream().subscribe(new Action1<String>() {
      @Override
      public void call(String response) {
        logger.info("Notify:" + Thread.currentThread().getName());
        respond(response + "\n", cnt);
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        respond(throwable.getMessage(), cnt);
      }
    });
  }

  private void respond(String ctx, Continuation cnt) {
    try {
      final Writer wr = cnt.getServletResponse().getWriter();
      wr.write(ctx);
      wr.flush();
      wr.close();
    } catch (IOException e) {
    } finally {
      cnt.complete();
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    logger.info("service");
  }
}