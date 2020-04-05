/**
 * The ApiService provides access to all the Winnow API calls except
 * those related to authentication/authorization.
 */
import axios from 'axios';
import * as Constants from '../constants';
import AuthService from "./AuthService";

export const fetchUserBookmarks = () => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/bookmarks`,
            {
                headers: AuthService.getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

export const saveUserBookmark = (data) => {
    console.info(`saveUserBookmark: ${JSON.stringify(data)}`);
    return new Promise((resolve, reject) => {
        if (data.length < 1) {
            reject("Bookmark name can not be blank!")
        }
        axios.post(
            `${Constants.WINNOW_API_BASE_URL}/bookmarks`,
            data,
            {
                headers: AuthService.getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

export const removeUserBookmark = (bookmarkId) => {
    console.info(`removeUserBookmark: ${JSON.stringify(bookmarkId)}`);
    return new Promise((resolve, reject) => {
        axios.delete(
            `${Constants.WINNOW_API_BASE_URL}/bookmarks/${bookmarkId}`,
            {
                headers: AuthService.getAuthHeader(),
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
 /*   let mockResp = {
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
    });*/
    return new Promise((resolve, reject) => {
        axios.post(
            `${Constants.WINNOW_API_BASE_URL}/search`,
            data,
            {
                headers: AuthService.getAuthHeader(),
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
                headers: AuthService.getAuthHeader(),
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
                headers: AuthService.getAuthHeader(),
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
                headers: AuthService.getAuthHeader(),
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
                headers: AuthService.getAuthHeader(),
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

export const fetchPubMedArticleList = (data) => {
    console.info(`fetchPubMedArticleList: ${JSON.stringify(data)}`);
    let mockResp = {
        searchQuery: data.searchQuery,
        queryType: data.queryType,
        queryFormat: data.queryFormat,
        geneId: data.geneId,
        meshId: data.meshId,
        results: [
            {
                publicationID: "32052514",
                publicationTitle: "COVID-19 Stuff",
                publicationAuthor: "publicationAuthor",
                publicationDate: "31 March 2020",
                publicationURLBase: "https://pubmed.ncbi.nlm.nih.gov/"
            },
            {
                publicationID: "32052514",
                publicationTitle: "COVID-19 Stuff",
                publicationAuthor: "publicationAuthor",
                publicationDate: "31 March 2020",
                publicationURLBase: "https://pubmed.ncbi.nlm.nih.gov/"
            },
            {
                publicationID: "32052514",
                publicationTitle: "COVID-19 Stuff",
                publicationAuthor: "publicationAuthor",
                publicationDate: "31 March 2020",
                publicationURLBase: "https://pubmed.ncbi.nlm.nih.gov/"
            },
        ]
    };
    return new Promise((resolve, reject) => {
        resolve(mockResp);
    });
    return new Promise((resolve, reject) => {
        axios.post(
            `${Constants.WINNOW_API_BASE_URL}/pubmedarticlelist`,
            data,
            {
                headers: AuthService.getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};
