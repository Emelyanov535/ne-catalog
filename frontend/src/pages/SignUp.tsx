import React, { useState } from "react";
import { authService } from "../services/AuthService";
import { useNavigate } from "react-router-dom";

const SignUp: React.FC = () => {
	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");
	const navigate = useNavigate(); // Хук для редиректа

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		const dto: SignUpDto = { username, password };
		try {
			await authService.registration(dto);
			alert("Registration successful!");
			navigate("/"); // Редирект на главную страницу
		} catch (error) {
			alert("Registration failed!");
		}
	};

	return (
		<div className="max-w-sm mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
			<h1 className="text-2xl font-semibold mb-4 text-center">Sign Up</h1>
			<form onSubmit={handleSubmit}>
				<div className="mb-4">
					<label htmlFor="email" className="block text-sm font-medium text-gray-700">
						Email
					</label>
					<input
						id="email"
						type="email"
						value={username}
						onChange={(e) => setUsername(e.target.value)}
						required
						className="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
					/>
				</div>
				<div className="mb-4">
					<label htmlFor="password" className="block text-sm font-medium text-gray-700">
						Password
					</label>
					<input
						id="password"
						type="password"
						value={password}
						onChange={(e) => setPassword(e.target.value)}
						required
						className="mt-1 p-2 w-full border rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
					/>
				</div>
				<button
					type="submit"
					className="w-full bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600 transition"
				>
					Sign Up
				</button>
			</form>
		</div>
	);
};

export default SignUp;
