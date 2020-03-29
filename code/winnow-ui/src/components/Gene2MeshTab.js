import React, {Fragment, useState} from "react";
import {Form, Button} from "react-bootstrap";
import Select from "react-select";

/**
 * Gene2MeshTab builds the content for Gene Search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Gene2MeshTab(props) {

    const [geneDetail, setGeneDetail] = useState({});
    const [geneId, setGeneId] = useState('');

    let geneData = props.geneData;

    return (
        <div>
            <Button>Search</Button>
            <Button>Save</Button>
            <Form>
                <Fragment>
                    <Select
                        defaultValue={geneData[0]}
                        isClearable
                        isSearchable
                        isMulti
                        autoFocus
                        name="gene"
                        onChange={e => {
                            //setGeneId(e.value);
                        }}
                        options={geneData}
                    />
                </Fragment>
            </Form>
        </div>
    );
}

export default Gene2MeshTab;
