import axiosInstance from "@/services/AxiosInstance.ts";
import {toast} from "sonner";
import qs from 'qs';

class SearchService {
    async getFilters(category: string): Promise<Filters> {
        try {
            const response = await axiosInstance.get(`/search/${category}/filters`);

            if (response.status !== 200) {
                toast.error("Ошибка при загрузке данных");
            }

            return response.data;
        } catch (error) {
            toast.error("Ошибка:" + error);
            throw error;
        }
    }

    async search(category: string,
                 searchQuery: string,
                 page: number,
                 size: number,
                 filters: Record<string, string[]>,
                 sortDir: string,
                 startPrice: number,
                 endPrice: number): Promise<SearchResults> {
        try {
            const response = await axiosInstance.get(`/search/${category}`, {
                params: {
                    category: category,
                    searchQuery: searchQuery,
                    page: page,
                    size: size,
                    sortBy: sortDir ? "price" : undefined,
                    sortDir: sortDir,
                    startPrice: startPrice,
                    endPrice: endPrice,
                    ...filters
                },
                paramsSerializer: params => qs.stringify(params, { arrayFormat: 'repeat' })})
            console.log(sortDir)
            if (response.status !== 200) {
                toast.error("Ошибка при загрузке данных");
            }
            console.log(response.data)
            return response.data;
        } catch (error) {
            toast.error("Ошибка:" + error);
            throw error;
        }
    }
}

export const searchService = new SearchService();