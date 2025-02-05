package com.mikhaile.nostobackend;

import io.vertx.core.Future;

public class CurrencyRateProviderMock implements CurrencyRateProvider {
    private final CurrencyRate[] fakeRates;

    public CurrencyRateProviderMock(CurrencyRate[] fakeRates) {
        this.fakeRates = fakeRates;
    }

    @Override
    public Future<CurrencyRate[]> getAllRates() {
        return Future.succeededFuture(fakeRates);
    }
}
