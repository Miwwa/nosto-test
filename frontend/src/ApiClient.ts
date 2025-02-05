const API_BASE_URL: string = import.meta.env.DEV ? "http://localhost:8888" : "";

export type ConvertData = {
    baseCurrency: string;
    quoteCurrency: string;
    baseAmount: number;
    quoteAmount: number;
}

type ApiError = {
    error: string;
    code: string;
}

export async function convert(baseCurrency: string, quoteCurrency: string, baseAmount: number): Promise<ConvertData> {
    const url = `${API_BASE_URL}/api/convert/${baseCurrency}/${quoteCurrency}?amount=${baseAmount}`

    const res = await fetch(url)
    if (!res.ok) {
        const message = await res.json()
            .then(data => {
                const errorCode = (data as ApiError)?.code;
                if (!errorCode) {
                    return "Unexpected error";
                }
                return errorCode;
            })
            .catch(() => "Unexpected error")
        throw new Error(message)
    }

    return res.json()
}
