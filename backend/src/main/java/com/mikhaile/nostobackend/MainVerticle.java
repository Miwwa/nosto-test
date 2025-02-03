package com.mikhaile.nostobackend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.List;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router
            .get("/api/convert/:baseCurrency/:quoteCurrency")
            .handler(ctx -> {
                String baseCurrency = ctx.pathParam("baseCurrency");
                String quoteCurrency = ctx.pathParam("quoteCurrency");
                List<String> amountStr = ctx.queryParam("amount");
                ConvertResponse res = new ConvertResponse(baseCurrency, quoteCurrency, 1.0f, 1.2345f);
                ctx.json(res);
            });

        server.requestHandler(router).listen(8888);
    }
}
