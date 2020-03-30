import React, {Fragment, useEffect, useState} from "react";
import {Button, Form} from "react-bootstrap";
import {MeshtermTree} from "../components/MeshtermTree";
import {fetchSearchResults} from "../service/ApiService";
import SearchResultsDisplay from "./SearchResultsDisplay";

/**
 * Mesh2GeneTab builds the content for MeSH term Search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Mesh2GeneTab(props) {
    const [isLoaded, setIsLoaded] = useState(false);
    const [checkedTerms, setCheckedTerms] = useState([]);
    const [haveResults, setHaveResults] = useState(false);
    const [resulData, setResultData] = useState('');

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

    if (isLoaded) {
        if (!haveResults) {
            return (
                <div>
                    <Form>
                        <Button onClick={executeSearch}>Search</Button>
                    </Form>
                    <Fragment>
                        <MeshtermTree callback={getChecked}/>
                    </Fragment>
                </div>
            )
        } else {
            return (
                <div>
                    <SearchResultsDisplay resData={resulData}/>
                </div>
            )
        }
    } else {
        return (
            <div>Loading...</div>
        )
    }
}

export default Mesh2GeneTab;
