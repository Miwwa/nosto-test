package com.mikhaile.nostobackend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CSPHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.json.schema.*;

import java.math.BigDecimal;

import static io.vertx.json.schema.common.dsl.Schemas.numberSchema;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;
import static io.vertx.json.schema.draft7.dsl.Keywords.minimum;

public class MainVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    private CurrencyRateProvider currencyRateProvider;
    private CurrencyConverterService currencyConverterService;

    public static MainVerticle Build(CurrencyRateProvider currencyRateProvider) {
        var main = new MainVerticle();
        main.currencyRateProvider = currencyRateProvider;
        return main;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        if (currencyRateProvider == null) {
            String swopApiKey = System.getenv("SWOP_API_KEY");
            if (swopApiKey == null || swopApiKey.isEmpty()) {
                throw new RuntimeException("Must provide SWOP_API_KEY env variable");
            }
            currencyRateProvider = new CurrencyRateProviderSwop(vertx, swopApiKey);
        }
        currencyConverterService = new CurrencyConverterService(currencyRateProvider);

        // allow any origin for simplicity
        // for production, you should list only allowed domains here
        var corsHandler = CorsHandler.create().addOrigin("*");

        // This policy allows images, scripts, AJAX, and CSS from the same origin
        // and does not allow any other resources to load (e.g., object, frame, media, etc.).
        var cspHandler = CSPHandler.create()
            .addDirective("default-src", "none")
            .addDirective("script-src", "self")
            .addDirective("connect-src", "self")
            .addDirective("style-src", "self")
            .addDirective("img-src", "self");

        Router router = Router.router(vertx);

        // turn on cors and csp headers for all requests
        router.route()
            .handler(corsHandler)
            .handler(cspHandler);

        // serve frontend
        router.route("/*").handler(StaticHandler.create("webroot"));

        router.get("/api/convert/:baseCurrency/:quoteCurrency")
            .handler(buildConvertValidationHandler())
            .handler(this::convertRequestHandler)
            .failureHandler(this::errorHandler);

        currencyConverterService.init(vertx).andThen(initResult -> {
            if (initResult.failed()) {
                startPromise.fail(initResult.cause());
                return;
            }

            int port = getPort();
            HttpServer server = vertx.createHttpServer();
            server.requestHandler(router).listen(port).onComplete(srv -> {
                log.info(String.format("Server running on port %d", port));
                startPromise.complete();
            });
        });
    }

    private ValidationHandler buildConvertValidationHandler() {
        SchemaRouter schemaRouter = SchemaRouter.create(vertx, new SchemaRouterOptions());
        SchemaParser parser = SchemaParser.createDraft7SchemaParser(schemaRouter);
        return ValidationHandlerBuilder.create(parser)
            .pathParameter(Parameters.param("baseCurrency", stringSchema()))
            .pathParameter(Parameters.param("quoteCurrency", stringSchema()))
            .queryParameter(Parameters.param("amount", numberSchema().with(minimum(0))))
            .build();
    }

    private void convertRequestHandler(RoutingContext ctx) {
        // parameters are guaranteed to be valid since validation handler ensures it
        String baseCurrency = ctx.pathParam("baseCurrency");
        String quoteCurrency = ctx.pathParam("quoteCurrency");
        BigDecimal baseAmount = new BigDecimal(ctx.queryParam("amount").get(0));

        var quoteAmount = currencyConverterService.convert(baseCurrency, quoteCurrency, baseAmount);
        if (quoteAmount == null) {
            ctx.fail(400, new RuntimeException("EXCHANGE_RATE_NOT_FOUNT"));
            return;
        }

        ConvertResponse res = new ConvertResponse(baseCurrency, quoteCurrency, baseAmount, quoteAmount);
        ctx.json(res);
    }

    private void errorHandler(RoutingContext ctx) {
        ctx.response()
            .setStatusCode(ctx.statusCode())
            .putHeader("content-type", "application/json")
            .end(new JsonObject()
                .put("error", ctx.statusCode() == 400 ? "Bad Request" : "Unexpected Error")
                .put("code", ctx.failure().getMessage())
                .toBuffer()
            );
    }

    public static int getPort() {
        int port;
        try {
            port = Integer.parseInt(System.getenv("PORT"));
        } catch (RuntimeException e) {
            port = 8888;
        }
        return port;
    }
}
