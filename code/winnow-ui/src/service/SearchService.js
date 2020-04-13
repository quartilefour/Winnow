/**
 * The SearchService manages the current session's search history. The search history is base64 encoded and
 * stored in a browser session variable.
 */

/**
 * Creates initial session search history.
 */
export const createSearchHistory = () => {
    sessionStorage.setItem('searchHistory', Buffer.from(JSON.stringify([])).toString("base64"));
}

/**
 * Retrieves the session search history. Creates initial history if not found.
 *
 * @return {any}
 */
export const getSearchHistory = () => {
    if (sessionStorage.getItem('searchHistory') === null) {
        createSearchHistory();
    }
    console.info(`SearchService getSearchHistory: ${Buffer.from(sessionStorage.getItem('searchHistory'), "base64").toString("ascii")}`);

    return JSON.parse(Buffer.from(sessionStorage.getItem('searchHistory'), "base64").toString("ascii"));
}

/**
 * Retrieves the last search in the session search history.
 *
 * @return {{}|any}
 */
export const getLastSearch = () => {
    console.info(`SearchService getlastSearch: ${JSON.stringify(getSearchHistory().pop())}`);
    return getSearchHistory().pop();
}

/**
 * Adds search to the end of the session search history.
 *
 * @param search - JSON search object.
 */
export const addSearchHistory = (search) => {
    let searchHistory = getSearchHistory();
    console.info(`SearchService addSearchHistory: searchHistory length: ${searchHistory.length}`);
    searchHistory.push(search);
    console.info(`SearchService addSearchHistory: searchHistory length: ${searchHistory.length}`);
    sessionStorage.setItem('searchHistory', Buffer.from(JSON.stringify(searchHistory)).toString("base64"));
}

/**
 * Removes a search from the session search history.
 *
 * @param index - Search to remove.
 */
export const removeSearchHistory = (index) => {
    let searchHistory = getSearchHistory();
    console.info(`SearchService removeSearchHistory: searchHistory length: ${searchHistory.length}`);
    searchHistory.splice(index, 1);
    console.info(`SearchService removeSearchHistory: searchHistory length: ${searchHistory.length}`);
    sessionStorage.setItem('searchHistory', Buffer.from(JSON.stringify(searchHistory)).toString("base64"));
}