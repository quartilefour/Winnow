import React, {Fragment, useEffect, useState} from "react";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlay, faTimes} from "@fortawesome/free-solid-svg-icons";
import {Form, Table} from "react-bootstrap";
import PageLoader from "../common/PageLoader";
import {getSearchHistory, removeSearchHistory} from "../../service/SearchService";

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

    useEffect(() => {
        if (removeSearch !== null) {
           removeSearchHistory(removeSearch);
           setRemoveSearch(null);
        }
        setSearchHistory(getSearchHistory());
        setIsLoaded(true);
    }, [removeSearch, setSearchHistory]);

    if (isLoaded) {
        return (
            <div>
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
                                console.info(`Looping through searches: ${JSON.stringify(search)}`);
                                return (
                                    <tr key={index}>
                                        <td>{index+1}</td>
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
                                            />
                                            <FontAwesomeIcon
                                                className="searchActions"
                                                icon={faTimes}
                                                color="maroon"
                                                title="Delete Search"
                                                onClick={(e) => {
                                                    console.info(`Clicked delete on bookmark #${index} `);
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
        return (
            <div><PageLoader/></div>
        )
    }
}

export default RecentSearchesTab;
