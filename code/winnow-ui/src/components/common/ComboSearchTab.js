import React, {forwardRef, useImperativeHandle, useState} from "react";
import {Form, Button, Alert} from "react-bootstrap";
import Select from "react-select";
import {callAPI, parseAPIError} from "../../service/ApiService";
import SearchResultsDisplay from "../common/SearchResultsDisplay";
import SearchTermUploader from "../common/SearchTermUploader";
import PageLoader from "../common/PageLoader";
import {
    addSearchHistory,
    getBatch,
    setBatch,
    prepareSearchQuery,
    clearMeshterm,
} from "../../service/SearchService";
import {MeshtermTree} from "../mesh/MeshtermTree";
import {API_RESOURCES} from "../../constants";

/**
 * ComboSearchTab builds the content for Gene Auto-complete Select and MesH Checkbox tree.
 *
 * Select - https://react-select.com/props
 * Checkbox Tree - See MeshtermTree.js
 *
 * @returns {*}
 * @constructor
 */
const ComboSearchTab = forwardRef((props, ref) => {

    const {GET_GENES, POST_QUERY, POST_QUERY_FILE} = API_RESOURCES;

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

    /* Allows parent Dashboard to reset search when clicking on 'Search' tab */
    useImperativeHandle(ref, () => ({
        returnToSelection
    }))

    React.useEffect(() => {
        if (!haveResults) {
            setUseBatch(getBatch);
            setIsLoaded(true);
            setIsMenuLoaded(true);
            if (!useBatch) {
                setActivateSearch((selectedGenes.length > 0 || checkedTerms.length > 0));
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
                            meshId: checkedTerms,
                            meshTreeId: [],
                            name: []
                        },
                    }
                } else { /* Batch import */
                    if (isFile) { /* File upload */
                        setSubmitText('Upload');
                        const data = new FormData()
                        data.append('file', batchData)
                        data.append('type', batchQueryFormat)
                        search = data;
                    } else { /* textarea */
                        search = prepareSearchQuery(batchQueryFormat, batchData);
                    }
                }
                addSearchHistory(search, isFile);
                callAPI((isFile) ? POST_QUERY_FILE : POST_QUERY, search)
                    .then(res => {
                        if (isFile) { /* For files, add query to searchHistory only after it's been validated. */
                            addSearchHistory(res.data.searchQuery)
                        }
                        setResultData(res.data);
                        setSelectedGenes([]);
                        setCheckedTerms([]);
                        clearMeshterm();
                        setIsLoaded(true);
                    })
                    .catch(error => {
                        setHaveResults(false);
                        setError(`Search failed with fatal error.\n${parseAPIError(error)}`);
                        setAlertType('danger');
                        setIsLoaded(true);
                    });
            }
        }
    }, [
        POST_QUERY,
        POST_QUERY_FILE,
        activateSearch,
        haveResults,
        selectedGenes,
        checkedTerms,
        useBatch,
        batchData,
        batchQueryFormat,
        isFile,
        resultData
    ]);

    /* Populates predictive dropdown with partial search results from API */
    function partialSearch(pattern) {
        callAPI(GET_GENES, pattern)
            .then(res => {
                /* Maps the gene data to the expect attributes used by the Select component */
                let mappedData = res.data.map((gene) => {
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
        setSelectedGenes([])
        setCheckedTerms([])
        setResultData('')
        setActivateSearch(false);
        setHaveResults(false);
    }

    /* Callback for MeshtermTree to send checked MeSH terms */
    const getChecked = (checkedNodes) => {
        setCheckedTerms(checkedNodes);
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
            if (!useBatch)  /* Displays Gene Dropdown & MeSH term Checkboxes */
                return (
                    <div>
                        <Alert variant={alertType} show={error.length > 0} dismissible={true}>{error}</Alert>
                        <div className="button-bar">
                            <Button
                                onClick={executeSearch}
                                variant="info"
                                size="sm"
                                disabled={!activateSearch}
                            >Search</Button>
                            <Button className="btn-search" onClick={toggleBatch}
                                    style={{display: "inline-block", float: "right"}}
                                    variant="info" size="sm">Batch Import</Button>
                        </div>
                        <Form>
                            <>
                                <Select
                                    isClearable
                                    isSearchable
                                    isMulti
                                    hideSelectedOptions={true}
                                    isLoading={!isMenuLoaded}
                                    loadingMessage="Loading..."
                                    placeholder="Enter partial Gene Id, Gene Symbol, or Gene Description"
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
                            </>
                        </Form>
                        <div className="separator">Check MeSH Terms below</div>
                        <>
                            <div id="meshterm-tree">
                                <MeshtermTree callback={getChecked}/>
                            </div>
                        </>
                    </div>
                );
            return ( /* Display batch import textarea and file upload */
                <div>
                    <Alert variant={alertType} show={error.length > 0} dismissible={true}>{error}</Alert>
                    <div className="button-bar">
                        <Button
                            onClick={executeSearch}
                            variant="info"
                            size="sm"
                            disabled={!activateSearch}
                        >{submitText}</Button>
                        <Button onClick={toggleBatch} style={{display: "inline-block", float: "right"}} variant="info"
                                size="sm">Selector</Button>
                    </div>
                    <SearchTermUploader active={useBatch} update={batchSearch} searchable={batchSearchState}/>
                </div>
            )
        } else {
            return ( /* Display search results retrieved from API */
                <SearchResultsDisplay resultData={resultData} history={returnToSelection}/>
            )
        }
    } else {
        return (
            <div><PageLoader message={`Searching ${(selectedGenes.length + checkedTerms.length)} term(s)...`}/></div>
        )
    }
})

export default ComboSearchTab;
