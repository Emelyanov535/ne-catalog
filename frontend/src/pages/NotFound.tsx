import React from "react";

const NotFound: React.FC = () => {
    return (
        <div className="flex items-center justify-center h-screen text-center">
            <div>
                <h1 className="text-4xl font-bold text-red-600 mb-4">404</h1>
                <p className="text-lg text-gray-700">Страница не найдена</p>
            </div>
        </div>
    );
};

export default NotFound;
