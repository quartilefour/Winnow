import React, {Fragment, useState} from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlay, faShareAlt, faTimes} from "@fortawesome/free-solid-svg-icons";
import {fetchSearchResults, fetchUserBookmarks, removeUserBookmark} from "../../service/ApiService";
import {Alert, Form, Table} from "react-bootstrap";
import PageLoader from "../common/PageLoader";
import SearchResultsDisplay from "../common/SearchResultsDisplay";

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
                    .catch(err => {
                        /* TODO: Should display/flash error message */
                    });
            }
            fetchUserBookmarks()
                .then(res => {
                    if (mounted) {
                        setBookmarkData(res);
                        setIsLoaded(true);
                    }
                }).catch(err => {
                setIsLoaded(true);
            });
        }
        return () => {
            mounted = false
        };
    }, [deleteBookmark, haveResults]);

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
                    <Fragment>
                        <Form>
                            <Table size="sm" striped bordered hover>
                                <thead>
                                <tr>
                                    <th>Label</th>
                                    <th>Terms</th>
                                    <th>Created</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                {bookmarkData.map((bookmark) => {
                                    return (
                                        <tr key={bookmark.searchId}>
                                            <td>{bookmark.searchName}</td>
                                            <td
                                                style={{
                                                    width: "auto",
                                                    maxWidth: "400px",
                                                    overflow: "hidden",
                                                    textOverflow: "ellipsis",
                                                    whiteSpace: "nowrap",
                                                }}
                                                title={JSON.stringify(bookmark.searchQuery)}
                                            >
                                                {JSON.stringify(bookmark.searchQuery)}
                                            </td>
                                            <td>{new Intl.DateTimeFormat("en-US", {
                                                year: "numeric",
                                                month: "long",
                                                day: "numeric",
                                                hour12: false,
                                                hour: "numeric",
                                                minute: "numeric",
                                                second: "numeric",
                                                timeZoneName: "short"
                                            }).format(new Date(Date.parse(bookmark.createdDate)))}</td>
                                            <td>
                                                <FontAwesomeIcon
                                                    className="searchActions"
                                                    icon={faPlay}
                                                    color="darkgreen"
                                                    title="Execute Search"
                                                    onClick={(e) => {
                                                        executeSearch(bookmark)
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
                                                    onClick={(e) => {
                                                        setDeleteBookmark(bookmark.searchId)
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

export default BookmarkTab;
