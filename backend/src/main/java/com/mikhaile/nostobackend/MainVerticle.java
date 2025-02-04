package com.mikhaile.nostobackend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.json.schema.*;

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

        Router router = Router.router(vertx);
        router
            .get("/api/convert/:baseCurrency/:quoteCurrency")
            .handler(buildConvertValidationHandler())
            .handler(this::convertRequestHandler)
            .failureHandler(this::errorHandler);

        int port = getPort();
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router).listen(port).onComplete(srv -> {
            log.info(String.format("Server running on port %d", port));
            startPromise.complete();
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
        String baseCurrency = ctx.pathParam("baseCurrency");
        String quoteCurrency = ctx.pathParam("quoteCurrency");

        // `amount` is guaranteed to be valid since validation handler ensures it
        float baseAmount = Float.parseFloat(ctx.queryParam("amount").get(0));
        currencyConverterService.convert(baseCurrency, quoteCurrency, baseAmount)
            .compose(quoteAmount -> {
                ConvertResponse res = new ConvertResponse(baseCurrency, quoteCurrency, baseAmount, quoteAmount);
                return ctx.json(res);
            })
            .onFailure(e -> {
                ctx.fail(500, e);
            });
    }

    private void errorHandler(RoutingContext ctx) {
        ctx.response()
            .setStatusCode(ctx.statusCode())
            .end(new JsonObject()
                .put("error", ctx.statusCode() == 400 ? "Bad Request" : "Unexpected Error")
                .put("message", ctx.failure().getMessage())
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
