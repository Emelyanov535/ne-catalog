import axios from "axios";
import {localStorageService} from "./LocalStorageService";

const axiosInstance = axios.create({
	baseURL: import.meta.env.VITE_BACKEND_API_URL,
	withCredentials: false
});

axiosInstance.interceptors.request.use(
	(config) => {
		const accessToken = localStorageService.getAccessToken();
		if (accessToken) {
			config.headers.Authorization = `Bearer ${accessToken}`;
			config.withCredentials = false
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

		if (error.response?.status === 403 && !originalRequest._retry) {
			originalRequest._retry = true;

			try {
				const refreshToken = localStorageService.getRefreshToken();

				if (!refreshToken) {
					throw new Error("No refresh token available");
				}

				const response = await axiosInstance.post(`/auth/refresh`, {
					refreshToken: refreshToken,
				});

				localStorageService.setTokenToStorage({
					access_token: response.data.accessToken,
					refresh_token: response.data.refreshToken
				});

				originalRequest.headers["Authorization"] = `Bearer ${response.data.accessToken}`;

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
