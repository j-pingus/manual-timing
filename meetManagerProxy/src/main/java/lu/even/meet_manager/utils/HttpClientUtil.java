package lu.even.meet_manager.utils;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import lu.even.RemoteServerConfig;

public class HttpClientUtil {
  public static HttpClient createClient(Vertx vertx, RemoteServerConfig httpConfig) {
    var httpOptions = new HttpClientOptions()
      .setDefaultHost(httpConfig.host())
      .setDefaultPort(httpConfig.port())
      .setSsl(httpConfig.ssl());
    return vertx.createHttpClient(httpOptions);
  }
}
