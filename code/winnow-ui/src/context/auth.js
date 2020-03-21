import { createContext, useContext } from 'react';

export const AuthContext = createContext({});

export function useAuth() {
    return useContext(AuthContext);
}

export function getUserFromToken(token) {
    if (token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (error) {
            // ignore
        }
    }

    return null;
}

