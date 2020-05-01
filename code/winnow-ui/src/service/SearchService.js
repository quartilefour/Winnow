/**
 * The SearchService manages the current session's search history and other session states.
 *
 * The search history is base64 encoded to save space and stored in a browser session variable.
 */
import {SS_SH, SS_SI, QUERY_FORMATS as QF} from "../constants";

/**
 * Creates initial session search history.
 */
export const createSearchHistory = () => {
    sessionStorage.setItem(
        SS_SH,
        Buffer.from(
            JSON.stringify([])
        ).toString("base64"));
}

/**
 * Retrieves the session search history. Creates initial history if not found.
 *
 * @return {any}
 */
export const getSearchHistory = () => {
    if (sessionStorage.getItem(SS_SH) === null) {
        createSearchHistory();
    }
    return JSON.parse(
        Buffer.from(
            sessionStorage.getItem(SS_SH),
            "base64"
        ).toString("ascii"));
}

/**
 * Retrieves the last search in the session search history.
 *
 * @return {{}|any}
 */
export const getLastSearch = () => {
    return getSearchHistory().pop();
}

/**
 * Adds search to the end of the session search history.
 *
 * @param search - JSON search object.
 * @param isFile - boolean (do not add to search history if input is file)
 */
export const addSearchHistory = (search, isFile) => {
    if (!isFile) {
        let searchHistory = getSearchHistory();
        searchHistory.push(search);
        sessionStorage.setItem(
            SS_SH,
            Buffer.from(
                JSON.stringify(searchHistory)
            ).toString("base64"));
    }
}

/**
 * Removes a search from the session search history.
 *
 * @param index - Search to remove.
 */
export const removeSearchHistory = (index) => {
    let searchHistory = getSearchHistory();
    searchHistory.splice(index, 1);
    sessionStorage.setItem(
        SS_SH,
        Buffer.from(
            JSON.stringify(searchHistory)
        ).toString("base64"));
}


/**
 * Retrieves the search input type, true for batch or false for selectors.
 *
 * @return {boolean}
 */
export const getBatch = () => {
    let useBatch = sessionStorage.getItem(
        SS_SI
    )
    if (useBatch === null) {
        setBatch(false);
        return false;
    }
    return JSON.parse(useBatch);
}

/**
 * Sets the search input type, true for batch or false for selectors.
 *
 * @param boolean
 */
export const setBatch = (boolean) => {
    sessionStorage.setItem(
        SS_SI,
        boolean
    )
}

/**
 * Prepares a batch search query for posting to the API. Formats data for fetchSearchResults() call.
 *
 * @param queryFormat
 * @param query
 * @return {{searchQuery: {symbol: [], geneId: [], meshTreeId: [], name: [], description: [], meshId: []}}}
 */
export const prepareSearchQuery = (queryFormat, query) => {
    const {
        GENE_ID,
        GENE_SYM,
        GENE_DESC,
        MESH_ID,
        MESH_TREEID,
        MESH_NAME
    } = QF;

    /* Parse batch import textarea query */
    query = query
        .split("\n")
        .filter((term) => {
                return term.trim()
            }
        );

    let data = {
        searchQuery: {
            geneId: [],
            symbol: [],
            description: [],
            meshTreeId: [],
            meshId: [],
            name: []
        }
    }
    switch (queryFormat) {
        case GENE_ID.value:
            data.searchQuery.geneId = query
            break
        case GENE_SYM.value:
            data.searchQuery.symbol = query
            break
        case GENE_DESC.value:
            data.searchQuery.description = query
            break
        case MESH_ID.value:
            data.searchQuery.meshId = query
            break
        case MESH_TREEID.value:
            data.searchQuery.meshTreeId = query
            break
        case MESH_NAME.value:
            data.searchQuery.name = query
            break
        default:
            data = null
    }
    return data;
}
