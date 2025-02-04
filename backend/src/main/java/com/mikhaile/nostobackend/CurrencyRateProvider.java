package com.mikhaile.nostobackend;

import io.vertx.core.Future;

public interface CurrencyRateProvider {
    Future<CurrencyRate[]> getAllRates();
}
