/**
 * The ApiService provides access to all the Winnow API calls except
 * those related to authentication/authorization.
 */
import axios from 'axios';
import * as C from '../constants';
import AuthService from "./AuthService";

export const fetchUserBookmarks = () => {
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/bookmarks`,
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
            `${C.WINNOW_API_BASE_URL}/bookmarks`,
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
            `${C.WINNOW_API_BASE_URL}/bookmarks/${bookmarkId}`,
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
    return new Promise((resolve, reject) => {
        axios.post(
            `${C.WINNOW_API_BASE_URL}/search`,
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
            `${C.WINNOW_API_BASE_URL}/genes/search/${partial}`,
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

export const fetchGeneDetails = (geneId) => {
    return new Promise((resolve, reject) => {
        axios.post(
            `${C.WINNOW_API_BASE_URL}/genes`,
            {geneId: geneId},
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

export const fetchMeshtermCat = () => {
    console.debug(`fetchMeshtermCat: ${C.WINNOW_API_BASE_URL}/meshterms/category`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/meshterms/category`,
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
    console.debug(`fetchMeshtermTree: ${C.WINNOW_API_BASE_URL}/meshterms/tree/parentid/${node}`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/meshterms/tree/parentid/${node}`,
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
    console.debug(`fetchMeshtermNode: ${C.WINNOW_API_BASE_URL}/meshterms/tree/nodeid/${node}`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/meshterms/tree/nodeid/${node}`,
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
            isChecked: node.isChecked,
            meshIndex: `${node.meshIndex}:${index}`,
            meshId: mesh.meshId,
            hasChild: mesh.hasChild,
            name: `${mesh.meshName} [${id}]`,
        };
    });
};

export const fetchPubMedArticleList = (data) => {
    console.info(`fetchPubMedArticleList: ${JSON.stringify(data)}`);
    return new Promise((resolve, reject) => {
        axios.post(
            `${C.WINNOW_API_BASE_URL}/publications`,
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

export const fetchApiStatus = () => {
    console.debug(`fetchApiStatus: ${C.WINNOW_API_BASE_URL}/status`);
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
