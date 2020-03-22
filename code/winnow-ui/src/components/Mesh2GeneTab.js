import {Button, Form, Input} from "./HTMLElements";
import React, {Fragment} from "react";
import Select from "react-select";
import {Tab} from "react-bootstrap";

/**
 * Mesh2GeneTab builds the content for MeSH term Search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Mesh2GeneTab(props) {

    let meshData = props.meshData;

    return (
        <Form>
            <Fragment>
                <Form>
                    <Input type="text"/>
                </Form>
                <Form>
                    <Input/>
                </Form>
            </Fragment>
            <Button>Search</Button>
        </Form>
    );
}

export default Mesh2GeneTab;
