package com.mikhaile.nostobackend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.json.schema.*;

import java.util.concurrent.ConcurrentHashMap;

import static io.vertx.json.schema.common.dsl.Schemas.numberSchema;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;
import static io.vertx.json.schema.draft7.dsl.Keywords.minimum;

public class MainVerticle extends AbstractVerticle {

    ConcurrentHashMap<String, ConcurrentHashMap<String, Float>> cache = new ConcurrentHashMap<>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        WebClient httpClient = WebClient.create(vertx);

        router
            .get("/api/convert/:baseCurrency/:quoteCurrency")
            .handler(buildConvertValidationHandler())
            .handler(this::convertRequestHandler)
            .failureHandler(this::errorHandler);

        server.requestHandler(router).listen(8888);
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
        float amount = Float.parseFloat(ctx.queryParam("amount").get(0));

        ConvertResponse res = new ConvertResponse(baseCurrency, quoteCurrency, amount, amount * 1.2345f);
        ctx.json(res);
    }

    private void errorHandler(RoutingContext ctx) {
        ctx.json(new JsonObject()
            .put("error", ctx.statusCode() == 400 ? "Bad Request" : "Unexpected Error")
            .put("message", ctx.failure().getMessage())
        );
    }
}
