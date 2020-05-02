import React, {useState} from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlay, faShareAlt, faTimes} from "@fortawesome/free-solid-svg-icons";
import {callAPI, parseAPIError} from "../../service/ApiService";
import {Alert} from "react-bootstrap";
import PageLoader from "../common/PageLoader";
import Moment from 'react-moment';
import 'moment-timezone';
import SearchResultsDisplay from "../common/SearchResultsDisplay";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import {prettySearch} from "../../service/SearchService";
import 'react-bootstrap-table2-toolkit/dist/react-bootstrap-table2-toolkit.min.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';
import 'react-bootstrap-table2-paginator/dist/react-bootstrap-table2-paginator.min.css';
import {API_RESOURCES, T2_POPTS} from "../../constants";

/**
 * BookmarkTab builds the content for user's saved search lists.
 *
 * @returns {*}
 * @constructor
 */
function BookmarkTab() {
    const {DELETE_BOOKMARKS, GET_BOOKMARKS, POST_QUERY} = API_RESOURCES

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
                callAPI(DELETE_BOOKMARKS, deleteBookmark)
                    .then(() => {
                        if (mounted) {
                            setDeleteBookmark(null);
                        }
                    })
                    .catch((error) => {
                        setError(`An error occurred while trying to delete bookmark #${deleteBookmark}\n/ 
                        ${parseAPIError(error)}`)
                        setAlertType('danger')
                    });
            }
            callAPI(GET_BOOKMARKS)
                .then(res => {
                    if (mounted) {
                        setBookmarkData(res.data);
                        setIsLoaded(true);
                    }
                })
                .catch((error) => {
                    setError(`An error occurred while retrieving your bookmarks.\n${parseAPIError(error)}`)
                    setAlertType('danger')
                    setIsLoaded(true);
                });
        }
        return () => {
            mounted = false
        };
    }, [DELETE_BOOKMARKS, GET_BOOKMARKS, deleteBookmark, haveResults]);

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
            headerStyle: () => {
                return {
                    width: "20%",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    whiteSpace: "nowrap",
                };
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
                return prettySearch(row.searchQuery)
            },
            title: (cell, row) => {
                return prettySearch(row.searchQuery)
            }
        },
        {
            dataField: 'createdDate',
            text: 'Created',
            sort: true,
            type: 'date',
            headerStyle: () => {
                return {width: "20%"};
            },
            formatter: (cell, row) => {
                return <Moment
                    date={row.createdDate}
                    format="YYYY-MMM-DD HH:MM:SS"
                    interval={0}
                />
            }
        },
        {
            dataField: 'action',
            isDummyField: true,
            text: 'Action',
            align: 'center',
            headerStyle: () => {
                return {width: "15%"};
            },
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
        callAPI(POST_QUERY, searchQuery)
            .then(res => {
                setResultData(res.data);
                setHaveResults(true);
                setIsLoaded(true);
            })
            .catch(error => {
                console.debug(`BookmarkTab: executeSearch Error: ${error}`)
                setError(`Search failed with fatal error.\n${parseAPIError(error)}`);
                setAlertType('danger');
                setIsLoaded(true);
                setHaveResults(false);
            });
    }

    /* Displays User's saved bookmarks, if any */
    if (isLoaded) {
        if (!haveResults) return (
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
                                            paginationFactory(T2_POPTS)
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
        )
        return ( /* Display search results retrieved from API */
            <div>
                <Alert variant={alertType} show={error.length > 0}>
                    {error}
                </Alert>
                <SearchResultsDisplay resultData={resultData} history={returnToSelection}/>
            </div>
        )
    } else {
        return (<div><PageLoader/></div>)
    }
}

export default BookmarkTab;
