import React, {useEffect, useRef, useState} from "react";
import {useNavigate, useParams, useSearchParams} from "react-router-dom";
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
import {CatalogDropdown} from "@/components/nav-actions.tsx";
import {Menu, Search} from "lucide-react";
import {Input} from "@/components/ui/input.tsx";
import {searchService} from "@/services/SearchService.ts";
import {CategoryTranslations} from "@/types/CategoryTranslations.ts";
import {Button} from "@/components/ui/button.tsx";
import {Card} from "@/components/ui/card.tsx";
import {cn} from "@/lib/utils.ts";

const Navbar: React.FC = () => {
    const [isNotificationsEnabled, setIsNotificationsEnabled] = useState<boolean>(false);
    const navigate = useNavigate();
    const isAuthenticated = Boolean(localStorageService.getAccessToken());
    const [username, setUsername] = useState<string>("");
    const [searchParams, setSearchParams] = useSearchParams();
    const initialQuery = searchParams.get('searchQuery') || '';

    const [query, setQuery] = useState(initialQuery);
    const [results, setResults] = useState<string[]>([]);
    const [showResults, setShowResults] = useState(false);
    const debounceRef = useRef<NodeJS.Timeout | null>(null);

    useEffect(() => {
        if (debounceRef.current) clearTimeout(debounceRef.current);

        debounceRef.current = setTimeout(() => {
            if (!query.trim()) {
                setResults([]);
                return;
            }

            searchService.fetchAvailableSearchCategories(query)
                .then(data => {
                    setResults(data);
                })
                .catch(console.error);

        }, 300);

        return () => {
            if (debounceRef.current) clearTimeout(debounceRef.current);
        };
    }, [query]);

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

    return (
        <header
            className="border-grid z-50 sticky top-0 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <div className="flex justify-between container mx-auto flex h-14 items-center px-4 md:px-6">
                <div className="flex items-center gap-4">
                    <a href="/" className="flex items-center gap-2 font-bold">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256" className="h-6 w-6">
                            <line x1="208" y1="128" x2="128" y2="208" stroke="currentColor" strokeWidth="32"></line>
                            <line x1="192" y1="40" x2="40" y2="192" stroke="currentColor" strokeWidth="32"></line>
                        </svg>
                        <span className="hidden lg:inline">ne-catalog</span>
                    </a>
                    <div className="px-3">
                        <CatalogDropdown/>
                    </div>

                </div>

                <div className={""}>
                    <div className="relative">
                        <Input
                            type="search"
                            placeholder="Поиск товаров..."
                            className={`
                            pl-4 pr-10 py-2 text-sm border rounded-xl shadow-sm
                            w-48
                            transition-all duration-300 ease-in-out
                            focus:w-80 focus:outline-none focus:ring-2 focus:ring-primary`}
                            onChange={e => setQuery(e.target.value)}
                            onFocus={() => setShowResults(true)}
                            onBlur={() => setTimeout(() => setShowResults(false), 100)}
                            style={{transformOrigin: 'center'}}
                        />
                        <Search className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400" size={18}/>
                    </div>
                    {/*{showResults && results.length > 0 && (*/}
                        <>
                            <Card className={cn(
                                "absolute mt-2 p-2",
                                "transition-all duration-300 ",
                                "overflow-hidden",
                                "origin-left", // Точка трансформации слева
                                showResults && results.length > 0
                                    ? "w-80 opacity-100 pointer-events-auto"
                                    : "w-48 opacity-0 pointer-events-none"
                            )}>
                                <div className="py-2">
                                    <Label className={"p-2 bold text-xl"}>Искать в</Label>
                                    <div className="space-y-1 mt-1">
                                        {results.map((title, idx) => (
                                            <Button
                                                key={idx}
                                                variant="ghost"
                                                className="w-full justify-start px-2 py-1 h-auto text-sm font-normal"
                                                onMouseDown={() => {
                                                    setShowResults(false);
                                                    navigate(`/catalog/${title}?searchQuery=${encodeURIComponent(query)}`);
                                                }}
                                            >
                                                {CategoryTranslations[title]}
                                            </Button>
                                        ))}
                                    </div>
                                </div>
                            </Card>
                        </>
                    {/*)}*/}
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