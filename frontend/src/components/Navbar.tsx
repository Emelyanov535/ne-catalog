import React, {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {localStorageService} from "../services/LocalStorageService";
import {authService} from "../services/AuthService";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {Label} from "@/components/ui/label.tsx";
import {Switch} from "@/components/ui/switch.tsx";
import {ModeToggle} from "@/components/mode-toggle.tsx";
import {Button} from "@/components/ui/button.tsx";
import {catalogService} from "@/services/CatalogService.ts";
import {CategoryTranslations} from "@/types/CategoryTranslations.ts";

const Navbar: React.FC = () => {
    const [isNotificationsEnabled, setIsNotificationsEnabled] = useState<boolean>(false);
    const navigate = useNavigate();
    const isAuthenticated = Boolean(localStorageService.getAccessToken());
    const [username, setUsername] = useState<string>("");
    const [categories, setCategories] = useState<string[]>([""]);

    const fetchUserData = async () => {
        try {
            const response = await authService.whoami();
            setIsNotificationsEnabled(response.isNotification);
            setUsername(response.username)
        } catch (error) {
            console.error("Ошибка при загрузке данных пользователя:", error);
        }
    };

    const handleSwitchChange = async (checked: boolean) => {
        try {
            await authService.changeNotificationStatus();
            setIsNotificationsEnabled(checked);
        } catch (error) {
            console.error("Ошибка при обновлении настроек уведомлений:", error);
        }
    };

    useEffect(() => {
        fetchCategories();
        if (isAuthenticated) {
            fetchUserData();
        }
    }, [isAuthenticated]);

    const handleLogout = () => {
        authService.logout().then(() => navigate("/signIn"));
    };

    const handleFavoritesClick = () => {
        navigate("/favorites");
    };

    const fetchCategories = async () => {
        setCategories(await catalogService.getCategories());
    }

    const goToCatalog = (category: string) => {
        navigate("/catalog/" + category);
    }

    return (
        <header className="border-grid sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <div className="flex justify-between container mx-auto flex h-14 items-center px-4 md:px-6">
                <div className="flex items-center gap-4">
                    <a href="/" className="flex items-center gap-2 font-bold">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256" className="h-6 w-6">
                            <line x1="208" y1="128" x2="128" y2="208" stroke="currentColor" strokeWidth="32"></line>
                            <line x1="192" y1="40" x2="40" y2="192" stroke="currentColor" strokeWidth="32"></line>
                        </svg>
                        <span className="hidden lg:inline">ne-catalog</span>
                    </a>
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <Button variant="outline">
                                Выбрать категорию
                            </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                            {categories.map((category, index) => (
                                <DropdownMenuItem key={index.toString()} onClick={() => goToCatalog(category)}>
                                    {CategoryTranslations[category] || category}
                                </DropdownMenuItem>
                            ))}
                        </DropdownMenuContent>
                    </DropdownMenu>
                </div>

                <div className="">

                </div>

                <div className="flex items-center gap-2">
                    <ModeToggle/>

                    {isAuthenticated ? (
                        <DropdownMenu modal={false}>
                            <DropdownMenuTrigger asChild>
                                <Avatar>
                                    <AvatarImage src="https://github.com/shadcn.png"/>
                                    <AvatarFallback>CN</AvatarFallback>
                                </Avatar>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent className="w-56">
                                <DropdownMenuLabel>My Account</DropdownMenuLabel>
                                <DropdownMenuSeparator/>
                                <DropdownMenuItem>
                                    {username}
                                </DropdownMenuItem>
                                <DropdownMenuSeparator/>
                                <DropdownMenuGroup>
                                    <DropdownMenuItem onClick={handleFavoritesClick}>
                                        Favorites
                                    </DropdownMenuItem>
                                    <DropdownMenuItem>
                                        <div className="flex justify-between items-center w-full">
                                            <Label htmlFor="notifications">Notifications</Label>
                                            <Switch
                                                id="notifications"
                                                checked={isNotificationsEnabled}
                                                onCheckedChange={handleSwitchChange}
                                            />
                                        </div>
                                    </DropdownMenuItem>
                                </DropdownMenuGroup>
                                <DropdownMenuSeparator/>
                                <DropdownMenuItem onClick={handleLogout}>
                                    Log out
                                </DropdownMenuItem>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    ) : (
                        <>
                            <nav className="hidden md:flex gap-4 text-sm">
                                <a href="/signIn" className="hover:text-gray-700 dark:hover:text-gray-300">Sign In</a>
                            </nav>
                            <nav className="hidden md:flex gap-4 text-sm">
                                <a href="/signUp" className="hover:text-gray-700 dark:hover:text-gray-300">Sign Up</a>
                            </nav>
                        </>
                    )}
                </div>
            </div>
        </header>

    );
};

export default Navbar;