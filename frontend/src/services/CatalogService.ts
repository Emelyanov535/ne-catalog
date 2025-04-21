import axiosInstance from "@/services/AxiosInstance.ts";
import {toast} from "sonner";

class CatalogService {
    async getProductList(page: number, size: number) {
        try {
            const response = await axiosInstance.get(`/catalog?page=${page}&size=${size}`);

            if (response.status !== 200) {
                toast.error("Ошибка при загрузке данных");
            }

            return response.data;
        } catch (error) {
            toast.error("Ошибка:" + error);
            throw error;
        }
    }

    async getProductByUrl(url: string) {
        try {
            const response = await axiosInstance.get(`/catalog/getByUrl?url=${url}`);
            if (response.status !== 200) {
                toast.error("Ошибка при загрузке данных");
            }

            return response.data;
        } catch (error) {
            toast.error("Ошибка:" + error);
            throw error;
        }
    }

    async getSimilarProductsByUrl(url: string) {
        try {
            const response = await axiosInstance.get(`/forecast/test?url=${url}`);
            if (response.status !== 200) {
                toast.error("Ошибка при загрузке данных");
            }

            return response.data;
        } catch (error) {
            toast.error("Ошибка:" + error);
            throw error;
        }
    }
}


export const catalogService = new CatalogService();