import './App.css'
import {useState} from "react";
import {CurrencySelect} from "./CurrencySelect.tsx";

type ConvertData = {
    baseCurrency: string;
    quoteCurrency: string;
    baseAmount: number;
    quoteAmount: number;
}

function App() {
    const [baseCurrency, setBaseCurrency] = useState<string>("USD");
    const [quoteCurrency, setQuoteCurrency] = useState<string>("EUR");
    const [baseAmount, setBaseAmount] = useState<number>(1);

    const [converted, setConverted] = useState<ConvertData | null>(null);

    function convert() {
        const quoteAmount = baseAmount * 1.123456789;
        setConverted({
            baseCurrency,
            quoteCurrency,
            baseAmount,
            quoteAmount,
        })
    }

    return (
        <div className="container">
            <h1 className="header">Currency Exchange</h1>
            <div>From</div>
            <CurrencySelect
                value={baseCurrency}
                onChange={value => setBaseCurrency(value)}
            />
            <div>To</div>
            <CurrencySelect
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
            <button className="button" onClick={convert}>
                Convert
            </button>
            {converted && (
                <div className="result">
                    {converted?.baseAmount.toFixed(2)} {converted?.baseCurrency} = {converted?.quoteAmount.toFixed(2)} {converted?.quoteCurrency}
                </div>
            )}
        </div>
    )
}

export default App
