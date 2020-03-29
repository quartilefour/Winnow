import React, {Fragment, useState} from "react";
import {Button, Form} from "react-bootstrap";
import {MeshtermTree} from "../components/MeshtermTree";

/**
 * Mesh2GeneTab builds the content for MeSH term Search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Mesh2GeneTab(props) {
    const [checkedTerms,setCheckedTerms] = useState([]);

    const getChecked = (checkedNodes) => {
       setCheckedTerms(checkedNodes);
        console.info(`Mesh2Gene checked(${checkedNodes.length}): ${JSON.stringify(checkedNodes)}`)
    };

    return (
        <div>
        <Form>
            <Button>Search</Button>
            <Button>Save</Button>
        </Form>
            <Fragment>
                <MeshtermTree callback={getChecked}/>
            </Fragment>
        </div>
    )
}

export default Mesh2GeneTab;
