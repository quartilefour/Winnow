/**
 * auth.js provides the base context to hold user's authenticated state.
 */
import {createContext, useContext} from 'react';

export const MeshTreeContext = createContext({});

export function useMeshTree() {
    return useContext(MeshTreeContext);
}
