import {Card, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import {TrendingUpIcon} from "lucide-react";
import {Button} from "@/components/ui/button";
import {useNavigate} from "react-router-dom";
import {FavoriteButton} from "@/components/FavoriteButton.tsx";

interface ItemCardProps {
    product: ProductDto;
    isFavorite: boolean;
    onToggleFavorite: (productId: number) => void;
}

export function ItemCard({product, isFavorite, onToggleFavorite}: ItemCardProps) {
    const navigate = useNavigate();
    const fallbackImage =
        "https://img.freepik.com/premium-vector/no-photo-available-vector-icon-default-image-symbol-picture-coming-soon-web-site-mobile-app_87543-10615.jpg";

    if (!product || !product.id) return null;

    return (
        <Card className="flex flex-col h-full relative">
            <CardHeader>
                <img
                    src={product.imageUrl || fallbackImage}
                    alt={product.productName}
                    className="w-full h-40 object-contain rounded-t-lg"
                    onError={(e) => {
                        (e.target as HTMLImageElement).src = fallbackImage;
                    }}
                />

                <FavoriteButton
                    productId={product.id}
                    isFavorite={isFavorite}
                    onToggle={() => onToggleFavorite(product.id)}
                    className="absolute top-4 left-4"
                />

                <CardDescription className="text-2xl font-semibold">
                    {product.brand || "No Brand"}
                </CardDescription>
                <CardTitle>{product.productName}</CardTitle>

                <div className="absolute top-4 right-4">
                    <Badge variant="outline" className="flex gap-1 rounded-lg text-xs">
                        <TrendingUpIcon className="size-3"/>
                        +12.5%
                    </Badge>
                </div>
            </CardHeader>

            <CardFooter className="mt-auto flex flex-col items-start gap-2 text-sm">
                <div className="text-2xl font-semibold">100 000</div>
                <Button type="button" className="w-full" onClick={() => navigate(`/product/${product.id}`)}>
                    Узнать подробнее
                </Button>
            </CardFooter>
        </Card>
    );
}