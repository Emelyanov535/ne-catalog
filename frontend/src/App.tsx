import React from "react";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import Home from "./pages/Home";
import SignUp from "./pages/SignUp";
import SignIn from "./pages/SignIn";
import Navbar from "./components/Navbar";
import PrivateRoute from "./components/PrivateRoute";
import {Toaster} from "@/components/ui/sonner"
import ProductDetail from "@/pages/ProductDetail.tsx";
import FavouritePage from "@/pages/FavouritePage.tsx";
import CatalogPage from "@/pages/CatalogPage.tsx";
import NotFound from "@/pages/NotFound.tsx";


const App: React.FC = () => {
    return (
        <Router>
            <Navbar/>
            <main>
                <Routes>
                    <Route path="/signUp" element={<SignUp/>}/>
                    <Route path="/signIn" element={<SignIn/>}/>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/product/:url" element={<ProductDetail/>}/>
                    <Route path="/catalog/:category" element={<CatalogPage/>}/>
                    {/* Защищенные маршруты */}
                    <Route element={<PrivateRoute/>}>
                        <Route path="/favorites" element={<FavouritePage/>}/>
                    </Route>
                    <Route path="*" element={<NotFound />} />
                </Routes>
                <Toaster/>
            </main>
        </Router>
    );
};

export default App;
