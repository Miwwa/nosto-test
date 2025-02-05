const API_BASE_URL: string = import.meta.env.DEV ? "http://localhost:8888" : "";

export type ConvertData = {
    baseCurrency: string;
    quoteCurrency: string;
    baseAmount: number;
    quoteAmount: number;
}

export async function convert(baseCurrency: string, quoteCurrency: string, baseAmount: number): Promise<ConvertData> {
    const url = `${API_BASE_URL}/api/convert/${baseCurrency}/${quoteCurrency}?amount=${baseAmount}`

    const res = await fetch(url)
    if (!res.ok) {
        throw new Error(`Failed to convert`)
    }

    return res.json()
}
