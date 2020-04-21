/**
 * The ApiService provides access to all the Winnow API calls except
 * those related to authentication/authorization and user management.
 */
import axios from 'axios';
import * as C from '../constants';
import {getAuthHeader} from "./AuthService";

/**
 * Retrieves User bookmarks.
 *
 * @return {Promise<>}
 */
export const fetchUserBookmarks = () => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/bookmarks`,
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Saves a User's search as a bookmark.
 *
 * @param data - JSON object describing a search.
 * @return {Promise<>}
 */
export const saveUserBookmark = (data) => {
    console.info(`saveUserBookmark: ${JSON.stringify(data)}`);
    return new Promise((resolve, reject) => {
        if (data.length < 1) {
            reject("Bookmark name can not be blank!")
        }
        axios.post(
            `${C.WINNOW_API_BASE_URL}/bookmarks`,
            data,
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Deletes a User's saved bookmark.
 *
 * @param bookmarkId
 * @return {Promise<>}
 */
export const removeUserBookmark = (bookmarkId) => {
    return new Promise((resolve, reject) => {
        axios.delete(
            `${C.WINNOW_API_BASE_URL}/bookmarks/${bookmarkId}`,
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Retrieves search results for selected Genes/MeSH terms.
 *
 * @param data
 * @return {Promise<>}
 */
export const fetchSearchResults = (data) => {
    return new Promise((resolve, reject) => {
        axios.post(
            `${C.WINNOW_API_BASE_URL}/search`,
            data,
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Retrieves genes matching `partial` search term.
 *
 * @param partial - String to search gene Id, Symbol, and Description.
 * @return {Promise<>}
 */
export const fetchGenes = (partial) => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/genes/search/${partial}`,
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Retrieves details associated with a given gene.
 *
 * @param geneId
 * @return {Promise<>}
 */
export const fetchGeneDetails = (geneId) => {
    return new Promise((resolve, reject) => {
        axios.post(
            `${C.WINNOW_API_BASE_URL}/genes`,
            {geneId: geneId},
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Retrieves details for a given gene from the NCBI.
 *
 * @param geneId
 * @return {Promise<>}
 */
export const fetchNCBIGeneDetails = (geneId) => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.NCBI_API_BASE}/esummary.fcgi?db=gene&retmode=json&api_key=${C.NCBI_API_TOKEN}&id=${geneId}`,
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Retrieves top level MeSH term categories.
 *
 * @return {Promise<>}
 */
export const fetchMeshtermCat = () => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/meshterms/category`,
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Retrieves MeSH term tree branch for given parent node.
 *
 * @param node
 * @return {Promise<>}
 */
export const fetchMeshtermTree = (node) => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/meshterms/tree/parentid/${node}`,
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Retrieves MeSH term tree node.
 *
 * @param node
 * @return {Promise<>}
 */
export const fetchMeshtermNode = (node) => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/meshterms/tree/nodeid/${node}`,
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Helper function to map MeSH term data to tree view node.
 *
 * @param data - JSON object of MeSH term.
 * @param node - Tree view node.
 * @param depth - Tree view depth.
 * @return JSON object of Tree node to insert.
 */
export const mapMeshtermTreeData = (data, node, depth) => {
    return data.map((mesh, index) => {
        let id = (depth > 0)
            ? `${mesh.treeParentId}.${mesh.treeNodeId}`
            : `${mesh.treeParentId}${mesh.treeNodeId}`;
        return {
            children: [],
            id: id,
            isChecked: node.isChecked,
            meshIndex: `${node.meshIndex}:${index}`,
            meshId: mesh.meshId,
            hasChild: mesh.hasChild,
            name: `${mesh.meshName} [${id}]`,
        };
    });
};

/**
 * Retrieves PubMed articles associated with provided Gene/MeSH terms.
 *
 * @param data - JSON object containing geneIds/meshIds.
 * @return {Promise<>}
 */
export const fetchPubMedArticleList = (data) => {
    return new Promise((resolve, reject) => {
        axios.post(
            `${C.WINNOW_API_BASE_URL}/publications`,
            data,
            {
                headers: getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * Checks for API availability.
 *
 * @return {Promise<>}
 */
export const fetchApiStatus = () => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/status`,
            {
                timeout: C.WINNOW_API_TIMEOUT
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};
