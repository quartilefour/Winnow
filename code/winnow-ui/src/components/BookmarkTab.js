import React, {Fragment, useEffect, useState} from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faPlay, faShareAlt, faShare, faTimes} from "@fortawesome/free-solid-svg-icons";
import {Form} from "./HTMLElements";
import {fetchUserBookmarks} from "../service/ApiService";
import {Table} from "react-bootstrap";

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

    useEffect(() => {
        /* fetchUserBookmarks() */
        //console.info(`BookmarkTab: ${JSON.stringify(checkedTerms)}`);
        fetchUserBookmarks()
            .then(res => {
                console.info(`Mesh2Gene executed search: ${JSON.stringify(res)}`);
                setBookmarkData(res);
                setIsLoaded(true);
            }).catch(err => {
            setIsLoaded(true);
        });
    }, []);

    const count = 5;
    const items = [];

    for (let i = 0; i < count; i++) {
        let stype = (i % 2) ? "Gene" : "MeSH";
        items.push(
            <li>
                Search {i + 1} ({stype})
                <span style={{display: 'inline-block'}}>
                </span>
            </li>
        )
    }

    if (isLoaded) {
        return (
            <Form>
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
                            {bookmarkData.map((value, index) => {
                                return (
                                    <tr key={index}>
                                        <td>{value.searchName}</td>
                                        <td>{value.queryType.toUpperCase()}</td>
                                        <td>{value.searchQuery.join(', ')}</td>
                                        <td>{value.createdAt}</td>
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
                                            />
                                        </td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </Table>
                    </Form>
                </Fragment>
            </Form>
        );
    } else {
        return (
            <div>Loading...</div>
        )
    }
}

export default BookmarkTab;
