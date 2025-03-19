import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { localStorageService } from "../services/LocalStorageService";
import { authService } from "../services/AuthService";

const Navbar: React.FC = () => {
	const navigate = useNavigate();
	const isAuthenticated = Boolean(localStorageService.getAccessToken());

	const handleLogout = () => {
		authService.logout();
		navigate("/signIn"); // Перенаправление после выхода
	};

	return (
		<nav className="bg-gray-800 p-4 shadow-md">
			<div className="max-w-screen-xl mx-auto flex justify-between items-center">
				{/* Логотип */}
				<Link to="/" className="text-white text-2xl font-semibold">
					NE-CATALOG
				</Link>

				{/* Навигационное меню */}
				<ul className="flex space-x-6 text-lg">
					<li>
						<Link
							to="/"
							className="text-white hover:text-gray-400 transition-colors duration-300"
						>
							Home
						</Link>
					</li>

					{isAuthenticated ? (
						<li>
							<button
								onClick={handleLogout}
								className="text-white bg-red-500 px-4 py-2 rounded hover:bg-red-600 transition-colors duration-300"
							>
								Logout
							</button>
						</li>
					) : (
						<>
							<li>
								<Link
									to="/signUp"
									className="text-white hover:text-gray-400 transition-colors duration-300"
								>
									Sign Up
								</Link>
							</li>
							<li>
								<Link
									to="/signIn"
									className="text-white hover:text-gray-400 transition-colors duration-300"
								>
									Sign In
								</Link>
							</li>
						</>
					)}
				</ul>
			</div>
		</nav>
	);
};

export default Navbar;
