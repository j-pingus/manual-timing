package lu.even.manual_timing.logger;

import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityLogger {
  private static final Logger logger = LoggerFactory.getLogger(ActivityLogger.class);

  public static void log(HttpServerRequest request) {
    logger.trace("{}/{} requested:{}",
      request.connection().remoteAddress().host(),
      request.connection().remoteAddress().hostName(),
      request.path()
    );
  }
}
