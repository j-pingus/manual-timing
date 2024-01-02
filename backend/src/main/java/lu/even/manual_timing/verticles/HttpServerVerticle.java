package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
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
        this.routeGet("/api/registrations/lane/:lane", EventTypes.REGISTER,EventAction.GET_BY_LANE);
        this.routePut("/api/registration", EventTypes.REGISTER);
        this.routeGet("/api/events", EventTypes.EVENT, EventAction.GET_ALL);
        this.routePost("/api/event", EventTypes.EVENT);
        this.routePost("/api/inscription", EventTypes.INSCRIPTION);
        this.routeGet("/api/inscriptions/:event/lane/:lane", EventTypes.INSCRIPTION,EventAction.GET_BY_EVENT_LANE);
        this.routeGet("/api/inscriptions/:event/heat/:heat", EventTypes.INSCRIPTION,EventAction.GET_BY_EVENT_HEAT);
        this.routePost("/api/time", EventTypes.MANUAL_TIME);
        this.routeGet("/api/meetmanager/reload", EventTypes.MEET_MANAGER,EventAction.LOAD_EVENTS);
        this.routeGet("/api/meetmanager/reloadheat/:event/:heat", EventTypes.MEET_MANAGER,EventAction.LOAD_HEAT);
        this.routeGet("/api/times/:event/lane/:lane", EventTypes.MANUAL_TIME,EventAction.GET_BY_EVENT_LANE);
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
        router.route("/api/eventbus/*").subRouter(SockJSHandler.create(vertx).bridge(opts));

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

    public void routeGet(String url, EventTypes eventType, EventAction action) {
        router.get(url).handler(getRoutingHandler(eventType, action));
    }

    public void routePost(String url, EventTypes eventType) {
        router.post(url).handler(getRoutingHandler(eventType, EventAction.POST));
    }

    public void routePut(String url, EventTypes eventType) {
        router.put(url).handler(getRoutingHandler(eventType, EventAction.PUT));
    }

    private Handler<RoutingContext> getRoutingHandler(EventTypes eventType, EventAction action) {
        return event ->
                //
                bus.<String>request(
                        eventType.getName(),
                        new EventMessage(
                                action,
                                event.body().asString(),
                                getId(event.request(),"event"),
                                getId(event.request(),"heat"),
                                getId(event.request(),"lane")
                        ),
                        reply -> {
                            if (reply.succeeded()) {
                                event.response().end(reply.result().body());
                            } else {
                                event.fail(500);
                            }
                        });
    }

    private int getId(HttpServerRequest request, String param) {
        String paramValue = request.getParam(param);
        if(paramValue!=null){
            return Integer.parseInt(paramValue);
        }
        return -1;
    }

}
