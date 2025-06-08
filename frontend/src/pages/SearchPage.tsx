import React, {useEffect, useState} from "react";
import {useLocation, useParams, useSearchParams} from "react-router-dom";
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
import {Separator} from "@/components/ui/separator.tsx";

const SearchPage: React.FC = () => {
    const {category} = useParams<{ category: string}>();
    const [choosedStartPrice, setChoosedStartPrice] = useState<number>();
    const [choosedEndPrice, setChoosedEndPrice] = useState<number>();
    const [categoryFilters, setCategoryFilters] = useState<Filters>({});
    const [choosedFilters, setChoosedFilters] = useState<Record<string, Record<string, string[]>>>({});
    const [openFilters, setOpenFilters] = useState<Record<string, Record<string, boolean>>>({});
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [products, setProducts] = useState<ProductDto[]>();
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [maxPage, setMaxPage] = useState<number>();
    const [sortDir, setSortDir] = useState<string>();
    const {favorites, toggleFavorite} = useFavorites();
    const [searchParams, setSearchParams] = useSearchParams();
    const page = parseInt(searchParams.get("page") || "1", 10);
    const location = useLocation();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const query = params.get('searchQuery');

        console.log(query)
        if (query !== searchQuery) {
            const performSearch = async () => {
                await fetchFilters();
                await search(query);
            };

            setSearchQuery(query || '');
            performSearch();
        }
    }, [location.search]);

    useEffect(() => {
        fetchFilters();
    }, [category]);

    useEffect(() => {
        setCurrentPage(page - 1);
    }, [page]);

    const fetchFilters = async () => {
        setCategoryFilters(await searchService.getFilters(category || ""))
    }

    const toggleFilterVisibility = (filterCategory: string, subFilterName: string) => {
        setOpenFilters(prev => ({
            ...prev,
            [filterCategory]: {
                ...prev[filterCategory],
                [subFilterName]: !prev[filterCategory]?.[subFilterName]
            }
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

    const search = async (sq: string) => {
        const result: Record<string, string[]> = {};

        Object.values(choosedFilters).forEach(subFilters => {
            Object.entries(subFilters).forEach(([subFilterName, values]) => {
                if (!result[subFilterName]) {
                    result[subFilterName] = [];
                }
                result[subFilterName].push(...values);
            });
        });
        const response = await searchService.search(category ?? '',
            sq || searchQuery, page - 1, 20, result, sortDir!, choosedStartPrice ?? categoryFilters.priceStart, choosedEndPrice ?? categoryFilters.priceEnd)
        setProducts(response.results)
        setMaxPage(response.maxPage)
    }

    const handlePageChange = (newPage: number) => {
        setSearchParams({ page: String(newPage) });
    };

    return (
        <div className={"max-w-2/3 mx-auto mt-4"}>
            {/*<div className={"mx-auto my-2 max-w-1/3 flex justify-around"}>*/}
            {/*    <Input className={""} value={searchQuery} onChange={handleSearchQueryChange} />*/}
            {/*    */}
            {/*</div>*/}
            <div className={"flex relative"}>
                <div className={"flex flex-col w-1/3"}>
                    <div className={"flex justify-between items-center p-2"}>
                        <p className={"text-sm"}>
                            Сортировка:
                        </p>
                        <Select value={sortDir} onValueChange={setSortDir}>
                            <SelectTrigger className="w-[200px]">
                                <SelectValue placeholder="..."/>
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    <SelectItem value="desc">Сначала дорогие</SelectItem>
                                    <SelectItem value="asc">Сначала недорогие</SelectItem>
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>
                    <Accordion type={"multiple"} className={"border rounded-xl shadow-sm p-2"}>
                        <AccordionItem key="price" value={"price"}>
                            {categoryFilters && (
                                <>
                                    <AccordionTrigger>Цена</AccordionTrigger>
                                    <AccordionContent>
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
                        {categoryFilters.filters && Object.entries(categoryFilters.filters).map(([filterCategory, subFilters]) =>
                            LaptopFiltersTranslations[filterCategory] && (
                            <AccordionItem key={filterCategory} value={filterCategory}>
                                <AccordionTrigger>{LaptopFiltersTranslations[filterCategory] /* или какой-то перевод */}</AccordionTrigger>
                                <AccordionContent>
                                    <Accordion type="multiple" className="border-x-2 px-2">
                                        {Object.entries(subFilters).map(([subFilterName, values]) => (
                                            <AccordionItem key={subFilterName} value={subFilterName}>
                                                <AccordionTrigger onClick={() => toggleFilterVisibility(filterCategory, subFilterName)}>
                                                    {LaptopFiltersTranslations[subFilterName]}
                                                </AccordionTrigger>
                                                {openFilters[filterCategory]?.[subFilterName] && (
                                                    <AccordionContent>
                                                        <ScrollArea className="whitespace-normal border rounded-md ">
                                                            <div className={"m-1 max-h-50 flex flex-col space-x-4"}>
                                                                {values.map(value => (
                                                                    <div key={value} className="flex items-center my-1">
                                                                        <Checkbox
                                                                            checked={choosedFilters[filterCategory]?.[subFilterName]?.includes(value) || false}
                                                                            onCheckedChange={checked => {
                                                                                setChoosedFilters(prev => {
                                                                                    const prevCategory = prev[filterCategory] || {};
                                                                                    const prevSubFilter = prevCategory[subFilterName] || [];

                                                                                    let updatedSubFilter: string[];
                                                                                    if (checked) {
                                                                                        updatedSubFilter = [...prevSubFilter, value];
                                                                                    } else {
                                                                                        updatedSubFilter = prevSubFilter.filter(v => v !== value);
                                                                                    }

                                                                                    return {
                                                                                        ...prev,
                                                                                        [filterCategory]: {
                                                                                            ...prevCategory,
                                                                                            [subFilterName]: updatedSubFilter,
                                                                                        },
                                                                                    };
                                                                                });
                                                                            }}
                                                                        />
                                                                        <p className="pl-1 break-words">{value}</p>
                                                                    </div>
                                                                ))}
                                                            </div>
                                                        </ScrollArea>
                                                    </AccordionContent>
                                                )}
                                            </AccordionItem>
                                        ))}
                                    </Accordion>
                                </AccordionContent>

                            </AccordionItem>
                        ))}

                    </Accordion>
                    <Button className={"mt-2"} onClick={() => {
                        setCurrentPage(0);
                        search(undefined)
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

export default SearchPage