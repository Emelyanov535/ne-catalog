import React, {useEffect, useState} from "react";
import {ItemCard} from "@/components/ItemCard.tsx";
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination";
import {catalogService} from "@/services/CatalogService.ts";
import {useFavorites} from "@/services/useFavorites.ts";

const Home: React.FC = () => {
    const [items, setItems] = useState<any[]>([]);
    const [totalItems, setTotalItems] = useState<number>(0);
    const [page, setPage] = useState(1);
    const itemsPerPage = 60;
    const {favorites, toggleFavorite} = useFavorites();

    useEffect(() => {
        const fetchItems = async () => {
            try {
                const adjustedPage = page <= 0 ? 1 : page;
                const data = await catalogService.getProductList(adjustedPage - 1, itemsPerPage);
                setItems(data.content || []);
                setTotalItems(data.page.totalElements || 0);
            } catch (error) {
                console.error("Ошибка при загрузке данных", error);
            }
        };
        fetchItems();
    }, [page]);

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
};

export default Home;