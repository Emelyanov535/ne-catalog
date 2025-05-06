// components/ProductCard.tsx

import {Button} from "@/components/ui/button";
import React from "react";
import {Card} from "./ui/card";
import MarketplaceLogo from "@/components/MarketplaceLogo.tsx";
import {ChevronDownIcon, ChevronUpIcon} from "lucide-react";

const fallbackImage = "https://img.freepik.com/premium-vector/no-photo-available-vector-icon-default-image-symbol-picture-coming-soon-web-site-mobile-app_87543-10615.jpg";

export const ProductCard: React.FC<{ product: ProductDto }> = ({product}) => {
    return (

        <Card
            className="flex flex-col sm:flex-row w-full justify-between items-start sm:items-center gap-4 p-4 border rounded-lg shadow-sm">
            <div className="flex w-full sm:w-auto">
                {/* Левая часть — изображение */}
                <img
                    src={product.imageUrl || fallbackImage}
                    alt={product.productName}
                    className="w-24 h-24 object-contain rounded-lg"
                    onError={(e) => {
                        (e.target as HTMLImageElement).src = fallbackImage;
                    }}
                />
            </div>

            <div className="flex-1">
                {/* Средняя часть — информация о товаре */}
                <p className="text-base font-medium">{product.productName}</p>
                <div className="flex items-center">
                    <p className="text-xs text-muted-foreground uppercase tracking-wide">{product.marketplace}</p>
                    <MarketplaceLogo marketplace={product.marketplace}/>
                </div>
            </div>

            <div>
                <p className="text-2xl font-extrabold">
                    {product.lastPrice && product.lastPrice !== 0
                        ? product.lastPrice.toLocaleString("ru-RU")
                        : "Цена не указана"}
                    {product.lastPrice && product.lastPrice !== 0 &&
                        <span className="text-base font-semibold"> ₽</span>
                    }
                </p>

                {product.percentChange !== 0 && product.percentChange !== undefined ?
                    <div
                        className={`flex items-center gap-1 text-xs ${
                            product.percentChange > 0 ? "text-red-600" : "text-green-600"
                        }`}
                    >
                        {product.percentChange > 0 ? (
                            <ChevronUpIcon className="w-4 h-4"/>
                        ) : (
                            <ChevronDownIcon className="w-4 h-4"/>
                        )}
                        <span>
                            {product.percentChange > 0
                                ? `+${product.percentChange}`
                                : `${Math.abs(product.percentChange)}`} ₽
                        </span>
                    </div> : null
                }


            </div>

            <div className="flex flex-col items-end gap-2 shrink-0 w-full sm:w-auto">
                <a
                    href={`/product/${encodeURIComponent(product.url)}`}
                    className="w-full sm:w-auto"
                >
                    <Button className="w-full sm:w-auto text-sm px-4 py-2">Узнать подробнее</Button>
                </a>
            </div>
        </Card>


    );
};
