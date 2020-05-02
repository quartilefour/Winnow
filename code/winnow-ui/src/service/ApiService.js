/**
 * The ApiService provides access to all the Winnow API calls except
 * those related to authentication/authorization and user management.
 */
import axios from 'axios';
import {getAuthHeader} from "./AuthService";
import {
    API_RESOURCES,
    NCBI_API_BASE,
    NCBI_API_TOKEN,
    WINNOW_API_BASE_URL,
    WINNOW_API_TIMEOUT
} from "../constants";

/**
 * callAPI uses the resource parameter to configure the API URL endpoint and
 * options to passed to the axios AJAX request.
 *
 * @param resource - API resource endpoint
 * @param data - Data to be passed to the API
 * @return {Promise<>}
 */
export const callAPI = (resource, data = '') => {
    const {
        WATERMARK,
        DELETE_BOOKMARKS,
        GET_BOOKMARKS,
        POST_BOOKMARKS,
        GET_GENES,
        GET_GENE_DETAIL,
        GET_ARTICLES,
        GET_MESH_CAT,
        GET_MESH_PARENT,
        GET_MESH_NODE,
        POST_QUERY,
        POST_QUERY_FILE,
        GET_API_STATUS,
        NCBI_GENE_DETAIL,
    } = API_RESOURCES;

    let method;
    let url = `${WINNOW_API_BASE_URL}`;

    switch (resource) {
        case GET_API_STATUS: /* Determines if the API is available. */
            method = 'get'
            url = `${url}/status`
            data = ''
            break
        case DELETE_BOOKMARKS: /* Deletes a User's saved bookmark. */
            method = 'delete'
            url = `${url}/bookmarks/${data}`
            data = ''
            break
        case GET_BOOKMARKS: /* Retrieves User bookmarks. */
            method = 'get'
            url = `${url}/bookmarks`
            data = ''
            break
        case POST_BOOKMARKS: /* Saves a User's search as a bookmark. */
            method = 'post'
            url = `${url}/bookmarks`
            break
        case GET_GENES: /* Retrieves genes matching `partial` search term. */
            method = 'get'
            url = `${url}/genes/search/${data}`
            data = ''
            break
        case GET_GENE_DETAIL: /* Retrieves details associated with a given gene. */
            method = 'post'
            url = `${url}/genes/`
            data = {geneId: data}
            break
        case GET_ARTICLES: /* Retrieves PubMed articles associated with provided Gene/MeSH terms. */
            method = 'post'
            url = `${url}/publications`
            break
        case GET_MESH_CAT: /* Retrieves top level MeSH term categories. */
            method = 'get'
            url = `${url}/meshterms/category`
            data = ''
            break
        case GET_MESH_PARENT: /* Retrieves MeSH term tree one level for given parent node. */
            method = 'get'
            url = `${url}/meshterms/tree/parentid/${data}`
            data = ''
            break
        case GET_MESH_NODE: /* Retrieves MeSH term tree node. */
            method = 'get'
            url = `${url}/meshterms/tree/nodeid/${data}`
            data = ''
            break
        case POST_QUERY: /* Retrieves search results for selected Genes/MeSH terms. */
            method = 'post'
            url = `${url}/search`
            break
        case POST_QUERY_FILE: /* Retrieves search results for file upload. */
            method = 'post'
            url = `${url}/search/upload`
            break
        case NCBI_GENE_DETAIL: /* Retrieves details for a given gene from the NCBI website. */
            method = 'get'
            url = `${NCBI_API_BASE}/esummary.fcgi?db=gene&retmode=json&api_key=${NCBI_API_TOKEN}&id=${data}`
            data = ''
            break
        default:
    }

    return axios({
        method: method,
        url: url,
        data: data,
        headers: (resource < WATERMARK) ? getAuthHeader() : {},
        timeout: (resource < WATERMARK) ? 0 : WINNOW_API_TIMEOUT
    });
}

/**
 * Tries to return meaningful error messages from .catch of Axios calls to our API.
 *
 * https://kapeli.com/cheat_sheets/Axios.docset/Contents/Resources/Documents/index
 *
 * @param error - Error caught in .catch
 * @return {string}
 */
export const parseAPIError = (error) => {
    if (error.response) {
        /* The request was made and the server responded with a status code
           that falls out of the range of 2xx */
        return `${error.response.status}: ${error.response.data}`
    } else if (error.request) {
        /* The request was made but no response was received
           `error.request` is an instance of XMLHttpRequest in the browser and an instance of
           http.ClientRequest in node.js */
        return `${error.request.toString()}`
    } else {
        /* Something happened in setting up the request that triggered an Error */
        return `${error.message}`
    }
}