import axiosInstance from "@/services/AxiosInstance.ts";
import {toast} from "sonner";
import qs from 'qs';

class AnalogService {
    async findAnalogs(url: string, choosedAnalogAttributes: string[]): Promise<AnalogDto[]> {
        try {
            const response = await axiosInstance.get(`/find-analog`,
                {
                    params: {
                        productUrl: url,
                        attributeGroups: choosedAnalogAttributes
                    },
                    paramsSerializer: params => qs.stringify(params, { arrayFormat: 'repeat' })})

            if (response.status !== 200) {
                toast.error("Ошибка при загрузке данных");
            }

            return response.data;
        } catch (error) {
            toast.error("Ошибка:" + error);
            throw error;
        }
    }

    async getAnalogAttributes(url: string) {
        try {
            const response = await axiosInstance.get(`/find-analog/attributes?productUrl=${url}`);

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

export const analogService = new AnalogService();