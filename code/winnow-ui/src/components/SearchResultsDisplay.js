import React, {useEffect, useState} from "react";
import {Form, Button} from "react-bootstrap";
import {fetchSearchResults} from "../service/ApiService";

/**
 * SearchResults displays the results of searches.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function SearchResultsDisplay(props) {

    const [isLoaded, setIsLoaded] = useState(false);
    const [resData, setResData] = useState('');
    const [haveResults, setHaveResults] = useState(false);

    useEffect(() => {
        if (!haveResults) {
            fetchSearchResults()
                .then(res => {
                    setResData(res);
                    setIsLoaded(true);
                }).catch(err => {
                setIsLoaded(true);
            });
            setHaveResults(true);
        } else {
            /* fetchSearchResults() */
        }
    }, [haveResults]);

    if (isLoaded) {
        console.info(`SearchResultsDisplay selected: ${JSON.stringify(resData)}`);
        return (
            <div>
                <Form>
                    <table />
                    <th /> Results
                    {this.state.resData.results.map (( value, index ) => {
                        return (
                            <tr key={index}>
                                <td>{value.geneId}</td>
                                <td>{value.symbol}</td>
                                <td>{value.meshId}</td>
                                <td>{value.meshTerm}</td>
                                <td>{value.publicationCount}</td>
                                <td>{value.pValue}</td>
                            </tr>
                        );
                    })}
                <Button>Bookmark</Button>
                <Button>Export</Button>
                </Form>
            </div>
        );
    } else {
        return (
            <div>Loading...</div>
        )
    }
}

export default SearchResultsDisplay;
