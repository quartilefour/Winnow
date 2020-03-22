/**
 * auth.js provides the base context to hold user's authenticated state.
 */
import { createContext, useContext } from 'react';

export const AuthContext = createContext({});

export function useAuth() {
    return useContext(AuthContext);
}
