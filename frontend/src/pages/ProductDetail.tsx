import {useParams} from "react-router-dom";
import React, {useEffect, useMemo, useState} from "react";
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
import {Collapsible, CollapsibleContent, CollapsibleTrigger,} from "@/components/ui/collapsible"
import {ProductCard} from "@/components/ProductCard.tsx";
import {Card} from "@/components/ui/card.tsx";
import MarketplaceLogo from "@/components/MarketplaceLogo.tsx";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue,} from "@/components/ui/select"
import {ProductPriceChange} from "@/components/ProductPriceChange.tsx";
import {analysisService} from "@/services/AnalysisService.ts";
import {AnalysisDataDto} from "@/types/AnalysisDataDto.tsx";
import {TestComponent} from "@/components/TestComponent.tsx";

const ProductDetail: React.FC = () => {
    const {url} = useParams<{ url: string }>();
    const [product, setProduct] = useState<ProductDto | null>(null);
    const decodedUrl = decodeURIComponent(url as string);
    const {favorites, toggleFavorite} = useFavorites();
    const [analogs, setAnalogs] = useState<ProductDto[]>([]);
    const [analogAttributes, setAnalogAttributes] = useState<Record<string, string[]>>();
    const [choosedAnalogAttributes, setChoosedAnalogAttributes] = useState<string[]>([]);
    const [isAnalogsFetched, setIsAnalogsFetched] = useState<boolean>(false);
    const [similarProducts, setSimilarProducts] = useState<ProductDto[]>([]);
    const [sortOrder, setSortOrder] = useState<'asc' | 'desc' | null>(null);
    const [analysisData, setAnalysisData] = useState<AnalysisDataDto | null>(null);
    const [identProdStats, setIdentProdStats] = useState<any | null>(null);

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
        const fetchAnalytics = async () => {
            try {
                const data = await analysisService.getAnalysis(decodedUrl);
                setAnalysisData(data);
            } catch (error) {
                console.error("Ошибка при загрузке данных", error);
            }
        }
        const fetchIdentProdStats = async () => {
            try {
                const data = await analysisService.getIdenticalProductStats(decodedUrl);
                setIdentProdStats(data);
            } catch (error) {
                console.error("Ошибка при загрузке данных", error);
            }
        }
        fetchIdentProdStats()
        fetchProduct();
        fetchAnalogAttributes();
        fetchAnalytics()
    }, [decodedUrl]);

    const sortedProducts = useMemo(() => {
        if (sortOrder === 'asc') {
            return [...similarProducts].sort((a, b) => a.lastPrice - b.lastPrice);
        } else if (sortOrder === 'desc') {
            return [...similarProducts].sort((a, b) => b.lastPrice - a.lastPrice);
        }
        return similarProducts;
    }, [similarProducts, sortOrder]);

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
                    <div className="w-1/4 flex justify-center relative overflow-hidden">
                        <img
                            src={product.imageUrl}
                            alt={product.productName}
                            className="w-full h-full object-contain"
                        />
                        {/* Блюр на пустое место */}
                        <div className="absolute inset-0 bg-cover bg-center filter blur-lg"
                             style={{backgroundImage: `url(${product.imageUrl})`, zIndex: -1}}></div>
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

                    <Card
                        className="w-1/3 rounded-xl shadow-sm p-4 flex flex-col items-center gap-3 text-center self-start">
                        <div className="flex items-center">
                            <p className="text-xs text-muted-foreground uppercase tracking-wide">{product.marketplace}</p>
                            <MarketplaceLogo marketplace={product.marketplace}/>
                        </div>

                        <p className="text-2xl font-extrabold">
                            {product.lastPrice && product.lastPrice !== 0
                                ? product.lastPrice.toLocaleString("ru-RU")
                                : "Цена не указана"}
                            {product.lastPrice && product.lastPrice !== 0 &&
                                <span className="text-base font-semibold"> ₽</span>
                            }
                        </p>

                        <ProductPriceChange
                            product={product}
                            analysisData={analysisData}
                        />

                        <a href={product.url} target="_blank" rel="noopener noreferrer" className="w-full">
                            <Button className="w-full text-sm font-semibold py-2 rounded-md">
                                Перейти на сайт
                            </Button>
                        </a>
                    </Card>
                </div>

                {similarProducts.length > 0 && (
                    <div className="w-full">
                        <p className="text-lg font-semibold py-4">Другие предложения:</p>

                        <div className="pb-4">
                            <Select onValueChange={(value) => {
                                if (value === 'asc') setSortOrder('asc');
                                else if (value === 'desc') setSortOrder('desc');
                            }}>
                                <SelectTrigger className="w-[180px]">
                                    <SelectValue placeholder="Сортировка"/>
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="asc">По возрастанию цены</SelectItem>
                                    <SelectItem value="desc">По убыванию цены</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        {/* Всегда показываем первые 2 */}
                        <div className="space-y-3">
                            {sortedProducts.slice(0, 2).map((product, index) => (
                                <ProductCard key={index} product={product}/>
                            ))}
                        </div>

                        {/* Остальные — по кнопке */}
                        {sortedProducts.length > 2 && (
                            <Collapsible>
                                <CollapsibleContent>
                                    <div className="space-y-3 mt-4">
                                        {sortedProducts.slice(2).map((product, index) => (
                                            <ProductCard key={index + 2} product={product}/>
                                        ))}
                                    </div>
                                </CollapsibleContent>
                                <CollapsibleTrigger asChild>
                                    <Button variant="outline" className="w-full mt-4">
                                        Показать ещё {sortedProducts.length - 2}
                                    </Button>
                                </CollapsibleTrigger>
                            </Collapsible>
                        )}
                    </div>
                )}


                <Tabs defaultValue="analyse" className="w-full pt-10">
                    <TabsList className="flex flex-row space-x-10">
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
                                                    setChoosedAnalogAttributes(prev => {
                                                        return [...prev, ...values];
                                                    })
                                                }
                                            }}/>
                                            <h4 className="font-semibold mb-2">{ProductAttributeGroupsTranslations[key]}</h4>
                                        </AccordionTrigger>
                                        <AccordionContent>
                                            <ul className="space-y-1 max-h-50 overflow-y-auto">
                                                {values.map(value => (
                                                    <li onClick={() => {
                                                        setChoosedAnalogAttributes(prev => {
                                                            console.log("click")
                                                            const current = prev ?? [];
                                                            if (!current.includes(value)) {
                                                                return [...current, value];
                                                            } else {
                                                                return current.filter(a => a !== value);
                                                            }
                                                        })
                                                    }}
                                                        key={value}
                                                        className="bg-white rounded px-2 py-1 border text-sm">
                                                        {choosedAnalogAttributes!.includes(value) ?
                                                            (
                                                                <b>{LaptopFiltersTranslations[value]}</b>) : LaptopFiltersTranslations[value]}
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
                                <ItemCard
                                    product={analog}
                                    isFavorite={false}
                                    onToggleFavorite={toggleFavorite}
                                />

                            ))}
                        </div>
                    </TabsContent>
                    <TabsContent value="attribute">

                    </TabsContent>
                    <TabsContent value="analyse">
                        <TestComponent chartData={identProdStats}></TestComponent>
                    </TabsContent>
                </Tabs>
            </div>
        </div>
    );
};

export default ProductDetail;
