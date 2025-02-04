package com.mikhaile.nostobackend;

import java.math.BigDecimal;

public record CurrencyRate(
    String baseCurrency,
    String quoteCurrency,
    BigDecimal rate) {
}
