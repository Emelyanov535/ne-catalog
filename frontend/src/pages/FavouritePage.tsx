import React, {useEffect, useState} from "react";
import {useFavorites} from "@/services/useFavorites.ts";
import {ItemCard} from "@/components/ItemCard.tsx";
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious
} from "@/components/ui/pagination.tsx";
import {favouriteService} from "@/services/FavouriteService.ts";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {Label} from "@/components/ui/label.tsx";
import { Button } from "@/components/ui/button";
import {Slider} from "@/components/ui/slider.tsx";
import {authService} from "@/services/AuthService.ts";

const FavouritePage: React.FC = () => {
    const [items, setItems] = useState<any[]>([]);
    const [totalItems, setTotalItems] = useState<number>(0);
    const [page, setPage] = useState(1);
    const itemsPerPage = 20;
    const {favorites, toggleFavorite} = useFavorites();
    const [sortOrder, setSortOrder] = useState<"ASC" | "DESC">("ASC");
    const [notificationThreshold, setNotificationThreshold] = useState<number | null>(null);
    const [isSaving, setIsSaving] = useState(false);

    const fetchUserData = async () => {
        try {
            const response = await authService.whoami();
            setNotificationThreshold(response.notificationPercent);
        } catch (error) {
            console.error("Ошибка при загрузке данных пользователя:", error);
        }
    };

    const handleSaveThreshold = async () => {
        setIsSaving(true);
        try {
            await authService.setNotificationThreshold(notificationThreshold);
        } catch (error) {
            console.error("Ошибка при сохранении порога уведомлений", error);
        } finally {
            setIsSaving(false);
        }
    };

    useEffect(() => {
        const fetchItems = async () => {
            try {
                const adjustedPage = page <= 0 ? 1 : page;
                const data = await favouriteService.getFavoriteProductsWithPaging(adjustedPage - 1, itemsPerPage, sortOrder);
                setItems(data.content || []);
                setTotalItems(data.page.totalElements || 0);
            } catch (error) {
                console.error("Ошибка при загрузке данных", error);
            }
        };
        fetchItems();
        fetchUserData();
    }, [page, sortOrder]);

    const totalPages = Math.ceil(totalItems / itemsPerPage);

    const getPaginationRange = () => {
        const range = [];
        let start = Math.max(page - 2, 1);
        let end = Math.min(page + 2, totalPages);

        if (start === 1) {
            end = Math.min(5, totalPages);
        }

        if (end === totalPages) {
            start = Math.max(totalPages - 4, 1);
        }

        for (let i = start; i <= end; i++) {
            range.push(i);
        }

        return range;
    };

    return (
        <div className="flex flex-col items-center p-6 md:p-10">
            <div className="w-full max-w-6xl flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 pb-4">
                <div className="w-full max-w">
                    <div className="flex items-center gap-3">
                        <Label htmlFor="notification-threshold" className="text-base whitespace-nowrap">
                            Уведомлять при скидке:
                        </Label>
                        <span className="font-medium text-lg px-3 py-1 bg-muted rounded-lg">
        {notificationThreshold}%
      </span>
                    </div>
                    <div className="flex items-center gap-4 mt-2">
                        <Slider
                            id="notification-threshold"
                            value={[notificationThreshold ?? 10]}
                            max={90}
                            min={5}
                            step={5}
                            onValueChange={(value) => setNotificationThreshold(value[0])}
                            className="w-[200px]"
                        />
                        <div className="flex items-center gap-2">
                            <Button
                                onClick={handleSaveThreshold}
                                disabled={isSaving}
                                size="sm"
                            >
                                {isSaving ? "Сохранение..." : "Сохранить"}
                            </Button>
                        </div>
                    </div>
                </div>

                <div className="w-[220px]">
                    <Label>Сортировка:</Label>
                    <Select
                        value={sortOrder}
                        onValueChange={(value: "ASC" | "DESC") => setSortOrder(value)}
                    >
                        <SelectTrigger className="w-full mt-2">
                            <SelectValue placeholder="Сортировка"/>
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="ASC">По возрастанию даты</SelectItem>
                            <SelectItem value="DESC">По убыванию даты</SelectItem>
                        </SelectContent>
                    </Select>
                </div>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 w-full max-w-6xl">
                {items.length > 0 ? (
                    items.map((item) => (
                        <ItemCard
                            key={item.url}
                            product={item}
                            isFavorite={favorites.has(item.url)}
                            onToggleFavorite={toggleFavorite}
                        />
                    ))
                ) : (
                    <p>Загрузка товаров...</p>
                )}
            </div>

            <Pagination className="p-10">
                <PaginationContent>
                    <PaginationItem>
                        <PaginationPrevious
                            href="#"
                            onClick={() => setPage((prev) => Math.max(prev - 1, 1))}
                        />
                    </PaginationItem>

                    {getPaginationRange().map((pageNum) => (
                        <PaginationItem key={pageNum}>
                            <PaginationLink
                                href="#"
                                isActive={page === pageNum}
                                onClick={() => setPage(pageNum)}
                            >
                                {pageNum}
                            </PaginationLink>
                        </PaginationItem>
                    ))}

                    {totalPages > 5 && page < totalPages - 2 && <PaginationItem><PaginationEllipsis/></PaginationItem>}

                    <PaginationItem>
                        <PaginationNext
                            href="#"
                            onClick={() => setPage((prev) => Math.min(prev + 1, totalPages))}
                        />
                    </PaginationItem>
                </PaginationContent>
            </Pagination>
        </div>
    );
}

export default FavouritePage;