import { Button } from "@/components/ui/button"
import {Separator} from "@/components/ui/separator.tsx";

const Footer = () => {
    return (
        <footer className="mt-10 border-t bg-muted/50 text-sm text-muted-foreground">
            <div className="container mx-auto px-4 py-8 grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* О нас */}
                <div>
                    <h3 className="font-semibold mb-2">О проекте</h3>
                    <p>ne-catalog — сервис для поиска и сравнения цен. Мы экономим ваше время и деньги.</p>
                </div>

                {/* Быстрые ссылки */}
                <div>
                    <h3 className="font-semibold mb-2">Навигация</h3>
                    <ul className="space-y-1">
                        <li><a href="/" className="hover:underline">Главная</a></li>
                        <li><a href="/catalog/laptops" className="hover:underline">Ноутбуки</a></li>
                        <li><a href="/catalog/phones" className="hover:underline">Смартфоны</a></li>
                        <li><a href="/favorites" className="hover:underline">Избранное</a></li>
                    </ul>
                </div>

                {/* Контакты или подписка */}
                <div>
                    <h3 className="font-semibold mb-2">Связь с нами</h3>
                    <p>Есть предложения или баги? Напишите нам:</p>
                    <Button variant="link" className="px-0" asChild>
                        <a href="mailto:support@ne-catalog.ru">support@ne-catalog.ru</a>
                    </Button>
                </div>
            </div>

            <Separator />

            {/* Подвал с копирайтом */}
            <div className="container mx-auto px-4 py-4 flex flex-col md:flex-row items-center justify-between text-xs">
                <span>&copy; {new Date().getFullYear()} ne-catalog. Все права защищены.</span>
                <span className="mt-2 md:mt-0">Сделано с ❤️ в России</span>
            </div>
        </footer>
    )
}

export default Footer
