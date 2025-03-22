import { HeartIcon } from "lucide-react";
import { useState } from "react";

interface FavoriteButtonProps {
    productId: number;
    isFavorite: boolean;
    onToggle: () => void;
    className?: string;
}

export function FavoriteButton({isFavorite, onToggle, className }: FavoriteButtonProps) {
    const [loading, setLoading] = useState(false);

    const handleToggleFavorite = async () => {
        if (loading) return;

        setLoading(true);
        try {
            onToggle();
        } catch (error) {
            console.error("Ошибка при изменении избранного:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <button
            onClick={handleToggleFavorite}
            className={`text-gray-500 hover:text-red-500 transition ${className}`}
            disabled={loading}
        >
            <HeartIcon className={`size-7 ${isFavorite ? "fill-red-500 stroke-red-500" : "stroke-gray-500"}`} />
        </button>
    );
}