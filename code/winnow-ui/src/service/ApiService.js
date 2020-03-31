/**
 * The ApiService provides access to all the Winnow API calls except
 * those related to authentication/authorization.
 */
import axios from 'axios';
import * as Constants from '../constants';

export const fetchUserBookmarks = () => {
    let mockResp = [
            {
                searchName: "Saved Search 1",
                queryType: "gene",
                searchQuery: ["1246504","36111395","36110376"],
                createdAt: "2020-03-29 17:43:26",
            },
            {
                searchName: "Saved Search 2",
                queryType: "gene",
                searchQuery: ["1246504","36111395","36110376"],
                createdAt: "2020-03-28 07:12:50",
            },
            {
                searchName: "Saved Search 3",
                queryType: "mesh",
                searchQuery: ["C14.280.778","C14.280.763","C20.425"],
                createdAt: "2020-03-28 11:38:24",
            },
    ];
    return new Promise((resolve, reject) => {
        resolve(mockResp);
    });
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
    let mockResp = {
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
        resolve(mockResp);
    });
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
            `${Constants.WINNOW_API_BASE_URL}/genes/search/${partial}`,
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
        let id = (depth > 0)
            ? `${mesh.treeParentId}.${mesh.treeNodeId}`
            : `${mesh.treeParentId}${mesh.treeNodeId}`;
        return {
            children: [],
            id: id,
            meshIndex: `${node.meshIndex}:${index}`,
            meshId: mesh.meshId,
            hasChild: mesh.hasChild,
            name: `${mesh.meshName} [${id}]`,
        };
    });
};

