import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {searchService} from "@/services/SearchService.ts"
import {LaptopFiltersTranslations} from "@/types/LaptopFiltersTranslations.ts";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {ItemCard} from "@/components/ItemCard.tsx";
import {Slider} from "@/components/ui/slider.tsx";
import {ScrollArea, ScrollBar} from "@/components/ui/scroll-area.tsx";
import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@/components/ui/accordion.tsx";
import {Checkbox} from "@/components/ui/checkbox.tsx";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectLabel,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select.tsx";
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink, PaginationNext,
    PaginationPrevious
} from "@/components/ui/pagination.tsx";

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

    useEffect(() => {
        fetchFilters()
    }, []);

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
            searchQuery, currentPage, 20, choosedFilters, sortDir!, choosedStartPrice, choosedEndPrice)
        setProducts(response.results)
        setMaxPage(response.maxPage)
    }

    const getPaginationRange = () => {
        const range = [];
        let start = Math.max(currentPage - 2, 1);
        let end = Math.min(currentPage + 2, maxPage);

        if (start === 1) {
            end = Math.min(5, maxPage);
        }

        if (end === maxPage) {
            start = Math.max(maxPage - 4, 1);
        }

        for (let i = start; i <= end; i++) {
            range.push(i);
        }

        return range;
    };

    return (
        <div className={"max-w-2/3 mx-auto"}>
            <div className={"mx-auto my-2 max-w-1/3 flex justify-around"}>
                <Input className={""} value={searchQuery} onChange={handleSearchQueryChange} />
                <Button className={"mx-1"} onClick={() => {setCurrentPage(0); search()}}>Искать</Button>
            </div>
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
                                                    <SelectValue placeholder="..." />
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
                                            <Input className={"border-none shadow-none m-3 text-center"} value={choosedStartPrice ?? categoryFilters?.priceStart} onChange={handleChoosedStartPriceInputChange}/>
                                            <Input className={"border-none shadow-none m-3 text-center"} value={choosedEndPrice ?? categoryFilters?.priceEnd} onChange={handleChoosedEndPriceInputChange}/>
                                        </div>
                                        <Slider value={[choosedStartPrice ?? categoryFilters.priceStart, choosedEndPrice ?? categoryFilters.priceEnd]}
                                                onValueChange={handleChoosedPriceChange}
                                                min={categoryFilters?.priceStart}
                                                max={categoryFilters?.priceEnd}  />
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
                                                    }} />
                                                    <p className={"ml-2"}>{value}</p>
                                                </div>
                                            ))}
                                        </div>
                                    </ScrollArea>
                                </AccordionContent>
                            </AccordionItem>
                        ))}
                    </Accordion>
                </div>
                <div className={"w-full flex flex-col"}>
                    <div className="w-full grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mx-10">
                        {products?.map((product) => (
                            <ItemCard
                                key={product.url}
                                product={product}
                                isFavorite={false} //TODO доделать
                                onToggleFavorite={() => ""} //TODO доделать
                            />
                        ))}
                    </div>
                    {maxPage && <Pagination className="my-10">
                        <PaginationContent>
                            <PaginationItem>
                                <PaginationPrevious
                                    href="#"
                                    onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                                />
                            </PaginationItem>

                            {getPaginationRange().map((pageNum) => (
                                <PaginationItem key={pageNum}>
                                    <PaginationLink
                                        href="#"
                                        isActive={currentPage + 1 === pageNum}
                                        onClick={() => {setCurrentPage(pageNum - 1); search()}}
                                    >
                                        {pageNum}
                                    </PaginationLink>
                                </PaginationItem>
                            ))}

                            {maxPage > 5 && currentPage < maxPage - 2 && <PaginationItem><PaginationEllipsis/></PaginationItem>}

                            <PaginationItem>
                                <PaginationNext
                                    href="#"
                                    onClick={() => setCurrentPage((prev) => Math.min(prev + 1, maxPage))}
                                />
                            </PaginationItem>
                        </PaginationContent>
                    </Pagination>}
                </div>
            </div>
        </div>
    )
}

export default CatalogPage