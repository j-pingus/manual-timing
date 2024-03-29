package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import lu.even.manual_timing.domain.SslConfig;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import lu.even.manual_timing.logger.ActivityLogger;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);
  private final int port;
  private final SslConfig ssl;
  private final String indexBody;
  private Router router;
  private EventBus bus;

  public HttpServerVerticle(int port, SslConfig ssl) throws IOException {

    this.port = port;
    this.ssl = ssl;
    indexBody = IOUtils.resourceToString("/frontend/browser/index.html", Charset.defaultCharset());

  }

  @Override
  public void start(Promise<Void> startPromise) {
    router = Router.router(vertx);
    bus = vertx.eventBus();
    // Body handler for parsing request bodies
    var bodyHandler = BodyHandler.create(false);
    bodyHandler.setBodyLimit(500 * 1024);
    router.route().handler(bodyHandler);
    //Configuration of specific business domain rest endpoints
    // ==== Pool Config ====
    this.routeGet("/api/poolconfig", EventTypes.POOL_CONFIG);
    // ==== User ====
    this.routePost("/api/user", EventTypes.USER);
    this.routeGet("/api/users/lane/:lane", EventTypes.USER, EventAction.GET_BY_LANE);
    // ==== Event ====
    this.routeGet("/api/events", EventTypes.EVENT, EventAction.GET_ALL);
    this.routeGet("/api/event/:event", EventTypes.EVENT);
    this.routeGet("/api/events/dump", EventTypes.EVENT, EventAction.DUMP);
    this.routeGet("/api/events/load", EventTypes.EVENT, EventAction.LOAD);
    this.routePost("/api/event", EventTypes.EVENT);
    this.routePost("/api/events", EventTypes.EVENT, EventAction.REPLACE_EVENTS);
    // ==== Inscriptions ====
    this.routePost("/api/inscription", EventTypes.INSCRIPTION);
    this.routePost("/api/inscriptions/:event/heat/:heat", EventTypes.INSCRIPTION, EventAction.REPLACE_INSCRIPTIONS);
    this.routeGet("/api/inscriptions/load", EventTypes.INSCRIPTION, EventAction.LOAD);
    this.routeGet("/api/inscriptions/dump", EventTypes.INSCRIPTION, EventAction.DUMP);
    this.routeGet("/api/inscriptions/:event/lane/:lane", EventTypes.INSCRIPTION, EventAction.GET_BY_EVENT_LANE);
    this.routeGet("/api/inscriptions/:event/heat/:heat", EventTypes.INSCRIPTION, EventAction.GET_BY_EVENT_HEAT);
    // ==== time ====
    this.routePost("/api/time", EventTypes.MANUAL_TIME);
    this.routeGet("/api/times/:event/lane/:lane", EventTypes.MANUAL_TIME, EventAction.GET_BY_EVENT_LANE);
    this.routeGet("/api/times/:event/heat/:heat", EventTypes.MANUAL_TIME, EventAction.GET_BY_EVENT_HEAT);
    //Generic failure management
    router.route().failureHandler(this::errorHandler);
    // Allow message events to be bridged to JavaScript client
    // Create the event bus bridge and add it to the router.
    router.route("/api/eventbus/*").subRouter(this.createSocksHandler());

    //Handle static content in webcontent resource folder
    router.route().handler(StaticHandler.create("frontend/browser"));
    //Hack for angular
    router.route("/*").handler(rc -> {
      ActivityLogger.logIndex(rc.request());
      rc.response().send(indexBody);
    });
    HttpServer httpServer;
    if (ssl != null) {
      var jksOptions = new JksOptions()
        .setPassword(ssl.password())
        .setPath(ssl.keystore());
      if (ssl.alias() != null) {
        jksOptions.setAlias(ssl.alias());
      }
      httpServer = vertx.createHttpServer(
        new HttpServerOptions()
          .setSsl(true)
          .setKeyStoreOptions(jksOptions)
      );
      logger.info("Starting http with ssl:{}", ssl);
      if (ssl.redirect()) {
        vertx
          .createHttpServer()
          .requestHandler(r -> {
            ActivityLogger.logToSsl(r);
            r.response()
              .setStatusCode(301)
              .putHeader("Location", r.absoluteURI().replace("http", "https"))
              .end();
          })
          .listen(80, http -> {
            if (http.succeeded()) {
              logger.info("HTTP server started on port:{} redirecting to https ", 80);
            }
          });
      }
    } else {
      httpServer = vertx.createHttpServer();
    }

    httpServer.requestHandler(router).listen(port, http -> {

      if (http.succeeded()) {
        startPromise.complete();
        logger.info("HTTP server started on port:{} ", port);
      } else {
        startPromise.fail(http.cause());
      }
    });

  }

  private Router createSocksHandler() {
    //Generic failure management
    router.route().failureHandler(this::errorHandler);
    // Allow message events to be bridged to JavaScript client
    SockJSBridgeOptions opts = new SockJSBridgeOptions()
      .addOutboundPermitted(new PermittedOptions().setAddress(EventTypes.MESSAGE.getName()));
    // Create the event bus bridge and add it to the router.
    return SockJSHandler.create(vertx).bridge(opts);
  }

  private void errorHandler(RoutingContext routingContext) {
    logger.error("Error happened during routing:{}", routingContext.failure(), routingContext.failure());
    Throwable failure = routingContext.failure();
    if (failure instanceof ReplyException) {
      routingContext.response()
        .setStatusCode(((ReplyException) failure).failureCode())
        .setStatusMessage(failure.getMessage())
        .end();

    } else {
      routingContext.response()
        .setStatusCode(500)
        .setStatusMessage(routingContext.failure() != null ?
          routingContext.failure().getMessage() : "Internal error")
        .end();
    }
  }

  public void routeGet(String url, EventTypes eventType) {
    router.get(url).handler(getRoutingHandler(eventType, EventAction.GET));
  }

  public void routeGet(String url, EventTypes eventType, EventAction action) {
    router.get(url).handler(getRoutingHandler(eventType, action));
  }

  public void routePost(String url, EventTypes eventType) {
    this.routePost(url, eventType, EventAction.POST);
  }

  public void routePost(String url, EventTypes eventType, EventAction action) {
    router.post(url).handler(getRoutingHandler(eventType, action));
  }

  public void routePut(String url, EventTypes eventType) {
    router.put(url).handler(getRoutingHandler(eventType, EventAction.PUT));
  }

  private Handler<RoutingContext> getRoutingHandler(EventTypes eventType, EventAction action) {
    return event -> {
      ActivityLogger.logAPI(event.request());
      bus.<String>request(
        eventType.getName(),
        new EventMessage(
          action,
          event.body().asString(),
          getId(event.request(), "event"),
          getId(event.request(), "heat"),
          getId(event.request(), "lane"),
          getId(event.request(), "distance"),
          event.request().getHeader("Authorization")
        ),
        reply -> {
          if (reply.succeeded()) {
            event.response().end(reply.result().body());
          } else {
            event.fail(500, reply.cause());
          }
        });
    };
  }

  private int getId(HttpServerRequest request, String param) {
    String paramValue = request.getParam(param);
    if (paramValue != null) {
      return Integer.parseInt(paramValue);
    }
    return -1;
  }

}
