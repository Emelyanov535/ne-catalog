import React from "react";
import {LoginForm} from "@/components/ui/login-form.tsx";


const SignIn: React.FC = () => {
    return (
        <div className="flex h-screen items-center justify-center">
            <div className="w-full max-w-sm">
                <LoginForm/>
            </div>
        </div>
    )
};

export default SignIn;
