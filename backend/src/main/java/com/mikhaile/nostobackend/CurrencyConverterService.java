package com.mikhaile.nostobackend;

import io.vertx.core.Future;

public class CurrencyConverterService {

    private final CurrencyRateProvider currencyRateProvider;
    private final CurrencyRateCache cache;

    public CurrencyConverterService(CurrencyRateProvider currencyRateProvider) {
        this.currencyRateProvider = currencyRateProvider;
        this.cache = new CurrencyRateCache();
    }

    public Future<Float> convert(String baseCurrency, String quoteCurrency, Float baseAmount) {
        // todo: make it thread safe
        if (cache.isExpired()) {
            return currencyRateProvider.getAllRates()
                .compose(currencyRates -> {
                    cache.setFromArray(currencyRates);
                    return getRateAndCalculate(baseCurrency, quoteCurrency, baseAmount);
                });
        }
        return getRateAndCalculate(baseCurrency, quoteCurrency, baseAmount);
    }

    private Future<Float> getRateAndCalculate(String baseCurrency, String quoteCurrency, Float baseAmount) {
        Float cachedRate = cache.get(baseCurrency, quoteCurrency);
        if (cachedRate == null) {
            return Future.failedFuture(String.format("Exchange rate is not available for currency pair %s -> %s", baseCurrency, quoteCurrency));
        }
        float quoteAmount = getQuoteAmount(baseAmount, cachedRate);
        return Future.succeededFuture(quoteAmount);
    }

    private float getQuoteAmount(float amount, float rate) {
        return amount * rate;
    }


}
