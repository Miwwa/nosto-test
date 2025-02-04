package com.mikhaile.nostobackend;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides in-memory caching for currency exchange rates.
 * For production use cases, you should use distributed redis-like storage instead
 * and update cache from only one instance of application
 */
public class CurrencyRateCache {
    private ConcurrentHashMap<CurrencyPair, BigDecimal> rateCache = new ConcurrentHashMap<>();

    private record CurrencyPair(String baseCurrency, String quoteCurrency) {
    }

    public BigDecimal get(String baseCurrency, String quoteCurrency) {
        return rateCache.get(new CurrencyPair(baseCurrency, quoteCurrency));
    }

    public void setFromArray(CurrencyRate[] rates) {
        var newCache = new ConcurrentHashMap<CurrencyPair, BigDecimal>();
        for (var rate : rates) {
            newCache.put(new CurrencyPair(rate.baseCurrency(), rate.quoteCurrency()), rate.rate());
        }
        rateCache = newCache;
    }
}
