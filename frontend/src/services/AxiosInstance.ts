import axios from "axios";
import { localStorageService } from "./LocalStorageService";

const axiosInstance = axios.create({
	baseURL: import.meta.env.VITE_BACKEND_API_URL,
});

axiosInstance.interceptors.request.use(
	(config) => {
		const accessToken = localStorageService.getAccessToken();
		if (accessToken) {
			config.headers["Authorization"] = `Bearer ${accessToken}`;
		}
		return config;
	},
	(error) => {
		return Promise.reject(error);
	},
);

axiosInstance.interceptors.response.use(
	(response) => {
		return response;
	},
	async (error) => {
		const originalRequest = error.config;

		// Если ошибка 401 и не делаем повторный запрос для токена
		if (error.response?.status === 401 && !originalRequest._retry) {
			originalRequest._retry = true;

			try {
				const refreshToken = localStorageService.getRefreshToken();

				if (!refreshToken) {
					throw new Error("No refresh token available");
				}

				const response = await axiosInstance.post(`/refresh`, {
					refresh_token: refreshToken,
				});

				const { access_token, refresh_token } = response.data;

				localStorageService.setTokenToStorage({ access_token, refresh_token });

				originalRequest.headers["Authorization"] = `Bearer ${access_token}`;

				return axiosInstance(originalRequest);
			} catch (refreshError) {
				console.error("Error refreshing token:", refreshError);
				localStorageService.removeTokenFromStorage();
				throw new Error("Session expired");
			}
		}

		return Promise.reject(error);
	},
);

export default axiosInstance;
