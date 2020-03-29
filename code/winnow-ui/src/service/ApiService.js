/**
 * The ApiService provides access to all the Winnow API calls except
 * those related to authentication/authorization.
 */
import axios from 'axios';
import * as Constants from '../constants';

export const fetchGenes = (partial) => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/genes`,
            {
                headers: Constants.authHeader,
            }
            )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

export const fetchMeshtermCat = () => {
    console.debug(`fetchMeshtermCat: ${Constants.WINNOW_API_BASE_URL}/meshterms/category`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/meshterms/category`,
            {
                headers: Constants.authHeader,
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

export const fetchMeshtermTree = (node) => {
    console.debug(`fetchMeshtermTree: ${Constants.WINNOW_API_BASE_URL}/meshterms/tree/parentid/${node}`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/meshterms/tree/parentid/${node}`,
            {
                headers: Constants.authHeader,
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

export const fetchMeshtermNode = (node) => {
    console.debug(`fetchMeshtermNode: ${Constants.WINNOW_API_BASE_URL}/meshterms/tree/nodeid/${node}`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/meshterms/tree/nodeid/${node}`,
            {
                headers: Constants.authHeader,
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

export const mapMeshtermTreeData = (data, node, depth) => {
    return data.map((mesh, index) => {
        return {
            children: [],
            id: (depth > 0)
                ? `${mesh.treeParentId.trim()}.${mesh.treeNodeId.trim()}`
                : `${mesh.treeParentId.trim()}${mesh.treeNodeId.trim()}`,
            meshIndex: `${node.meshIndex}:${index}`,
            hasChild: mesh.hasChild,
            name: mesh.meshName.trim(),
        };
    });
};

