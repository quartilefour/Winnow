import React, {Fragment, useEffect, useState} from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlay, faShareAlt, faTimes} from "@fortawesome/free-solid-svg-icons";
import {fetchUserBookmarks, removeUserBookmark} from "../../service/ApiService";
import {Form, Table} from "react-bootstrap";
import PageLoader from "../common/PageLoader";

/**
 * BookmarkTab builds the content for user's saved search lists.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function BookmarkTab(props) {

    const [isLoaded, setIsLoaded] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const [bookmarkData, setBookmarkData] = useState([]);
    const [deleteBookmark, setDeleteBookmark] = useState(null);

    useEffect(() => {
        setRefresh(props.refresh);
        if (deleteBookmark) {
            removeUserBookmark(deleteBookmark)
                .then(res => {

                    setDeleteBookmark(null);
                })
                .catch(err => {

                });
        }
        fetchUserBookmarks()
            .then(res => {
                console.info(`Fetching user bookmarks: ${JSON.stringify(res)}`);
                setBookmarkData(res);
                setIsLoaded(true);
                //console.info(`Bookmark count: ${bookmarkData.length}`)
            }).catch(err => {
            setIsLoaded(true);
            setRefresh(false);
        });
    }, [deleteBookmark, props]);

    if (isLoaded) {
        return (
            <div>
                <Fragment>
                    <Form>
                        <Table striped bordered hover>
                            <thead>
                            <tr>
                                <th>Label</th>
                                <th>Type</th>
                                <th>Terms</th>
                                <th>Created</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {bookmarkData.map((bookmark) => {
                                console.info(`Looping through bookmarks: ${JSON.stringify(bookmark)}`);
                                return (
                                    <tr key={bookmark.searchId}>
                                        <td>{bookmark.searchName}</td>
                                        <td>{bookmark.queryType.toUpperCase()}</td>
                                        <td>{bookmark.searchQuery.join(', ')}</td>
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
                                                    console.info(`Clicked delete on bookmark #${bookmark.searchId} `);
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