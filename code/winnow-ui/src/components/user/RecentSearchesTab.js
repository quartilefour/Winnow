import React, {Fragment, useState} from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlay, faTimes} from "@fortawesome/free-solid-svg-icons";
import {Alert, Form, Table} from "react-bootstrap";
import PageLoader from "../common/PageLoader";
import {getSearchHistory, removeSearchHistory} from "../../service/SearchService";
import SearchResultsDisplay from "../common/SearchResultsDisplay";
import {fetchSearchResults} from "../../service/ApiService";

/**
 * RecentSearchesTab builds the content for user's saved search lists.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function RecentSearchesTab(props) {

    const [isLoaded, setIsLoaded] = useState(false);
    const [searchHistory, setSearchHistory] = useState([]);
    const [removeSearch, setRemoveSearch] = useState(null);
    const [haveResults, setHaveResults] = useState(false);
    const [resultData, setResultData] = useState('');
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');

    React.useEffect(() => {
        if (!haveResults) {
            if (removeSearch !== null) {
                removeSearchHistory(removeSearch);
                setRemoveSearch(null);
            }
            setSearchHistory(getSearchHistory());
            setIsLoaded(true);
        }
    }, [removeSearch, setSearchHistory, haveResults]);

    /* Returns to selection from results display */
    function returnToSelection() {
        setHaveResults(false);
    }

    /* Submits search criteria to API */
    function executeSearch(searchQuery) {
        setIsLoaded(false);
        fetchSearchResults(searchQuery)
            .then(res => {
                setResultData(res);
                setHaveResults(true);
                setIsLoaded(true);
            })
            .catch(err => {
                console.debug(`RecentSearchesTab: executeSearch Error: ${err}`)
                setError("Search failed with fatal error.");
                setAlertType('danger');
                setIsLoaded(true);
                setHaveResults(false);
            });
    }

    /* Displays user's recent session searches */
    if (isLoaded) {
        if (!haveResults) {
            return (
                <div id="recent-search-div">
                    <Fragment>
                        <Form>
                            <Table size="sm" striped bordered hover>
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Terms</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                {searchHistory.map((search, index) => {
                                    return (
                                        <tr key={index}>
                                            <td>{index + 1}</td>
                                            <td
                                                style={{
                                                    width: "auto",
                                                    maxWidth: "550px",
                                                    overflow: "hidden",
                                                    textOverflow: "ellipsis",
                                                    whiteSpace: "nowrap",
                                                }}
                                            >
                                                {JSON.stringify(search.searchQuery)}
                                            </td>
                                            <td>
                                                <FontAwesomeIcon
                                                    className="searchActions"
                                                    icon={faPlay}
                                                    color="darkgreen"
                                                    title="Execute Search"
                                                    onClick={(e) => {
                                                        executeSearch(search)
                                                    }}
                                                />
                                                <FontAwesomeIcon
                                                    className="searchActions"
                                                    icon={faTimes}
                                                    color="maroon"
                                                    title="Delete Search"
                                                    onClick={(e) => {
                                                        setRemoveSearch(index)
                                                    }}
                                                />
                                            </td>
                                        </tr>
                                    );
                                })}
                                </tbody>
                            </Table>
                        </Form>
                    </Fragment>
                </div>
            );
        } else {
            /* Display search results retrieved from API */
            return (
                <div>
                    <Alert variant={alertType} show={error.length > 0}>
                        {error}
                    </Alert>
                    <SearchResultsDisplay resData={resultData} history={returnToSelection}/>
                </div>
            )
        }
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default RecentSearchesTab;
