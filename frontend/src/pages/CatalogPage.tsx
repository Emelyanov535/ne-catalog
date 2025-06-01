import React, {useEffect, useState} from "react";
import {useParams, useSearchParams} from "react-router-dom";
import {searchService} from "@/services/SearchService.ts"
import {LaptopFiltersTranslations} from "@/types/LaptopFiltersTranslations.ts";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {ItemCard} from "@/components/ItemCard.tsx";
import {Slider} from "@/components/ui/slider.tsx";
import {ScrollArea} from "@/components/ui/scroll-area.tsx";
import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion.tsx";
import {Checkbox} from "@/components/ui/checkbox.tsx";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {useFavorites} from "@/services/useFavorites.ts";
import {PaginationControls} from "@/components/PaginationControls.tsx";

const CatalogPage: React.FC = () => {
    const {category} = useParams<string>();
    const [categoryFilters, setCategoryFilters] = useState<Filters>();
    const [choosedStartPrice, setChoosedStartPrice] = useState<number>();
    const [choosedEndPrice, setChoosedEndPrice] = useState<number>();
    const [choosedFilters, setChoosedFilters] = useState<Record<string, string[]>>({});
    const [openFilters, setOpenFilters] = useState<Record<string, boolean>>({});
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [products, setProducts] = useState<ProductDto[]>();
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [maxPage, setMaxPage] = useState<number>();
    const [sortDir, setSortDir] = useState<string>();
    const {favorites, toggleFavorite} = useFavorites();
    const [searchParams, setSearchParams] = useSearchParams();
    const page = parseInt(searchParams.get("page") || "1", 10);

    useEffect(() => {
        fetchFilters();
    }, [category]);

    useEffect(() => {
        setCurrentPage(page - 1);
    }, [page]);

    useEffect(() => {
        search();
    }, [category, searchQuery, currentPage, choosedFilters, sortDir, choosedStartPrice, choosedEndPrice]);


    const fetchFilters = async () => {
        setCategoryFilters(await searchService.getFilters(category || ""))
    }

    const toggleFilterVisibility = (name: string) => {
        setOpenFilters((prev) => ({
            ...prev,
            [name]: !prev[name],
        }));
    };

    const handleSearchQueryChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (Number.isNaN(event.target.value)) return
        setSearchQuery(event.target.value);
    };

    const handleChoosedPriceChange = (value: number[]) => {
        setChoosedStartPrice(value[0]);
        setChoosedEndPrice(value[1]);
    };

    const handleChoosedStartPriceInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setChoosedStartPrice(Number.parseInt(event.target.value));
    };

    const handleChoosedEndPriceInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setChoosedEndPrice(Number.parseInt(event.target.value));
    };

    const search = async () => {
        const response = await searchService.search(category ?? '',
            searchQuery, page - 1, 20, choosedFilters, sortDir!, choosedStartPrice, choosedEndPrice)
        setProducts(response.results)
        setMaxPage(response.maxPage)
    }

    const handlePageChange = (newPage: number) => {
        setSearchParams({ page: String(newPage) });
    };

    return (
        <div className={"max-w-2/3 mx-auto"}>
            {/*<div className={"mx-auto my-2 max-w-1/3 flex justify-around"}>*/}
            {/*    <Input className={""} value={searchQuery} onChange={handleSearchQueryChange} />*/}
            {/*    */}
            {/*</div>*/}
            <div className={"flex"}>
                <div className={"w-1/3"}>
                    <Accordion type={"multiple"} className="">
                        <AccordionItem value={"price"}>
                            {categoryFilters && (
                                <>
                                    <AccordionTrigger>Цена</AccordionTrigger>
                                    <AccordionContent>
                                        <div className={"flex justify-between items-center"}>
                                            <p>
                                                Сортировать по:
                                            </p>
                                            <Select value={sortDir} onValueChange={setSortDir}>
                                                <SelectTrigger className="w-[180px]">
                                                    <SelectValue placeholder="..."/>
                                                </SelectTrigger>
                                                <SelectContent>
                                                    <SelectGroup>
                                                        <SelectItem value="asc">Возрастанию</SelectItem>
                                                        <SelectItem value="desc">Убыванию</SelectItem>
                                                    </SelectGroup>
                                                </SelectContent>
                                            </Select>
                                        </div>
                                        <div className={"flex items-center justify-between"}>
                                            <Input className={"border-none shadow-none m-3 text-center"}
                                                   value={choosedStartPrice ?? categoryFilters?.priceStart}
                                                   onChange={handleChoosedStartPriceInputChange}/>
                                            <Input className={"border-none shadow-none m-3 text-center"}
                                                   value={choosedEndPrice ?? categoryFilters?.priceEnd}
                                                   onChange={handleChoosedEndPriceInputChange}/>
                                        </div>
                                        <Slider
                                            value={[choosedStartPrice ?? categoryFilters.priceStart, choosedEndPrice ?? categoryFilters.priceEnd]}
                                            onValueChange={handleChoosedPriceChange}
                                            min={categoryFilters?.priceStart}
                                            max={categoryFilters?.priceEnd}/>
                                    </AccordionContent>
                                </>
                            )}
                        </AccordionItem>
                        {categoryFilters && Object.entries(categoryFilters.filters).map(([name, values]) => (
                            <AccordionItem className={""} key={name} value={name}>
                                <AccordionTrigger>
                                    {LaptopFiltersTranslations[name]}
                                </AccordionTrigger>
                                <AccordionContent>
                                    <ScrollArea className={" whitespace-nowrap border rounded-md"}>
                                        <div className={"m-1 max-h-50 flex flex-col w-max space-x-4"}>
                                            {values.map((value) => (
                                                <div key={value} className={"flex"}>
                                                    <Checkbox onCheckedChange={(checked) => {
                                                        if (checked) {
                                                            if (choosedFilters[name]) {
                                                                choosedFilters[name].push(value)
                                                            } else {
                                                                choosedFilters[name] = [value]
                                                            }
                                                        } else {
                                                            choosedFilters[name] = choosedFilters[name].filter((v) => v !== value)
                                                        }
                                                        console.log(choosedFilters)
                                                    }}/>
                                                    <p className={"ml-2"}>{value}</p>
                                                </div>
                                            ))}
                                        </div>
                                    </ScrollArea>
                                </AccordionContent>
                            </AccordionItem>
                        ))}
                    </Accordion>
                    <Button className={"mx-1"} onClick={() => {
                        setCurrentPage(0);
                        search()
                    }}>Искать</Button>
                </div>
                <div className={"w-full flex flex-col"}>
                    <div className="w-full grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mx-10">
                        {products?.map((product) => (
                            <ItemCard
                                key={product.url}
                                product={product}
                                isFavorite={favorites.has(product.url)}
                                onToggleFavorite={toggleFavorite}
                            />
                        ))}
                    </div>
                    <PaginationControls
                        currentPage={page}
                        maxPage={maxPage ?? 1}
                        onPageChange={handlePageChange}
                    />
                </div>
            </div>
        </div>
    )
}

export default CatalogPage