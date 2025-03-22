import { useState, useEffect, useCallback } from "react";
import { favouriteService } from "@/services/FavouriteService.ts";

export function useFavorites() {
    const [favorites, setFavorites] = useState<Set<number>>(new Set());

    // Загружаем избранное при монтировании
    useEffect(() => {
        loadFavorites();
    }, []);

    const loadFavorites = async () => {
        try {
            const products = await favouriteService.getFavoriteProducts();
            if (Array.isArray(products)) {
                setFavorites(new Set(products.map((p) => p.id)));
            }
        } catch (error) {
            console.error("Ошибка загрузки избранного:", error);
        }
    };

    // Переключение избранного
    const toggleFavorite = useCallback(async (productId: number) => {
        if (!productId) return;

        const isFav = favorites.has(productId);
        const success = isFav
            ? await favouriteService.removeFromFavorites(productId)
            : await favouriteService.addToFavorites(productId);

        if (success) {
            setFavorites((prev) => {
                const newFavs = new Set(prev);
                isFav ? newFavs.delete(productId) : newFavs.add(productId);
                return newFavs;
            });
        }
    }, [favorites]);

    return { favorites, toggleFavorite };
}