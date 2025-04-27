import {useCallback, useEffect, useState} from "react";
import {favouriteService} from "@/services/FavouriteService.ts";

export function useFavorites() {
    const [favorites, setFavorites] = useState<Set<string>>(new Set());

    // Загружаем избранные товары при монтировании
    useEffect(() => {
        loadFavorites();
    }, []);

    const loadFavorites = async () => {
        try {
            const products = await favouriteService.getFavoriteProducts();
            if (Array.isArray(products)) {
                setFavorites(new Set(products.map((p) => p.url))); // Храним productUrl
            }
        } catch (error) {
            console.error("Ошибка загрузки избранного:", error);
        }
    };

    // Переключение избранного
    const toggleFavorite = useCallback(async (productUrl: string) => {
        if (!productUrl) return;

        const isFav = favorites.has(productUrl);
        const success = isFav
            ? await favouriteService.removeFromFavorites(productUrl)
            : await favouriteService.addToFavorites(productUrl);

        if (success) {
            setFavorites((prev) => {
                const newFavs = new Set(prev);
                isFav ? newFavs.delete(productUrl) : newFavs.add(productUrl);
                return newFavs;
            });
        }
    }, [favorites]);

    return { favorites, toggleFavorite };
}
