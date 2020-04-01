import React, {Fragment, useEffect, useState} from "react";
import {Form, Button} from "react-bootstrap";
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

    const [selectedGenes, setSelectedGenes] = useState([]);
    const [useBatch, setUseBatch] = useState(false);
    const [isLoaded, setIsLoaded] = useState(false);
    const [isMenuLoaded, setIsMenuLoaded] = useState(false);
    const [geneData, setGeneData] = useState([]);
    const [haveResults, setHaveResults] = useState(false);
    const [resultData, setResultData] = useState('');

    useEffect(() => {
        if (!haveResults) {
            setIsLoaded(true);
            setIsMenuLoaded(true);
        } else {
            /* fetchSearchResults() */
            fetchSearchResults({
                searchQuery: selectedGenes,
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
    }, [haveResults, selectedGenes]);

    function partialSearch(pattern) {
        fetchGenes(pattern)
            .then(res => {
                let mappedData = res.map((gene, index) => {
                    return {
                        value: gene.geneId,
                        label: `(${gene.symbol}) ${gene.description}`
                    };
                });
                setGeneData(mappedData);
            }).catch(err => {
            setGeneData([]);
        });
    }

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
                console.info(`Gene2Mesh selected: ${JSON.stringify(selectedGenes)}`);
                return (
                    <div>
                        <div className="button-bar">
                            <Button onClick={toggleBatch} variant="info" size="sm">Batch Import</Button>
                            <Button onClick={executeSearch} variant="info" size="sm">Search</Button>
                        </div>
                        <Form>
                            <Fragment>
                                <Select
                                    isClearable
                                    isSearchable
                                    isMulti
                                    hideSelectedOptions={true}
                                    isLoading={!isMenuLoaded}
                                    loadingMessage="Loading..."
                                    autoFocus={true}
                                    name="gene"
                                    onInputChange={e => {
                                        console.info(`Gene2Mesh onInputChange: ${JSON.stringify(e)}`);
                                        if (e.length >= 2) {
                                            partialSearch(e);
                                        }
                                    }}
                                    onChange={e => {
                                        console.info(`Gene2Mesh onChange: ${JSON.stringify(e)}`);
                                        if (e !== null) {
                                            e.forEach((val, index) => {
                                                setSelectedGenes([...selectedGenes, val.value]);
                                            })
                                        }
                                        setGeneData([]);
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
                        <div className="button-bar">
                            <Button onClick={toggleBatch} variant="info" size="sm">Selector</Button>
                            <Button onClick={null} variant="info" size="sm">Search</Button>
                        </div>
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
