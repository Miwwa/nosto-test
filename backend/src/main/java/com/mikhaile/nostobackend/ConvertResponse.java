package com.mikhaile.nostobackend;

record ConvertResponse(
    String baseCurrency,
    String quoteCurrency,
    Float baseAmount,
    Float quoteAmount
) {
}
