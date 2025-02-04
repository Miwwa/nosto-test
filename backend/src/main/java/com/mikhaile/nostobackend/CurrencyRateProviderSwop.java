package com.mikhaile.nostobackend;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.util.Arrays;
import java.util.Date;

public class CurrencyRateProviderSwop implements CurrencyRateProvider {
    private final WebClient webClient;
    private final String apiKey;

    public CurrencyRateProviderSwop(Vertx vertx, String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.create(
            vertx,
            new WebClientOptions().setDefaultHost("swop.cx")
        );
    }

    @Override
    public Future<CurrencyRate[]> getAllRates() {
        return webClient
            .get("/rest/rates")
            .addQueryParam("api-key", apiKey)
            .send()
            .compose(response -> {
                if (response.statusCode() == 200) {
                    SwopCurrencyRate[] rates = response.bodyAsJson(SwopCurrencyRate[].class);
                    var transformed = Arrays.stream(rates)
                        .map(SwopCurrencyRate::toCurrencyRate)
                        .toArray(CurrencyRate[]::new);
                    return Future.succeededFuture(transformed);
                }
                return Future.failedFuture("Failed to fetch exchange rates");
            });
    }

    /**
     * API's response JSON mapping
     */
    private record SwopCurrencyRate(String base_currency, String quote_currency, Float quote, Date date) {
        public CurrencyRate toCurrencyRate() {
            return new CurrencyRate(base_currency, quote_currency, quote);
        }
    }
}
