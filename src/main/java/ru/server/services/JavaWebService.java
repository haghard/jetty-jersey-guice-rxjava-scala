package ru.server.services;

import com.google.common.util.concurrent.SettableFuture;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.server.services.api.WebService;
import rx.Observable;
import rx.schedulers.Schedulers;
import java.io.IOException;
import java.util.concurrent.*;

public class JavaWebService implements WebService {

  private static Logger logger = LoggerFactory.getLogger("general");

  public JavaWebService() {}

  private Future<String> invoke() {
    final SettableFuture<String> promise = SettableFuture.create();
    try {
      final ListenableFuture<Response> f = package$.MODULE$.WS().execute();
      f.addListener(() -> {
        logger.info("IO:" + Thread.currentThread().getName());
        try {
          final Response response = f.get(package$.MODULE$.timeout(), TimeUnit.MILLISECONDS);
          if (response.getStatusCode() < 400) {
            promise.set(response.getResponseBody());
          } else {
            promise.set("ResponseBodyError");
          }
        } catch (InterruptedException | ExecutionException |
            TimeoutException | IOException e) {
          promise.set("Execution Error");
        }
      }, package$.MODULE$.externalIOExecutor());
    } catch (IOException e) {
      promise.set("IOException Error");
    }
    return promise;
  }

  public Observable<? extends String> stream() {
    return Observable.from(invoke(), Schedulers.executor(package$.MODULE$.externalIOExecutor()));
  }
}
