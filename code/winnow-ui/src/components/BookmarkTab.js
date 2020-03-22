import {Button, Form, Input} from "./HTMLElements";
import React, {Fragment} from "react";
import Select from "react-select";
import {Tab} from "react-bootstrap";

/**
 * BookmarkTab builds the content for user's saved search lists.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function BookmarkTab(props) {

    let bookmarkData = props.bookmarkData;

    return (
        <Form>
            <Fragment>
                <Form>
                    <Input/>
                </Form>
            </Fragment>
        </Form>
    );
}

export default BookmarkTab;
