import axiosInstance from "@/services/AxiosInstance.ts";
import {toast} from "sonner";

class ForecastService {
    async getForecast(url: string) {
        try {
            console.log(url.url)
            const response = await axiosInstance.get(`/forecast?url=${url.url}`);
            console.log(response)
            return response.data;
        } catch (error) {
            toast.error("Ошибка при получении графика " + error);
            return false;
        }
    }
}

export const forecastService = new ForecastService();