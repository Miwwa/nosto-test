import './App.css'
import {useState} from "react";

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
    const [currencies] = useState<Currency[]>(TEMP_CURRENCIES)

    const [baseCurrency, setBaseCurrency] = useState<string>("USD");
    const [quoteCurrency, setQuoteCurrency] = useState<string>("EUR");
    const [baseAmount, setBaseAmount] = useState<number>(100);
    const [resultString, setResultString] = useState<string>("");

    function convert() {
        const quoteAmount = baseAmount * 1.123456789;
        setResultString(`${baseAmount.toFixed(2)} ${baseCurrency} = ${quoteAmount.toFixed(2)} ${quoteCurrency}`)
    }

    return (
        <div className="container">
            <h1 className="header">Currency Exchange</h1>
            <div>From</div>
            <CurrencySelect
                currencies={currencies}
                value={baseCurrency}
                onChange={value => setBaseCurrency(value)}
            />
            <div>To</div>
            <CurrencySelect
                currencies={currencies}
                value={quoteCurrency}
                onChange={value => setQuoteCurrency(value)}
            />
            <div>Amount</div>
            <input
                type="number"
                className="input-field"
                placeholder="Enter amount"
                min="0"
                max={Number.MAX_SAFE_INTEGER}
                value={baseAmount}
                onChange={e => setBaseAmount(Number(e.target.value))}
            />
            <button className="button" onClick={convert}>Convert</button>
            <div className="result">
                {resultString}
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
            {currencies.map(currency => (
                <option key={currency.code} value={currency.code}>
                    {`${currency.code} ${currency.name}`}
                </option>
            ))}
        </select>
    )
}

export default App
