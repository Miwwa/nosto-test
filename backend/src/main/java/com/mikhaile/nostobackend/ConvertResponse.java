package com.mikhaile.nostobackend;

class ConvertResponse {
    public String baseCurrency;
    public String quoteCurrency;
    public Float baseAmount;
    public Float quoteAmount;

    public ConvertResponse(String baseCurrency, String quoteCurrency, Float baseAmount, Float quoteAmount) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.baseAmount = baseAmount;
        this.quoteAmount = quoteAmount;
    }
}
