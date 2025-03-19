import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import SignUp from "./pages/SignUp";
import SignIn from "./pages/SignIn";
import Navbar from "./components/Navbar";
import PrivateRoute from "./components/PrivateRoute";

const App: React.FC = () => {
	return (
		<Router>
			<Navbar />
			<main>
				<Routes>
					<Route path="/signUp" element={<SignUp />} />
					<Route path="/signIn" element={<SignIn />} />

					{/* Защищенные маршруты */}
					<Route element={<PrivateRoute />}>
						<Route path="/" element={<Home />} />
					</Route>
				</Routes>
			</main>
		</Router>
	);
};

export default App;
