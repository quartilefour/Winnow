import {BrowserRouter} from "react-router-dom";
import Enzyme, {shallow, mount} from "enzyme";
import { shape } from 'prop-types';

/* istanbul ignore file */

// Instantiate router context
const router = {
    history: new BrowserRouter().history,
    route: {
        location: {},
        match: {},
    },
    authToken: 'jwttoken',
};

const createContext = () => ({
    context: { router },
    childContextTypes: { router: shape({}) },
});

export function mountWrap(node) {
    return mount(node, createContext());
}

export function shallowWrap(node) {
    return shallow(node, createContext());
}