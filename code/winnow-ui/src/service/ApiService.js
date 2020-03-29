/**
 * The ApiService provides access to all the Winnow API calls except
 * those related to authentication/authorization.
 */
import axios from 'axios';
import * as Constants from '../constants';

export const fetchUserBookmarks = (data) => {
    console.info(`fetchUserBookmarks: ${JSON.stringify(data)}`);
    return {
        searchQuery: data.searchQuery,
        queryType: data.queryType,
        queryFormat: data.queryFormat,
        results: [
            {
                geneId: "geneId1",
                description: "geneDescription1",
                symbol: "geneSymbol1",
                meshId: "meshId1",
                meshTerm: "meshName1",
                publicationCount: "publicationCount",
                pValue: "pValue"
            },
            {
                geneId: "geneId2",
                description: "geneDescription2",
                symbol: "geneSymbol2",
                meshId: "meshId2",
                meshTerm: "meshName2",
                publicationCount: "publicationCount",
                pValue: "pValue"
            },
            {
                geneId: "geneId3",
                description: "geneDescription3",
                symbol: "geneSymbol3",
                meshId: "meshId3",
                meshTerm: "meshName3",
                publicationCount: "publicationCount",
                pValue: "pValue"
            },
        ]
    };
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/bookmarks`,
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

export const fetchSearchResults = (data) => {
    console.info(`fetchSearchResults: ${JSON.stringify(data)}`);
    return {
        searchQuery: data.searchQuery,
        queryType: data.queryType,
        queryFormat: data.queryFormat,
        results: [
            {
                geneId: "geneId1",
                description: "geneDescription1",
                symbol: "geneSymbol1",
                meshId: "meshId1",
                meshTerm: "meshName1",
                publicationCount: "publicationCount",
                pValue: "pValue"
            },
            {
                geneId: "geneId2",
                description: "geneDescription2",
                symbol: "geneSymbol2",
                meshId: "meshId2",
                meshTerm: "meshName2",
                publicationCount: "publicationCount",
                pValue: "pValue"
            },
            {
                geneId: "geneId3",
                description: "geneDescription3",
                symbol: "geneSymbol3",
                meshId: "meshId3",
                meshTerm: "meshName3",
                publicationCount: "publicationCount",
                pValue: "pValue"
            },
        ]
    };
    return new Promise((resolve, reject) => {
        axios.post(
            `${Constants.WINNOW_API_BASE_URL}/search`,
            data,
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

