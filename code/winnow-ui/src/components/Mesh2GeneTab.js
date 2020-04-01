import React, {Fragment, useEffect, useState} from "react";
import {Button, Form} from "react-bootstrap";
import {MeshtermTree} from "../components/MeshtermTree";
import {fetchSearchResults} from "../service/ApiService";
import SearchResultsDisplay from "./SearchResultsDisplay";
import SearchTermUploader from "./SearchTermUploader";
import PageLoader from "./common/PageLoader";

/**
 * Mesh2GeneTab builds the content for MeSH term Search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Mesh2GeneTab(props) {
    const [isLoaded, setIsLoaded] = useState(false);
    const [useBatch, setUseBatch] = useState(false);
    const [checkedTerms, setCheckedTerms] = useState([]);
    const [haveResults, setHaveResults] = useState(false);
    const [resultData, setResultData] = useState('');

    const getChecked = (checkedNodes) => {
        setCheckedTerms(checkedNodes);
        console.info(`Mesh2Gene checked(${checkedNodes.length}): ${JSON.stringify(checkedNodes)}`)
    };

    useEffect(() => {
        if (!haveResults) {
            setIsLoaded(true);
        } else {
            /* fetchSearchResults() */
            console.info(`Mesh2Gene execute search for: ${JSON.stringify(checkedTerms)}`);
            fetchSearchResults({
                searchQuery: checkedTerms,
                queryType: "mesh",
                queryFormat: "meshid"
            })
                .then(res => {
                    console.info(`Mesh2Gene executed search: ${JSON.stringify(res)}`);
                    setResultData(res);
                    setIsLoaded(true);
                }).catch(err => {
                setIsLoaded(true);
            });
        }
    }, [haveResults, checkedTerms]);

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
                return (
                    <div>
                        <Form>
                            <div className="button-bar">
                                <Button onClick={toggleBatch} variant="info" size="sm">Batch Import</Button>
                                <Button onClick={executeSearch} variant="info" size="sm">Search</Button>
                            </div>
                        </Form>
                        <Fragment>
                            <MeshtermTree callback={getChecked}/>
                        </Fragment>
                    </div>
                )
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
                <div>
                    <SearchResultsDisplay resData={resultData}/>
                </div>
            )
        }
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default Mesh2GeneTab;
