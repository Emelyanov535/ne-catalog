import React from "react";
import {RefisterForm} from "@/components/ui/reg-form.tsx";

const SignUp: React.FC = () => {
    return (
        <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
            <div className="w-full max-w-sm">
                <RefisterForm/>
            </div>
        </div>
    );
};

export default SignUp;
