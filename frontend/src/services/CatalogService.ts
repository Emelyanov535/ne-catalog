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

    async getProductById(id: bigint) {
        try {
            const response = await axiosInstance.get(`/catalog/${id}`);
            console.log("asdfsdfsdf", response)
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