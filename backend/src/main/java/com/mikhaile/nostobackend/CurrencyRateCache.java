package com.mikhaile.nostobackend;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides in-memory caching for currency exchange rates.
 * For production use cases, it should use shared redis-like storage instead
 * to cache exchange rates for all instances of our application
 */
public class CurrencyRateCache {
    private ConcurrentHashMap<CurrencyPair, Float> rateCache = new ConcurrentHashMap<>();
    private Instant expiresAt = Instant.MIN;

    private record CurrencyPair(String baseCurrency, String quoteCurrency) {
    }

    public Float get(String baseCurrency, String quoteCurrency) {
        return rateCache.get(new CurrencyPair(baseCurrency, quoteCurrency));
    }

    public void setFromArray(CurrencyRate[] rates) {
        var newCache = new ConcurrentHashMap<CurrencyPair, Float>();
        for (var rate : rates) {
            newCache.put(new CurrencyPair(rate.baseCurrency(), rate.quoteCurrency()), rate.rate());
        }
        rateCache = newCache;

        // in production, it should check the time of the next update of the external API (swop.cx)
        // now, it uses 24h expire time for simplicity
        int CACHE_TTL_SECONDS = 24 * 3600;
        expiresAt = Instant.now().plusSeconds(CACHE_TTL_SECONDS);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
