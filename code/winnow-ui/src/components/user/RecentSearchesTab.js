import React, {useState} from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlay, faShareAlt, faTimes} from "@fortawesome/free-solid-svg-icons";
import {Alert} from "react-bootstrap";
import PageLoader from "../common/PageLoader";
import {getSearchHistory, removeSearchHistory} from "../../service/SearchService";
import SearchResultsDisplay from "../common/SearchResultsDisplay";
import {fetchSearchResults, parseAPIError} from "../../service/ApiService";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import 'react-bootstrap-table2-toolkit/dist/react-bootstrap-table2-toolkit.min.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';
import 'react-bootstrap-table2-paginator/dist/react-bootstrap-table2-paginator.min.css';
import * as C from "../../constants";

/**
 * RecentSearchesTab builds the content for user's saved search lists.
 *
 * @returns {*}
 * @constructor
 */
function RecentSearchesTab() {

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

    /* Set up our table options and custom formatting */
    const columns = [
        {
            dataField: 'index',
            text: '#',
            style: {
                width: '20px'
            },
            formatter: (cell, row) => {
                return row.index + 1;
            },
            headerStyle: () => {
                return { width: "10%" };
            },
            sort: true
        },
        {
            dataField: 'searchQuery',
            text: 'Terms',
            style: {
                width: "auto",
                maxWidth: "400px",
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
            },
            formatter: (cell, row) => {
                return JSON.stringify(row.searchQuery)
            },
            title: (cell, row) => {
                return JSON.stringify(row.searchQuery)
            }
        },
        {
            dataField: 'action',
            isDummyField: true,
            text: 'Action',
            align: 'center',
            headerAlign: 'center',
            headerStyle: () => {
                return { width: "20%" };
            },
            formatter: (cell, row) => {
                return (
                    <span>
                    <FontAwesomeIcon
                        className="searchActions"
                        icon={faPlay}
                        color="darkgreen"
                        title="Execute Search"
                        onClick={() => {
                            executeSearch(row.searchQuery)
                        }}
                    />
                <FontAwesomeIcon
                    className="searchActions"
                    icon={faShareAlt}
                    color="cornflowerblue"
                    title="Share Search"
                />
                <FontAwesomeIcon
                    className="searchActions"
                    icon={faTimes}
                    color="maroon"
                    title="Delete Search"
                    onClick={() => {
                        setRemoveSearch(row.index)
                    }}
                />
                </span>
                )
            }
        }
    ]
    const {SearchBar} = Search;

    /* Submits search criteria to API */
    function executeSearch(searchQuery) {
        setIsLoaded(false);
        fetchSearchResults(searchQuery)
            .then(res => {
                setResultData(res);
                setHaveResults(true);
                setIsLoaded(true);
            })
            .catch(error => {
                console.debug(`RecentSearchesTab: executeSearch Error: ${error}`)
                setError(`Search failed with fatal error.\n${parseAPIError(error)}`);
                setAlertType('danger');
                setIsLoaded(true);
                setHaveResults(false);
            });
    }

    /* Displays user's recent session searches */
    if (isLoaded) {
        if (!haveResults) {
            return (
                <div>
                    <ToolkitProvider
                        keyField='index'
                        data={searchHistory}
                        columns={columns}
                        search={{
                            searchFormatted: true
                        }}
                    >
                        {
                            props => (
                                <div>
                                    <SearchBar {...props.searchProps} placeholder="Search recent..."/>
                                    <BootstrapTable
                                        {...props.baseProps}
                                        pagination={
                                            paginationFactory(C.T2_POPTS)
                                        }
                                        bootstrap4
                                        striped
                                        condensed
                                        hover
                                    />
                                </div>
                            )
                        }
                    </ToolkitProvider>
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
