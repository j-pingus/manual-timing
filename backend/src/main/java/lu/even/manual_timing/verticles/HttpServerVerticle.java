package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

public class HttpServerVerticle extends AbstractVerticle {
    private final int port;
    private Router router;
    private EventBus bus;

    public HttpServerVerticle(int port) {
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        router = Router.router(vertx);
        bus = vertx.eventBus();

        // Body handler for parsing request bodies
        router.route().handler(BodyHandler.create());
        //Configuration of specific business domain rest endpoints
        this.routeGet("/api/poolconfig", EventTypes.POOL_CONFIG);
        this.routePost("/api/registration", EventTypes.REGISTER);
        //Generic failure management
        router.route().failureHandler(handler -> {
            System.out.println("Error happened during routing:" + handler.failure());
            if (handler.failure() != null) {
                handler.failure().printStackTrace();
            }
            handler.response().end(handler.failure() != null ? handler.failure().getMessage() : "Internal error");
        });
        // Allow message events to be bridged to JavaScript client
        SockJSBridgeOptions opts = new SockJSBridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddress(EventTypes.MESSAGE.getName()));
        // Create the event bus bridge and add it to the router.
        router.mountSubRouter("/api/eventbus", SockJSHandler.create(vertx).bridge(opts));

        vertx.createHttpServer().requestHandler(router).listen(port, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port " + port);
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    public void routeGet(String url, EventTypes eventType) {
        router.get(url).handler(getRoutingHandler(eventType, EventAction.GET));
    }
    public void routePost(String url, EventTypes eventType) {
        router.post(url).handler(getRoutingHandler(eventType, EventAction.POST));
    }

    private Handler<RoutingContext> getRoutingHandler(EventTypes eventType, EventAction action) {
        return event -> bus.<String>request(
                eventType.getName(),
                new EventMessage(action, event.body().asString()),
                reply -> {
                    if (reply.succeeded()) {
                        event.response().end(reply.result().body());
                    } else {
                        event.fail(500);
                    }
                });
    }

}
