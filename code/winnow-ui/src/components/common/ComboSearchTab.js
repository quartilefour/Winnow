import React, {Fragment, useState} from "react";
import {Form, Button, Alert} from "react-bootstrap";
import Select from "react-select";
import {fetchGenes, fetchSearchResults, parseAPIError} from "../../service/ApiService";
import SearchResultsDisplay from "../common/SearchResultsDisplay";
import SearchTermUploader from "../common/SearchTermUploader";
import PageLoader from "../common/PageLoader";
import {addSearchHistory, getLastSearch, getBatch, setBatch, prepareSearchQuery} from "../../service/SearchService";
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

    const [activateSearch, setActivateSearch] = useState(false);
    const [selectedGenes, setSelectedGenes] = useState([]);
    const [useBatch, setUseBatch] = useState(false);
    const [isLoaded, setIsLoaded] = useState(false);
    const [isMenuLoaded, setIsMenuLoaded] = useState(false);
    const [geneData, setGeneData] = useState([]);
    const [checkedTerms, setCheckedTerms] = useState([]);
    const [batchData, setBatchData] = useState({});
    const [batchQueryFormat, setBatchQueryFormat] = useState(null);
    const [isFile, setIsFile] = useState(false);
    const [haveResults, setHaveResults] = useState(false);
    const [resultData, setResultData] = useState('');
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');
    const [submitText, setSubmitText] = useState('Search');

    React.useEffect(() => {
        console.info(`ComboSearchTab: haveResults = ${haveResults}`)
        if (!haveResults) {
            setUseBatch(getBatch);
            setIsLoaded(true);
            setIsMenuLoaded(true);
            if (!useBatch && (selectedGenes.length > 0 || checkedTerms.length > 0)) {
                setActivateSearch(true);
                console.info(`ComboSearchTab: !haveResults: activeSearch: ${true}`)
            }
        } else {
            if (resultData === '') { /* data is not loaded */
                let search
                if (!useBatch) { /* Dropdown & Checkboxes */
                    search = {
                        searchQuery: {
                            geneId: selectedGenes,
                            symbol: [],
                            description: [],
                            meshId: [],
                            meshTreeId: checkedTerms,
                            name: []
                        },
                    }
                } else { /* Batch import */
                    console.info(`ComboSearch: batchQuery mode`)
                    if (isFile) { /* File upload */
                        console.info(`ComboSearch: batchQuery mode: have file: ${batchQueryFormat} - ${batchData.name}`)
                        setSubmitText('Upload');
                        const data = new FormData()
                        data.append('file', batchData)
                        data.append('type', batchQueryFormat)
                        search = data;
                    } else { /* textarea */
                        console.info(`ComboSearch: batchQuery: ${batchQueryFormat} - ${batchData}`)
                        search = prepareSearchQuery(batchQueryFormat, batchData);
                    }
                }
                console.debug(`ComboSearchTab: calling fetchSearchResults`)
                addSearchHistory(search, isFile);
                fetchSearchResults(search, isFile)
                    .then(res => {
                        setResultData(res);
                        setSelectedGenes([]);
                        setCheckedTerms([]);
                        sessionStorage.removeItem('mtt');
                        setIsLoaded(true);
                    })
                    .catch(error => {
                        console.debug(`ComboSearchTab: fetchSearchResults Error: ${error}`)
                        setHaveResults(false);
                        setError(`Search failed with fatal error.\n${parseAPIError(error)}`);
                        setAlertType('danger');
                        setIsLoaded(true);
                    });
            }
        }
    }, [haveResults, selectedGenes, checkedTerms, useBatch, batchData, batchQueryFormat, isFile, resultData]);

    /* Populates predictive dropdown with partial search results from API */
    function partialSearch(pattern) {
        fetchGenes(pattern)
            .then(res => {
                let mappedData = res.map((gene) => {
                    return {
                        value: gene.geneId,
                        label: `${gene.geneId} | ${gene.symbol} | ${gene.description}`
                    };
                });
                setGeneData(mappedData);
            })
            .catch(() => {
                setGeneData([]);
            });
    }

    /* Submits search criteria to API */
    function executeSearch() {
        setIsLoaded(false);
        setHaveResults(true);
    }

    /* Toggles GUI selection and bulk imports for searches */
    function toggleBatch() {
        setBatch(!useBatch);
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
        console.info(`ComboSearchTab: getChecked: ${JSON.stringify(checkedTerms)}`);
    };

    /* Callback for enabling search button from Batch */
    const batchSearchState = (searchState) => {
        setActivateSearch(searchState)
    }

    /* Callback for processing batch input */
    const batchSearch = (batchFormat, batchData, haveFile = false) => {
        setBatchData(batchData)
        setBatchQueryFormat(batchFormat)
        setIsFile(haveFile)
        if (haveFile) {
            setSubmitText('Upload')
        }
    }

    if (isLoaded) {
        if (!haveResults) {
            if (!useBatch) { /* Displays Gene Dropdown & Meshterm Checkboxes */
                return (
                    <div>
                        <Alert variant={alertType} show={error.length > 0} dismissible={true}>{error}</Alert>
                        <div className="button-bar">
                            <Button onClick={toggleBatch} variant="info" size="sm">Batch Import</Button>
                            <Button
                                onClick={executeSearch}
                                variant="info"
                                size="sm"
                                disabled={!activateSearch}
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
                                            e.forEach((val) => {
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
            } else { /* Display batch import textarea and file upload */
                return (
                    <div>
                        <Alert variant={alertType} show={error.length > 0} dismissible={true}>{error}</Alert>
                        <div className="button-bar">
                            <Button onClick={toggleBatch} variant="info" size="sm">Selector</Button>
                            <Button
                                onClick={executeSearch}
                                variant="info"
                                size="sm"
                                disabled={!activateSearch}
                            >{submitText}</Button>
                        </div>
                        <SearchTermUploader active={useBatch} update={batchSearch} searchable={batchSearchState}/>
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
