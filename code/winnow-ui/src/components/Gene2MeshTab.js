import React, {Fragment, useEffect, useState} from "react";
import {Form, Button, Spinner} from "react-bootstrap";
import Select from "react-select";
import {fetchGenes, fetchSearchResults} from "../service/ApiService";
import SearchResultsDisplay from "./SearchResultsDisplay";
import SearchTermUploader from "./SearchTermUploader";
import PageLoader from "./common/PageLoader";

/**
 * Gene2MeshTab builds the content for Gene Search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Gene2MeshTab(props) {

    const [geneDetail, setGeneDetail] = useState([]);
    const [useBatch, setUseBatch] = useState(false);
    const [isLoaded, setIsLoaded] = useState(false);
    const [geneData, setGeneData] = useState('');
    const [haveResults, setHaveResults] = useState(false);
    const [resultData, setResultData] = useState('');

    useEffect(() => {
        if (!haveResults) {
            fetchGenes()
                .then(res => {
                    let mappedData = res.map((gene, index) => {
                        return {
                            value: gene.geneId,
                            label: `(${gene.symbol}) ${gene.description}`
                        };
                    });
                    setGeneData(mappedData);
                    setIsLoaded(true);
                }).catch(err => {
                setIsLoaded(true);
            });
        } else {
            /* fetchSearchResults() */
            fetchSearchResults({
                searchQuery: geneDetail,
                queryType: "gene",
                queryFormat: "geneid"
            })
                .then(res => {
                    setResultData(res);
                    setIsLoaded(true);
                }).catch(err => {
                setIsLoaded(true);
            });
        }
    }, [haveResults, geneDetail]);

    function executeSearch() {
        setIsLoaded(false);
        setHaveResults(true);
    }

    function toggleBatch() {
        setUseBatch(!useBatch);
    }

    if (isLoaded) {
        if (!haveResults) {
            if (!useBatch) {
                console.info(`Gene2Mesh selected: ${JSON.stringify(geneDetail)}`);
                return (
                    <div>
                        <Button onClick={toggleBatch}>Batch Import</Button>
                        <Button onClick={executeSearch}>Search</Button>
                        <Form>
                            <Fragment>
                                <Select
                                    isClearable
                                    isSearchable
                                    isMulti
                                    hideSelectedOptions={true}
                                    isLoading={!isLoaded}
                                    loadingMessage={<Spinner animation="border" size="sm" variant="info"/>}
                                    autoFocus
                                    //filterOption={}
                                    name="gene"
                                    onChange={e => {
                                        console.info(`Gene2Mesh onChange: ${JSON.stringify(e)}`);
                                        e.forEach((val, index) => {
                                            setGeneDetail([...geneDetail, val.value]);
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
                    <div>
                        <Button onClick={toggleBatch}>Selector</Button>
                        <Button onClick={null}>Search</Button>
                        <SearchTermUploader data={null}/>
                    </div>
                )
            }
        } else {
            return (
                <SearchResultsDisplay resData={resultData}/>
            )
        }
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default Gene2MeshTab;
