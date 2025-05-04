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
import {Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious} from "@/components/ui/carousel.tsx";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";

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

    const firstPart = items.slice(0, 8);
    const secondPart = items.slice(8);

    return (
        <div className="flex flex-col items-center">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 w-full max-w-6xl p-10">
                {firstPart.map((item) => (
                    <ItemCard
                        key={item.url}
                        product={item}
                        isFavorite={favorites.has(item.url)}
                        onToggleFavorite={toggleFavorite}
                    />
                ))}

                <Card className="w-full max-w-6xl col-span-full border-yellow-400">
                    <CardHeader>
                        <CardTitle className="text-2xl font-bold">Best deals</CardTitle>
                        <CardDescription className="text-lg text-muted-foreground">
                            The best deals for the week
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="px-20">
                        <Carousel opts={{align: "start"}}>
                            <CarouselContent>
                                {items.map((item) => (
                                    <CarouselItem
                                        key={item.url}
                                        className="basis-1/1 sm:basis-1/2 md:basis-1/3"
                                    >
                                        <div>
                                            <ItemCard
                                                product={item}
                                                isFavorite={favorites.has(item.url)}
                                                onToggleFavorite={toggleFavorite}
                                            />
                                        </div>
                                    </CarouselItem>
                                ))}
                            </CarouselContent>
                            <CarouselPrevious/>
                            <CarouselNext/>
                        </Carousel>
                    </CardContent>
                </Card>

                {secondPart.map((item) => (
                    <ItemCard
                        key={item.url}
                        product={item}
                        isFavorite={favorites.has(item.url)}
                        onToggleFavorite={toggleFavorite}
                    />
                ))}
            </div>

            <Pagination className="pb-10">
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