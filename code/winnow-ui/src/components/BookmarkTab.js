import React, {Fragment} from "react";
import {Button} from "react-bootstrap";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faEdit, faCaretSquareRight, faShareSquare, faTimesCircle} from "@fortawesome/free-regular-svg-icons";
import {faPlay, faPencilAlt, faShare, faTimes} from "@fortawesome/free-solid-svg-icons";
import {Form} from "./HTMLElements";

/**
 * BookmarkTab builds the content for user's saved search lists.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function BookmarkTab(props) {

    let bookmarkData = props.bookmarkData;
    const count = 5;
    const items = [];

    for (let i = 0; i < count; i++) {
        let stype = (i % 2) ? "Gene" : "MeSH";
        items.push(
            <li>
                Search {i + 1} ({stype})
                <span style={{display: 'inline-block'}}>
                <FontAwesomeIcon icon={faPlay} color="darkgreen"/>
                <FontAwesomeIcon icon={faShare} />
                <FontAwesomeIcon icon={faPencilAlt} />
                <FontAwesomeIcon icon={faTimes} color="maroon"/>
                </span>
            </li>
        )
    }

    return (
        <Form>
            <Fragment>
                <Form>
                    <ul>
                        {items}
                    </ul>
                </Form>
            </Fragment>
        </Form>
    );
}

export default BookmarkTab;
