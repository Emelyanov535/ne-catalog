import axiosInstance from "@/services/AxiosInstance.ts";
import {toast} from "sonner";
import {AnalysisDataDto} from "@/types/AnalysisDataDto.tsx";

class AnalysisService {
    async getAnalysis(url: string): Promise<AnalysisDataDto | null> {
        try {
            const response = await axiosInstance.get(`/analysis?url=${url}`);
            return response.data;
        } catch (error) {
            toast.error("Ошибка при получении анализа: " + error);
            return null;
        }
    }

    async getIdenticalProductStats(url: string) {
        try {
            const response = await axiosInstance.get(`/analysis/identProdStats?url=${url}`);
            return response.data;
        } catch (error) {
            toast.error("Ошибка при получении графика идентичных товаров: " + error);
            return null;
        }
    }
}

export const analysisService = new AnalysisService();