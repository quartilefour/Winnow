import React, {Fragment, useEffect, useState} from "react";
import {Form, Button} from "react-bootstrap";
import Select from "react-select";
import {fetchGenes, fetchSearchResults} from "../../service/ApiService";
import SearchResultsDisplay from "../common/SearchResultsDisplay";
import SearchTermUploader from "../common/SearchTermUploader";
import PageLoader from "../common/PageLoader";
import {addSearchHistory, getLastSearch} from "../../service/SearchService";
import {MeshtermTree} from "../mesh/MeshtermTree";

/**
 * ComboSearchTab builds the content for Gene Auto-complete Select and MesH Checkbox tree.
 *
 * Select - https://react-select.com/props
 * Checkbox Tree - See MeshtermTree.js
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

    /* Populates predictive dropdown with partial search results from API */
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

    /* Submits search criteria to API */
    function executeSearch() {
        setIsLoaded(false);
        setHaveResults(true);
    }

    /* Toogles GUI selection and bulk imports for searches */
    function toggleBatch() {
        setUseBatch(!useBatch);
    }

    /* Returns to selection from results display */
    function returnToSelection() {
        let lastSearch = getLastSearch();
        setSelectedGenes(lastSearch.searchQuery.geneId)
        setCheckedTerms(lastSearch.searchQuery.meshTreeId)
        setHaveResults(false);
    }

    /* Callback for MeshtermTree */
    const getChecked = (checkedNodes) => {
        setCheckedTerms(checkedNodes);
    };

    if (isLoaded) {
        if (!haveResults) {
            if (!useBatch) {
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
                                    placeholder="Enter partial Gene Id, Symbol, or Description"
                                    autoFocus={true}
                                    name="gene"
                                    onInputChange={e => {
                                        if (e.length >= 2) {
                                            partialSearch(e);
                                        }
                                    }}
                                    onChange={e => {
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
            /* Display search results retrieved from API */
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
