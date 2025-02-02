import './App.css'
import {useState} from "react";
import {CurrencySelect} from "./CurrencySelect.tsx";
import {convert} from "./ApiClient.ts";

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

    const [isLoading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [converted, setConverted] = useState<ConvertData | null>(null);

    async function tryConvert() {
        if (isLoading) {
            return
        }

        setLoading(true)
        try {
            const data = await convert(baseCurrency, quoteCurrency, baseAmount)
            setConverted(data)
            setError(null)
        } catch (e) {
            if (e instanceof Error) {
                setError(e.message)
            } else {
                setError(String(e))
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="container">
            <h1 className="header">
                Currency Exchange
            </h1>
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
            <button className="button" onClick={tryConvert}>
                Convert
            </button>
            {converted && !error && (
                <div className="result">
                    {converted?.baseAmount.toFixed(2)} {converted?.baseCurrency} = {converted?.quoteAmount.toFixed(2)} {converted?.quoteCurrency}
                </div>
            )}
            {error && <div className="error">{error}</div>}
        </div>
    )
}

export default App
