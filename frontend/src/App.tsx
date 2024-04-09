import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import Home from "./pages/Home";
import {Missing} from "./pages/misc.tsx";
import BikeSearch from "./pages/BikeSearch.tsx";

function App() {


    return (
        <Router>
            <Routes>
                <Route path={"/"} element={Home()}/>
                <Route path={"/search"} element={BikeSearch()} />
                <Route path={"*"} element={Missing()} />
            </Routes>
        </Router>
    )
}

export default App;