"use client"

import {useEffect, useState} from "react"
import {Menu} from "lucide-react"
import {Button} from "@/components/ui/button"
import {Popover, PopoverContent, PopoverTrigger,} from "@/components/ui/popover"
import {
    Sidebar,
    SidebarContent,
    SidebarGroup,
    SidebarGroupContent,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    SidebarProvider,
} from "@/components/ui/sidebar"
import {catalogService} from "@/services/CatalogService.ts"
import {useNavigate} from "react-router-dom";
import {CategoryTranslations} from "@/types/CategoryTranslations.ts";

export function NavActions() {
    const [isOpen, setIsOpen] = useState(false)
    const [categories, setCategories] = useState<string[]>([])
    const navigate = useNavigate();

    useEffect(() => {
        const fetchCategories = async () => {
            const data = await catalogService.getCategories()
            setCategories(data)
        }

        fetchCategories()
    }, [])

    const goToCatalog = (category: string) => {
        navigate("/catalog/" + category);
    }

    return (
        <SidebarProvider>
            <div className="flex items-center gap-2 text-sm">
                <Popover open={isOpen} onOpenChange={setIsOpen}>
                    <PopoverTrigger asChild>
                        <Button variant="ghost" className="flex items-center gap-2">
                            <Menu className="w-7 h-7"/>
                            Каталог
                        </Button>
                    </PopoverTrigger>
                    <PopoverContent
                        className="w-56 overflow-hidden rounded-lg p-0"
                        align="start"
                    >
                        <Sidebar collapsible="none" className="bg-transparent">
                            <SidebarContent>
                                <SidebarGroup>
                                    <SidebarGroupContent>
                                        <SidebarMenu>
                                            {categories.map((item, index) => (
                                                <SidebarMenuItem key={index} onClick={() => goToCatalog(item)}>
                                                    <SidebarMenuButton>
                                                        {CategoryTranslations[item] || item}
                                                    </SidebarMenuButton>
                                                </SidebarMenuItem>
                                            ))}
                                        </SidebarMenu>
                                    </SidebarGroupContent>
                                </SidebarGroup>
                            </SidebarContent>
                        </Sidebar>
                    </PopoverContent>
                </Popover>
            </div>
        </SidebarProvider>
    )
}
