import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import {authService} from "@/services/AuthService.ts";
import {toast} from "sonner";

export function RefisterForm({
                              className,
                              ...props
                          }: React.ComponentProps<"div">) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate(); // Хук для редиректа

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const dto: SignUpDto = { username, password };
        try {
            await authService.registration(dto);
            toast.success("Registration successful!");
            await authService.authorization(dto)
            navigate("/"); // Редирект на главную страницу
        } catch (error) {
            toast.error("Registration failed!");
        }
    };

    return (
        <div className={cn("flex flex-col gap-6", className)} {...props}>
            <Card>
                <CardHeader>
                    <CardTitle>Registrate new account</CardTitle>
                    <CardDescription>
                        Enter your email and password for registration
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit}>
                        <div className="flex flex-col gap-6">
                            <div className="grid gap-3">
                                <Label htmlFor="email">Email</Label>
                                <Input
                                    id="email"
                                    type="email"
                                    value={username}
                                    placeholder="m@example.com"
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="grid gap-3">
                                <div className="flex items-center">
                                    <Label htmlFor="password">Password</Label>
                                </div>
                                <Input
                                    id="password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="flex flex-col gap-3">
                                <Button type="submit" className="w-full">
                                    Registration
                                </Button>
                            </div>
                        </div>
                        <div className="mt-4 text-center text-sm">
                            Have an account?{" "}
                            <a href="/signIn" className="underline underline-offset-4">
                                Sign in
                            </a>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}
