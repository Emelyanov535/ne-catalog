import {useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {analogService} from "@/services/AnalogService.ts"
import {catalogService} from "@/services/CatalogService.ts";
import {FavoriteButton} from "@/components/FavoriteButton.tsx";
import {useFavorites} from "@/services/useFavorites.ts";
import {Separator} from "@radix-ui/react-dropdown-menu";
import {Button} from "@/components/ui/button.tsx";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@radix-ui/react-tabs";
import {Accordion, AccordionContent, AccordionTrigger} from "@/components/ui/accordion.tsx";
import {AccordionItem} from "@radix-ui/react-accordion";
import {LaptopFiltersTranslations} from "@/types/LaptopFiltersTranslations.ts";
import {ProductAttributeGroupsTranslations} from "@/types/ProductAttributeGroupsTranslations.ts";
import {Checkbox} from "@/components/ui/checkbox.tsx";
import {ItemCard} from "@/components/ItemCard.tsx";

const ProductDetail: React.FC = () => {
    const {url} = useParams<{ url: string }>();
    const [product, setProduct] = useState<ProductDto | null>(null);
    const decodedUrl = decodeURIComponent(url as string);
    const {favorites, toggleFavorite} = useFavorites();
    const [analogs, setAnalogs] = useState<AnalogDto[]>([]);
    const [analogAttributes, setAnalogAttributes] = useState<Record<string, string[]>>();
    const [choosedAnalogAttributes, setChoosedAnalogAttributes] = useState<string[]>([]);
    const [isAnalogsFetched, setIsAnalogsFetched] = useState<boolean>(false);
    const [similarProducts, setSimilarProducts] = useState<ShopPriceDto[]>([]);

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const data = await catalogService.getProductByUrl(decodedUrl);
                setProduct(data);

                const similarData = await catalogService.getSimilarProductsByUrl(decodedUrl);
                setSimilarProducts(similarData);
            } catch (error) {
                console.error("Ошибка при загрузке данных", error);
            }
        };
        const fetchAnalogAttributes = async () => {
            try {
                const data = await analogService.getAnalogAttributes(decodedUrl);
                setAnalogAttributes(data);
            } catch (error) {
                console.error("Ошибка при загрузке данных", error);
            }
        };
        fetchProduct();
        fetchAnalogAttributes()
    }, [decodedUrl]);

    if (!product) return <p>Загрузка...</p>;

    const findAnalogs = () => {
        setIsAnalogsFetched(true)
        analogService.findAnalogs(decodedUrl, choosedAnalogAttributes)
            .then((response) => {
                setIsAnalogsFetched(false);
                setAnalogs(response)
            })
            .catch(() => setIsAnalogsFetched(false))
    }

    return (
        <div className="flex justify-center items-center p-4">
            <div className="w-full max-w-7xl p-6">
                {/* Верхний блок с названием и рейтингом */}
                <div className="flex justify-between items-center">
                    <div>
                        <p className="text-lg font-semibold">{product.productName}</p>
                        <p className="text-gray-500 text-sm">57 000 ₽ — 82 770 ₽</p>
                    </div>
                    <FavoriteButton
                        productUrl={product.url}
                        isFavorite={favorites.has(product.url)}
                        onToggle={() => toggleFavorite(product.url)}
                    />
                </div>
                <Separator className="my-4 border-t border-gray-300"></Separator>

                {/* Нижний блок с фото, характеристиками и ценами */}
                <div className="flex gap-6">
                    {/* Фото */}
                    <div className="w-1/4 flex justify-center">
                        <img src={product.imageUrl} alt={product.productName} className="w-90 h-90 object-contain"/>
                    </div>

                    {/* Характеристики */}
                    <div className="w-1/2 space-y-2">
                        <p className="text-lg font-semibold">О товаре</p>

                        <div className="grid gap-2">
                            {[
                                {label: "Экран", value: "6.1 дюйм, 2556x1179, OLED/POLED"},
                                {label: "Процессор", value: "Apple A16 Bionic, 6-ядерный"},
                                {label: "Оперативная память", value: "6 Гб"},
                                {label: "Встроенная память", value: "128 Гб"},
                                {label: "Емкость аккумулятора", value: "3349 мА*ч"},
                            ].map((item, index) => (
                                <div key={index} className="flex items-center">
                                    <span className="text-gray-500 whitespace-nowrap">{item.label}</span>
                                    <span className="flex-grow border-b border-dotted border-gray-400 mx-2"></span>
                                    <span className="whitespace-nowrap">{item.value}</span>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Цены в магазинах */}
                    <div className="w-1/3 space-y-3">
                        {[
                            ...similarProducts.filter(p => p.url === decodedUrl),
                            ...similarProducts.filter(p => p.url !== decodedUrl)
                        ].map((product, index) => {
                            const isCurrent = product.url === decodedUrl;

                            return (
                                <div
                                    key={index}
                                    className={`p-3 border rounded-lg shadow-sm bg-white ${
                                        isCurrent ? "border-blue-500 bg-blue-50 shadow-md" : ""
                                    }`}
                                >
                                    <p className="text-sm font-semibold">{product.marketplace}</p>
                                    <p className="text-lg font-bold">{product.price.toLocaleString("ru-RU")} ₽</p>
                                    <a href={product.url} target="_blank" rel="noopener noreferrer">
                                        <Button className="w-full p-5">Перейти на сайт</Button>
                                    </a>
                                </div>
                            );
                        })}
                    </div>
                </div>

                <Tabs defaultValue="about" className="w-[800px] pt-10">
                    <TabsList className="grid w-full grid-cols-5">
                        <TabsTrigger value="about">О товаре</TabsTrigger>
                        <TabsTrigger value="price">Цены</TabsTrigger>
                        <TabsTrigger value="analog">Аналоги</TabsTrigger>
                        <TabsTrigger value="attribute">Характеристики</TabsTrigger>
                        <TabsTrigger value="analyse">Анализ цен</TabsTrigger>
                    </TabsList>
                    <Separator className="my-4 border-t border-gray-300"></Separator>
                    <TabsContent value="about">

                    </TabsContent>
                    <TabsContent value="price">

                    </TabsContent>
                    <TabsContent value="analog">
                        <div className={"flex overflow-x-auto"}>
                            {analogAttributes && Object.entries(analogAttributes).map(([key, values]) => (
                                <Accordion key={key} type={"multiple"}>
                                    <AccordionItem className={"mx-2"} value={key}>
                                        <AccordionTrigger className={"flex h-15"}>
                                            <Checkbox onCheckedChange={(checked) => {
                                                setChoosedAnalogAttributes(choosedAnalogAttributes.filter(attr => !values.includes(attr)));
                                                if (checked) {
                                                    setChoosedAnalogAttributes(prev => {return [...prev, ...values];})
                                                }
                                            }} />
                                            <h4 className="font-semibold mb-2">{ProductAttributeGroupsTranslations[key]}</h4>
                                        </AccordionTrigger>
                                        <AccordionContent>
                                            <ul className="space-y-1 max-h-50 overflow-y-auto">
                                                {values.map(value => (
                                                    <li onClick={() => {setChoosedAnalogAttributes(prev => {
                                                        console.log("click")
                                                        const current = prev ?? [];
                                                        if (!current.includes(value)) {
                                                            return [...current, value];
                                                        } else {
                                                            return current.filter(a => a !== value);
                                                        }
                                                    })}}
                                                        key={value} className="bg-white rounded px-2 py-1 border text-sm">
                                                        {choosedAnalogAttributes!.includes(value) ?
                                                            (<b>{LaptopFiltersTranslations[value]}</b>) : LaptopFiltersTranslations[value]}
                                                    </li>
                                                ))}
                                            </ul>
                                        </AccordionContent>
                                    </AccordionItem>
                                </Accordion>
                            ))}
                        </div>
                        <Separator className="my-4 border-t border-gray-300"></Separator>
                        <Button onClick={() => findAnalogs()}>Искать аналоги</Button>
                        {isAnalogsFetched && (<p>Поиск аналогов</p>)}
                        <div className={"flex mt-4 h-150 overflow-x-auto"}>
                            {analogs && analogs.map(analog => (
                                <ItemCard product={analog}/>
                            ))}
                        </div>
                    </TabsContent>
                    <TabsContent value="attribute">

                    </TabsContent>
                    <TabsContent value="analyse">

                    </TabsContent>
                </Tabs>
            </div>
        </div>

        // {product.percentChange !== 0 && product.percentChange !== undefined && (
        //     <div>
        //         <TooltipProvider>
        //             <Tooltip>
        //                 <TooltipTrigger asChild>
        //                     <Badge
        //                         variant="outline"
        //                         className={`flex gap-1 rounded-lg text-xs ${
        //                             product.percentChange > 0 ? "text-red-600 border-red-600" : "text-green-600 border-green-600"
        //                         }`}
        //                     >
        //                         {product.percentChange > 0 ? (
        //                             <TrendingUpIcon className="size-3 text-red-600" />
        //                         ) : (
        //                             <TrendingDownIcon className="size-3 text-green-600" />
        //                         )}
        //                         {product.percentChange > 0 ? `+${product.percentChange.toFixed(2)}%` : `${product.percentChange.toFixed(2)}%`}
        //                     </Badge>
        //                 </TooltipTrigger>
        //                 <TooltipContent>
        //                     <p>The price has changed by {product.percentChange.toFixed(2)}% since the last collection</p>
        //                 </TooltipContent>
        //             </Tooltip>
        //         </TooltipProvider>
        //     </div>
        // )}
    );
};

export default ProductDetail;
