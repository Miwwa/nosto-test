import './App.css'

function App() {
    return (
        <div className="container">
            <h2>Currency Exchange</h2>
            <select id="fromCurrency" className="dropdown">
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
                <option value="GBP">GBP</option>
            </select>
            <select id="toCurrency" className="dropdown">
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
                <option value="GBP">GBP</option>
            </select>
            <input type="number" id="amount" className="input-field" placeholder="Enter amount"/>
            <button className="button">Convert</button>
            <div id="result" className="result"></div>
        </div>
    )
}

export default App
