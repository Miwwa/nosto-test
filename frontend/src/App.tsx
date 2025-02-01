import './App.css'
import {useEffect, useState} from "react";

type Currency = {
    code: string;
    name: string;
}

const TEMP_CURRENCIES: Currency[] = [
    {code: "USD", name: "US Dollar"},
    {code: "EUR", name: "Euro"},
    {code: "GBP", name: "British Pound"},
]

function App() {
    const [currencies, setCurrencies] = useState<Currency[]>(TEMP_CURRENCIES)
    const [fromCurrency, setFromCurrency] = useState<string>("USD");
    const [toCurrency, setToCurrency] = useState<string>("EUR");
    const [exchangeRate, setExchangeRate] = useState<number>(1.1)
    const [amount, setAmount] = useState<number>(100);
    const [result, setResult] = useState<number>(0);

    useEffect(() => {
        setResult(amount * exchangeRate);
    }, [exchangeRate, amount])

    return (
        <div className="container">
            <h2>Currency Exchange</h2>
            <CurrencySelect
                currencies={currencies}
                value={fromCurrency}
                onChange={value => setFromCurrency(value)}
            />
            <CurrencySelect
                currencies={currencies}
                value={toCurrency}
                onChange={value => setToCurrency(value)}
            />
            <input
                type="number"
                className="input-field"
                placeholder="Enter amount"
                min="0"
                max={Number.MAX_SAFE_INTEGER}
                value={amount}
                onChange={e => setAmount(Number(e.target.value))}
            />
            <button className="button">Convert</button>
            <div className="result">
                {amount} {fromCurrency} = {result.toFixed(2)} {toCurrency}
            </div>
        </div>
    )
}

type CurrencySelectProps = {
    currencies: Currency[];
    value: string;
    onChange: (value: string) => void;
}

function CurrencySelect({currencies, value, onChange}: CurrencySelectProps) {
    return (
        <select className="dropdown" value={value} onChange={e => onChange(e.target.value)}>
            {currencies.map(currency => <option value={currency.code}>{`${currency.code} ${currency.name}`}</option>)}
        </select>
    )
}

export default App
