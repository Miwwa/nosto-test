package com.mikhaile.nostobackend;

import java.math.BigDecimal;

record ConvertResponse(
    String baseCurrency,
    String quoteCurrency,
    BigDecimal baseAmount,
    BigDecimal quoteAmount
) {
}
