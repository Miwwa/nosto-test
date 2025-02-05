import './App.css'
import {useState} from "react";
import {useTranslation} from "react-i18next";
import {CurrencySelect} from "./CurrencySelect.tsx";
import {convert, ConvertData} from "./ApiClient.ts";
import LanguageSelect from "./LanguageSelect.tsx";

function App() {
    const {t} = useTranslation();

    const [baseCurrency, setBaseCurrency] = useState<string>("EUR");
    const [quoteCurrency, setQuoteCurrency] = useState<string>("USD");
    const [baseAmount, setBaseAmount] = useState<number>(1);

    const [isLoading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [converted, setConverted] = useState<ConvertData | null>(null);

    async function submitConvert(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault()
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
        <form className="container" onSubmit={submitConvert}>
            <h1 className="header">
                {t("Currency Exchange")}
                <LanguageSelect className="language-select"/>
            </h1>
            <div>{t("From")}</div>
            <CurrencySelect
                value={baseCurrency}
                onChange={value => setBaseCurrency(value)}
            />
            <div>{t("To")}</div>
            <CurrencySelect
                value={quoteCurrency}
                onChange={value => setQuoteCurrency(value)}
            />
            <div>{t("Amount")}</div>
            <input
                type="number"
                className="input-field"
                placeholder={t("Enter amount")}
                min="0"
                max={Number.MAX_SAFE_INTEGER}
                value={baseAmount}
                onChange={e => setBaseAmount(Number(e.target.value))}
            />
            <button className="button" type="submit">
                {t("Convert")}
            </button>
            {converted && !error && (
                <div className="result">
                    {converted?.baseAmount.toFixed(2)} {converted?.baseCurrency} = {converted?.quoteAmount.toFixed(2)} {converted?.quoteCurrency}
                </div>
            )}
            {error && <div className="error">
                {t("Error")}:
                <br/>
                {t(error)}
            </div>}
        </form>
    )
}

export default App
