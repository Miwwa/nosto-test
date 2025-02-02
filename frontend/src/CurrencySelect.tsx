import {CURRENCIES} from "./Currencies.ts";

type CurrencySelectProps = {
    value: string;
    onChange: (value: string) => void;
}

export function CurrencySelect({value, onChange}: CurrencySelectProps) {
    return (
        <select className="dropdown" value={value} onChange={e => onChange(e.target.value)}>
            {CURRENCIES.map(currency => (
                <option key={currency.code} value={currency.code}>
                    {`${currency.code} ${currency.name}`}
                </option>
            ))}
        </select>
    )
}
