import {Button, Form} from "./HTMLElements";
import React, {Fragment, useState} from "react";
import Select from "react-select";
import GeneDetail from "./GeneDetail";
import ApiService from "../service/ApiService";

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

    /* Get gene detail information by gene id */
    function getGene() {
        ApiService.getGene(geneId).then(res => {
            if (res.status === 200) {
                console.info(`Gene data: ${res.data}`);
                setGeneDetail(res.data);
            }
        }).catch(error => {
            console.error(`getGene Error: ${error}`);
        })
    }

    return (
        <div>
            <Form>
                <Fragment>
                    <Select
                        defaultValue={geneData[0]}
                        isClearable
                        isSearchable
                        name="gene"
                        onChange={e => {
                            console.info(`Selected gene: ${e.value}`);
                            setGeneId(e.value);
                        }}
                        options={geneData}
                    />
                </Fragment>
                <Button onClick={getGene}>Search</Button>
            </Form>
            <GeneDetail geneDetail={geneDetail}/>
        </div>
    );
}

export default Gene2MeshTab;
