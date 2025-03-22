class LocalStorageService {
	private accessName = "access";
	private refreshName = "refresh";

	setTokenToStorage(tokens: { access_token: string; refresh_token: string }) {
		try {
			localStorage.setItem(this.accessName, tokens.access_token);
			localStorage.setItem(this.refreshName, tokens.refresh_token);
		} catch (error) {
			console.error("Error setting tokens to storage:", error);
			throw error;
		}
	}

	getAccessToken() {
		return localStorage.getItem(this.accessName);
	}

	getRefreshToken() {
		return localStorage.getItem(this.refreshName);
	}

	removeTokenFromStorage() {
		localStorage.removeItem(this.accessName);
		localStorage.removeItem(this.refreshName);
	}
}

export const localStorageService = new LocalStorageService();
