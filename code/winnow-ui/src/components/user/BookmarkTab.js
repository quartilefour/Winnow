import React, {Fragment, useEffect, useState} from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlay, faShareAlt, faTimes} from "@fortawesome/free-solid-svg-icons";
import {fetchUserBookmarks, removeUserBookmark} from "../../service/ApiService";
import {Form, Table} from "react-bootstrap";
import PageLoader from "../common/PageLoader";

/**
 * RecentSearchesTab builds the content for user's saved search lists.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function BookmarkTab(props) {

    const [isLoaded, setIsLoaded] = useState(false);
    const [bookmarkData, setBookmarkData] = useState([]);
    const [deleteBookmark, setDeleteBookmark] = useState(null);

    useEffect(() => {
        let mounted = true;
        if (deleteBookmark) {
            removeUserBookmark(deleteBookmark)
                .then(res => {
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
                setBookmarkData(res);
                setIsLoaded(true);
            }).catch(err => {
            setIsLoaded(true);
        });
        return () => mounted = false;
    }, [deleteBookmark]);

    /* Displays User's saved bookmarks, if any */
    if (isLoaded) {
        return (
            <div>
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
        return (
            <div><PageLoader/></div>
        )
    }
}

export default BookmarkTab;
