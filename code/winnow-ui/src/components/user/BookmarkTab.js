import React, {useState} from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlay, faShareAlt, faTimes} from "@fortawesome/free-solid-svg-icons";
import {fetchSearchResults, fetchUserBookmarks, removeUserBookmark} from "../../service/ApiService";
import {Alert} from "react-bootstrap";
import PageLoader from "../common/PageLoader";
import SearchResultsDisplay from "../common/SearchResultsDisplay";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import 'react-bootstrap-table2-toolkit/dist/react-bootstrap-table2-toolkit.min.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';
import 'react-bootstrap-table2-paginator/dist/react-bootstrap-table2-paginator.min.css';
import * as C from "../../constants";

/**
 * BookmarkTab builds the content for user's saved search lists.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function BookmarkTab(props) {

    const [isLoaded, setIsLoaded] = useState(false);
    const [bookmarkData, setBookmarkData] = useState([]);
    const [deleteBookmark, setDeleteBookmark] = useState(null);
    const [haveResults, setHaveResults] = useState(false);
    const [resultData, setResultData] = useState('');
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');

    React.useEffect(() => {
        let mounted = true;
        if (!haveResults) {
            if (deleteBookmark) {
                removeUserBookmark(deleteBookmark)
                    .then(() => {
                        if (mounted) {
                            setDeleteBookmark(null);
                        }
                    })
                    .catch(() => {
                        setError(`An error occurred while trying to delete bookmark #${deleteBookmark}`)
                        setAlertType('danger')
                    });
            }
            fetchUserBookmarks()
                .then(res => {
                    if (mounted) {
                        setBookmarkData(res);
                        setIsLoaded(true);
                    }
                }).catch(() => {
                setError(`An error occurred while retrieving your bookmarks.`)
                setAlertType('danger')
                setIsLoaded(true);
            });
        }
        return () => {
            mounted = false
        };
    }, [deleteBookmark, haveResults]);

    /* Set up our table options and custom formatting */
    const columns = [
        {
            dataField: 'searchId',
            hidden: true
        },
        {
            dataField: 'searchName',
            text: 'Label',
            title: (cell, row) => {
                return row.searchId
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
            dataField: 'createdDate',
            text: 'Created',
            sort: true,
            type: 'date',
            formatter: (cell, row) => {
                return new Intl.DateTimeFormat("en-US", {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                    hour12: false,
                    hour: "numeric",
                    minute: "numeric",
                    second: "numeric",
                    timeZoneName: "short"
                }).format(new Date(Date.parse(row.createdDate)))

            }
        },
        {
            dataField: 'action',
            isDummyField: true,
            text: 'Action',
            align: 'center',
            headerAlign: 'center',
            formatter: (cell, row) => {
                return (
                    <span>
                    <FontAwesomeIcon
                        className="searchActions"
                        icon={faPlay}
                        color="darkgreen"
                        title="Execute Search"
                        onClick={() => {
                            executeSearch(row)
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
                        setDeleteBookmark(row.searchId)
                    }}
                />
                </span>
                )
            }
        }
    ]

    const {SearchBar} = Search;

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
                console.debug(`BookmarkTab: executeSearch Error: ${err}`)
                setError("Search failed with fatal error.");
                setAlertType('danger');
                setIsLoaded(true);
                setHaveResults(false);
            });
    }

    /* Displays User's saved bookmarks, if any */
    if (isLoaded) {
        if (!haveResults) {
            return (
                <div>
                    <Alert variant={alertType} show={error.length > 0}>
                        {error}
                    </Alert>
                    <ToolkitProvider
                        keyField='searchId'
                        data={bookmarkData}
                        columns={columns}
                        search={{
                            searchFormatted: true
                        }}
                    >
                        {
                            props => (
                                <div>
                                    <SearchBar {...props.searchProps} placeholder="Search bookmarks..."/>
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

export default BookmarkTab;
