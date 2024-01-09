package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpMethod;
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
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(AbstractTimingVerticle.class);
  private final int port;
  private final String indexBody;
  private Router router;
  private EventBus bus;

  public HttpServerVerticle(int port) throws IOException {

    this.port = port;
    indexBody = IOUtils.resourceToString("/frontend/browser/index.html", Charset.defaultCharset());

  }

  @Override
  public void start(Promise<Void> startPromise) {
    router = Router.router(vertx);
    bus = vertx.eventBus();
    // Body handler for parsing request bodies
    router.route().handler(BodyHandler.create());
    //Configuration of specific business domain rest endpoints
    // ==== Pool Config ====
    this.routeGet("/api/poolconfig", EventTypes.POOL_CONFIG);
    // ==== User ====
    this.routePost("/api/registration", EventTypes.REGISTER);
    this.routeGet("/api/registrations/lane/:lane", EventTypes.REGISTER, EventAction.GET_BY_LANE);
    this.routePut("/api/registration", EventTypes.REGISTER);
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
    router.route("/*").method(HttpMethod.GET).handler(rc -> {
      logger.info("rerouting:{}", rc.request().path());
      rc.response().send(indexBody);
    });
    vertx.createHttpServer(
        new HttpServerOptions()
          .setSsl(true)
          .setKeyStoreOptions(
            new JksOptions()
              .setPassword("changeit")
              .setPath("./security/keystore.jks"))
      )
      .requestHandler(router).listen(port, http -> {

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
    return event -> bus.<String>request(
      eventType.getName(),
      new EventMessage(
        action,
        event.body().asString(),
        getId(event.request(), "event"),
        getId(event.request(), "heat"),
        getId(event.request(), "lane"),
        event.request().getHeader("Authorization")
      ),
      reply -> {
        if (reply.succeeded()) {
          event.response().end(reply.result().body());
        } else {
          event.fail(500, reply.cause());
        }
      });
  }

  private int getId(HttpServerRequest request, String param) {
    String paramValue = request.getParam(param);
    if (paramValue != null) {
      return Integer.parseInt(paramValue);
    }
    return -1;
  }

}
