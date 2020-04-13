import React, {Fragment, useEffect, useState} from "react";
import {Form, Button} from "react-bootstrap";
import Select from "react-select";
import {fetchGenes, fetchSearchResults} from "../../service/ApiService";
import SearchResultsDisplay from "../common/SearchResultsDisplay";
import SearchTermUploader from "../common/SearchTermUploader";
import PageLoader from "../common/PageLoader";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBan} from "@fortawesome/free-solid-svg-icons";
import {addSearchHistory} from "../../service/SearchService";
import {MeshtermTree} from "../mesh/MeshtermTree";

/**
 * ComboSearchTab builds the content for Gene Search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function ComboSearchTab(props) {

    const [selectedGenes, setSelectedGenes] = useState([]);
    const [useBatch, setUseBatch] = useState(false);
    const [isLoaded, setIsLoaded] = useState(false);
    const [isMenuLoaded, setIsMenuLoaded] = useState(false);
    const [geneData, setGeneData] = useState([]);
    const [checkedTerms, setCheckedTerms] = useState([]);
    const [haveResults, setHaveResults] = useState(false);
    const [resultData, setResultData] = useState('');

    const getChecked = (checkedNodes) => {
        setCheckedTerms(checkedNodes);
        console.info(`ComboSearchTab checked from MeshtermTree(${checkedNodes.length}): ${JSON.stringify(checkedNodes)}`)
    };

    useEffect(() => {
        if (!haveResults) {
            setIsLoaded(true);
            setIsMenuLoaded(true);
        } else {
            /* fetchSearchResults() */
            let search = {
                searchQuery: {
                    geneId: selectedGenes,
                    symbol: [],
                    description: [],
                    meshId: [],
                    meshTreeId: checkedTerms,
                    name: []
                },
            }
            addSearchHistory(search);
            fetchSearchResults(search)
                .then(res => {
                    setResultData(res);
                    setIsLoaded(true);
                }).catch(err => {
                setIsLoaded(true);
            });
        }
    }, [haveResults, selectedGenes, checkedTerms]);

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

    function returnToSelection() {
        setSelectedGenes([]);
        setHaveResults(false);
    }

    if (isLoaded) {
        if (!haveResults) {
            if (!useBatch) {
                console.info(`ComboSearchTab selected: ${JSON.stringify(selectedGenes)}`);
                return (
                    <div>
                        <div className="button-bar">
                            <Button onClick={toggleBatch} variant="info" size="sm">Batch Import</Button>
                            <Button
                                onClick={executeSearch}
                                variant="info"
                                size="sm"
                                disabled={selectedGenes.length === 0 && checkedTerms.length === 0}
                            >Search</Button>
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
                                        console.info(`ComboSearchTab onInputChange: ${JSON.stringify(e)}`);
                                        if (e.length >= 2) {
                                            partialSearch(e);
                                        }
                                    }}
                                    onChange={e => {
                                        console.info(`ComboSearchTab onChange: ${JSON.stringify(e)}`);
                                        if (e !== null) {
                                            e.forEach((val, index) => {
                                                setSelectedGenes([...selectedGenes, val.value]);
                                            })
                                        } else {
                                            setSelectedGenes([])
                                        }
                                        setGeneData([]);
                                    }}
                                    options={geneData}
                                />
                            </Fragment>
                        </Form>
                        <Fragment>
                            <div id="meshterm-tree">
                                <MeshtermTree callback={getChecked}/>
                            </div>
                        </Fragment>
                    </div>
                );
            } else {
                return (
                    <div>
                        <div className="button-bar">
                            <Button onClick={toggleBatch} variant="info" size="sm">Selector</Button>
                            <Button onClick={null} variant="info" size="sm">Search</Button>
                        </div>
                        <SearchTermUploader data={null} qType="gene"/>
                    </div>
                )
            }
        } else {
            return (
                <SearchResultsDisplay resData={resultData} history={returnToSelection}/>
            )
        }
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default ComboSearchTab;
