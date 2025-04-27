import axiosInstance from "@/services/AxiosInstance.ts";
import {toast} from "sonner";

class FavouriteService {
    async addToFavorites(productUrl: string) {
        try {
            await axiosInstance.get(`/favorite?productUrl=${productUrl}`);
            return true;
        } catch (error) {
            toast.error("Ошибка при добавлении в избранное: " + error);
            return false;
        }
    }

    async removeFromFavorites(productUrl: string) {
        try {
            await axiosInstance.delete(`/favorite?productUrl=${productUrl}`);
            return true;
        } catch (error) {
            toast.error("Ошибка при удалении из избранного: " + error);
            return false;
        }
    }

    async getFavoriteProducts() {
        try {
            const response = await axiosInstance.get(`/favorite/list`);
            return response.data;
        } catch (error) {
            console.error("Ошибка при получении списка избранного:", error);
            return [];
        }
    }

    async getFavoriteProductsWithPaging(page: number, size: number) {
        try {
            console.log(size)
            const response = await axiosInstance.get(`/favorite/paging?page=${page}&size=${size}`);
            return response.data;
        } catch (error) {
            console.error("Ошибка при получении списка избранного:", error);
            return [];
        }
    }
}

export const favouriteService = new FavouriteService();
