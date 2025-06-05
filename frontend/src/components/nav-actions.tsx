import {useEffect, useState} from "react"
import {useNavigate} from "react-router-dom"
import {Menu} from "lucide-react"
import {catalogService} from "@/services/CatalogService.ts"
import {CategoryTranslations} from "@/types/CategoryTranslations.ts"
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger,} from "@/components/ui/dropdown-menu"

export function CatalogDropdown() {
    const [categories, setCategories] = useState<string[]>([])
    const navigate = useNavigate()

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const data = await catalogService.getCategories()
                setCategories(data)
            } catch (error) {
                console.error("Ошибка при загрузке категорий:", error)
            }
        }

        fetchCategories()
    }, [])

    const goToCatalog = (category: string) => {
        navigate("/catalog/" + category)
    }

    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <button
                    className="flex items-center gap-2 rounded-md border px-3 py-2 text-sm font-medium shadow-sm hover:bg-muted transition">
                    <span>Каталог</span>
                    <Menu className="w-4 h-4"/>
                </button>
            </DropdownMenuTrigger>
            <DropdownMenuContent side="bottom" align="start" className="min-w-48">
                {categories.map((item, index) => (
                    <DropdownMenuItem key={index} onClick={() => goToCatalog(item)}>
                        {CategoryTranslations[item] || item}
                    </DropdownMenuItem>
                ))}
            </DropdownMenuContent>
        </DropdownMenu>
    )
}
