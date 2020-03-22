import {Button, Form} from "./AuthForm";
import React, {Fragment} from "react";
import Select from "react-select";

/**
 * Gene2MeshTab builds the content for Gene Search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Gene2MeshTab(props) {

    let geneData = props.geneData;

    return (
        <Form>
            <Fragment>
                <Select
                    defaultValue={geneData[0]}
                    isClearable
                    isSearchable
                    name="gene"
                    options={geneData}
                />
            </Fragment>
            <Button>Search</Button>
        </Form>
    );
}

export default Gene2MeshTab;
