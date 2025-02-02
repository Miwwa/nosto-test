const BASE_URL = "/api"

export type ConvertData = {
    baseCurrency: string;
    quoteCurrency: string;
    baseAmount: number;
    quoteAmount: number;
}

export async function convert(baseCurrency: string, quoteCurrency: string, baseAmount: number): Promise<ConvertData> {
    const url = `${BASE_URL}/conversions/${baseCurrency}/${quoteCurrency}?amount=${baseAmount}`

    const res = await fetch(url)
    if (!res.ok) {
        throw new Error(`Failed to convert`)
    }

    return res.json()
}
