package com.mikhaile.nostobackend;

public record CurrencyRate(
    String baseCurrency,
    String quoteCurrency,
    Float rate) {
}
