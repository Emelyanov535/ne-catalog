import {Card, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {useNavigate} from "react-router-dom";
import {FavoriteButton} from "@/components/FavoriteButton.tsx";

interface ItemCardProps {
    product: ProductDto;
    isFavorite: boolean;
    onToggleFavorite: (productUrl: string) => void;
}

export function ItemCard({product, isFavorite, onToggleFavorite}: ItemCardProps) {
    const navigate = useNavigate();
    const fallbackImage =
        "https://img.freepik.com/premium-vector/no-photo-available-vector-icon-default-image-symbol-picture-coming-soon-web-site-mobile-app_87543-10615.jpg";

    if (!product || !product.url) return null;

    return (
        <Card className="flex flex-col h-full relative">
            <CardHeader>
                <img //TODO сделать красиво
                    src={product.imageUrl || fallbackImage}
                    alt={product.productName}
                    className="w-full h-50 object-contain rounded-t-lg"
                    onError={(e) => {
                        (e.target as HTMLImageElement).src = fallbackImage;
                    }}
                />

                <FavoriteButton
                    productUrl={product.url}
                    isFavorite={isFavorite}
                    onToggle={() => onToggleFavorite(product.url)}
                    className="absolute top-4 left-4"
                />
                <CardDescription className="text-2xl font-semibold">
                    {product.brand || "No Brand"}
                </CardDescription>
                <CardTitle className={"overflow-hidden"}>{product.productName}</CardTitle>
            </CardHeader>

            <CardFooter className="mt-auto flex flex-col items-start gap-2 text-sm">
                {product.lastPrice && (<div className="text-2xl font-semibold">{product.lastPrice} ₽</div>)}
                <Button type="button" className="w-full"
                        onClick={() => navigate(`/product/${encodeURIComponent(product.url)}`)}>
                    Узнать подробнее
                </Button>
            </CardFooter>
        </Card>
    );
}