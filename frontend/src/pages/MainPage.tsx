import {Button} from "@/components/ui/button.tsx";
import {Card, CardContent} from "@/components/ui/card";
import {useNavigate} from "react-router-dom";

const MainPage = () => {
    const navigate = useNavigate();

    const goToCatalog = (category: string) => {
        navigate("/catalog/" + category);
    }

    return (
        <div className="p-6 space-y-8">
            {/* Hero Section */}
            <div className="text-center space-y-4">
                <h1 className="text-4xl font-bold">🔍 Найдите лучшую цену на ноутбуки и смартфоны</h1>
                <p className="text-gray-500 text-lg">Сравнение цен из магазинов ShopX и StoreY за секунды</p>
            </div>

            {/* Быстрые категории */}
            <div className="grid grid-cols-2 gap-4">
                <Card className="hover:shadow-lg cursor-pointer" onClick={() => goToCatalog('LAPTOP')}>
                    <CardContent className="flex flex-col items-center p-6">
                        <div className="text-5xl">🖥️</div>
                        <h2 className="text-xl font-semibold mt-2">Ноутбуки</h2>
                    </CardContent>
                </Card>
                <Card className="hover:shadow-lg cursor-pointer" onClick={() => goToCatalog('SMARTPHONE')}>
                    <CardContent className="flex flex-col items-center p-6">
                        <div className="text-5xl">📱</div>
                        <h2 className="text-xl font-semibold mt-2">Мобильные телефоны</h2>
                    </CardContent>
                </Card>
            </div>

            {/* Снижение цен */}
            <div className="space-y-4">
                <h2 className="text-2xl font-semibold">🔥 Снижение цен</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                    {[1, 2, 3].map(i => (
                        <Card key={i} className="hover:shadow-md">
                            <CardContent className="p-4">
                                <div className="text-4xl">📦</div>
                                <h3 className="font-bold text-lg mt-2">Товар {i}</h3>
                                <p className="text-sm text-gray-500">Цена снижена на 15%</p>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </div>

            {/* Популярные товары */}
            <div className="space-y-4">
                <h2 className="text-2xl font-semibold">⭐ Популярные товары</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                    {[4, 5, 6].map(i => (
                        <Card key={i} className="hover:shadow-md">
                            <CardContent className="p-4">
                                <div className="text-4xl">🔥</div>
                                <h3 className="font-bold text-lg mt-2">Популярный товар {i}</h3>
                                <Button size="sm" className="mt-2">В избранное</Button>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </div>

            {/* Подписка */}
            <div className="bg-gray-100 p-6 rounded-xl text-center space-y-4">
                <h2 className="text-2xl font-semibold">📩 Подпишитесь на снижение цен</h2>
                <p className="text-gray-500">Получайте уведомления на email, когда цена на интересующие товары падает</p>
                <div className="flex justify-center items-center gap-2 max-w-md mx-auto">
                    <Button>Подписаться</Button>
                </div>
            </div>
        </div>
    )
}

export default MainPage;