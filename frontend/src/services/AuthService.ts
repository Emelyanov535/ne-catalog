import axiosInstance from "./AxiosInstance.ts";
import {localStorageService} from "@/services/LocalStorageService.ts";

class AuthService {
	async registration(dto: SignUpDto) {
		try {
			const response = await axiosInstance.post(`/account/register`, dto);
			return response.data;
		} catch (error) {
			console.error(
				"Error during registration:",
				error?.response.data || error.message,
			);
			throw error;
		}
	}

	async authorization(dto: SignInDto) {
		try {
			const response = await axiosInstance.post(`/auth/login`, dto);
			localStorageService.setTokenToStorage({
				access_token: response.data.accessToken,
				refresh_token: response.data.refreshToken
			});
			return response.data;
		} catch (error) {
			console.error(
				"Error during authorization:",
				error.response?.data || error.message,
			);
			throw error;
		}
	}

	async logout() {
		try {
			localStorageService.removeTokenFromStorage();
			window.location.href = "/signIn"; // Перенаправление на страницу входа
		} catch (error) {
			console.error("Error during logout:", error);
		}
	}
}

export const authService = new AuthService();
