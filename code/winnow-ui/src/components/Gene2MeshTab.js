import React, {Fragment, useEffect, useState} from "react";
import {Form, Button} from "react-bootstrap";
import Select from "react-select";
import {fetchGenes} from "../service/ApiService";

/**
 * Gene2MeshTab builds the content for Gene Search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Gene2MeshTab(props) {

    const [geneDetail, setGeneDetail] = useState([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [geneData, setGeneData] = useState('');
    const [haveResults, setHaveResults] = useState(false);

    useEffect(() => {
        if (!haveResults) {
            fetchGenes()
                .then(res => {
                    let mappedData = res.map((gene, index) => {
                        return {
                            value: gene.geneId,
                            label: gene.symbol
                        };
                    });
                    setGeneData(mappedData);
                    setIsLoaded(true);
                }).catch(err => {
                setIsLoaded(true);
            });
        } else {
            /* fetchSearchResults() */
        }
    }, [haveResults]);

    function executeSearch() {
       setIsLoaded(false);
    }

    if (isLoaded) {
        console.info(`Gene2Mesh selected: ${JSON.stringify(geneDetail)}`);
        return (
            <div>
                <Button>Search</Button>
                <Button>Save</Button>
                <Form>
                    <Fragment>
                        <Select
                            isClearable
                            isSearchable
                            isMulti
                            hideSelectedOptions={true}
                            isLoading={!isLoaded}
                            autoFocus
                            name="gene"
                            onChange={e => {
                                console.info(`Gene2Mesh onChange: ${JSON.stringify(e)}`);
                                e.forEach((val, index) => {
                                    setGeneDetail([...geneDetail,val.value]);
                                })
                            }}
                            options={geneData}
                        />
                    </Fragment>
                </Form>
            </div>
        );
    } else {
        return (
            <div>Loading...</div>
        )
    }
}

export default Gene2MeshTab;
