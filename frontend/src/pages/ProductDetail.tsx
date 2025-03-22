import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { catalogService } from "@/services/CatalogService.ts";

const ProductDetail: React.FC = () => {
    const { id } = useParams<{ id: bigint }>();
    const [product, setProduct] = useState<ProductDto | null>(null);

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const data = await catalogService.getProductById(id);
                console.log(data)
                setProduct(data);
            } catch (error) {
                console.error("Ошибка при загрузке данных", error);
            }
        };
        fetchProduct();
    }, [id]);

    if (!product) return <p>Загрузка...</p>;


    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold">{product.productName}</h1>
            <p>Бренд: {product.brand}</p>
            <p>Цена: 100 000</p>
            <img src={product.imageUrl} alt={product.productName} className="w-60 h-60 object-contain" />
        </div>
    );
};

export default ProductDetail;
