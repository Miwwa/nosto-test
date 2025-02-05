import {useTranslation} from "react-i18next";

const CURRENCIES = [
    "USD",
    "EUR",
    "GBP",
    "JPY",
    "AUD",
    "CAD",
    "CNY",
    "CHF",
    "INR",
    "NZD"
]

type CurrencySelectProps = {
    value: string;
    onChange: (value: string) => void;
}

export function CurrencySelect({value, onChange}: CurrencySelectProps) {
    const {t} = useTranslation();

    return (
        <select className="dropdown" value={value} onChange={e => onChange(e.target.value)}>
            {CURRENCIES.map(code => (
                <option key={code} value={code}>
                    {`${code} ${t(`currencies.${code}`)}`}
                </option>
            ))}
        </select>
    )
}
