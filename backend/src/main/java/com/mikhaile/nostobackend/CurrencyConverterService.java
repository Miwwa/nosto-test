package com.mikhaile.nostobackend;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.math.BigDecimal;

public class CurrencyConverterService {

    private final Logger log = LoggerFactory.getLogger(CurrencyConverterService.class);

    private final CurrencyRateProvider currencyRateProvider;
    private final CurrencyRateCache cache;

    public CurrencyConverterService(CurrencyRateProvider currencyRateProvider) {
        this.currencyRateProvider = currencyRateProvider;
        this.cache = new CurrencyRateCache();
    }

    public Future<Void> init(Vertx vertx) {
        // in production, it should check the time of the next update of the external API (swop.cx)
        // now, it updates cache every 24h for simplicity
        int updateRateMs = 24 * 3600 * 1000;
        vertx.setPeriodic(updateRateMs, t -> updateRatesCache());
        return updateRatesCache();
    }

    private Future<Void> updateRatesCache() {
        log.info("Updating exchange rates...");
        return currencyRateProvider.getAllRates()
            .compose(currencyRates -> {
                cache.setFromArray(currencyRates);
                log.info("Exchange rates updated");
                return Future.succeededFuture();
            });
    }

    public BigDecimal convert(String baseCurrency, String quoteCurrency, BigDecimal baseAmount) {
        BigDecimal cachedRate = cache.get(baseCurrency, quoteCurrency);
        if (cachedRate == null) {
            return null;
        }
        return baseAmount.multiply(cachedRate);
    }
}
