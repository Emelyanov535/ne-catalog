import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { localStorageService } from "../services/LocalStorageService";

const PrivateRoute: React.FC = () => {
    const isAuthenticated = Boolean(localStorageService.getAccessToken());

    return isAuthenticated ? <Outlet /> : <Navigate to="/signIn" replace />;
};

export default PrivateRoute;
