package lu.even.manual_timing.logger;

import io.vertx.core.MultiMap;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class ActivityLogger {
  private static final Logger logger = LoggerFactory.getLogger(ActivityLogger.class);

  public static void logToSsl(HttpServerRequest request) {
    log("toSsl",request);
  }
  public static void log(String prefix,HttpServerRequest request) {
    logger.trace(prefix+":[{}]{}:{}\nparams:{}\ncookies:{}",
      request.connection().remoteAddress().host(),
      request.method().name(),
      request.path(),
      toString(request.params()),
      toString(request.cookies())
    );
  }

  private static String toString(MultiMap params) {
    return params.names().toString();
  }

  private static String toString(Set<Cookie> cookies) {
    return cookies.stream().map(Cookie::getName).toList().toString();
  }

  public static void logAPI(HttpServerRequest request) {
    log("api",request);
  }

  public static void logIndex(HttpServerRequest request) {
    log("toIndex",request);
  }
}
