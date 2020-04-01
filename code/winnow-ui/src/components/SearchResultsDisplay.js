import React, {useEffect, useState} from "react";
import {Form, Button, Table} from "react-bootstrap";
import {fetchPubMedArticleList} from "../service/ApiService";
import PubMedArticleListDisplay from "./PubMedArticleListDisplay";

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
            setResData(props.resData);
            setHaveResults(true);
            setIsLoaded(true);
        } else {
            /* fetchSearchResults() */
        }
    }, [haveResults, props]);

    if (isLoaded) {
        console.info(`SearchResultsDisplay selected: ${JSON.stringify(resData)}`);
        return (
            <div>
                <Form>
                    <h3> Results</h3>
                    <Table striped bordered hover>
                        <thead>
                        <tr>
                            <th>Gene Id</th>
                            <th>Symbol</th>
                            <th>Meshterm Id</th>
                            <th>Meshterms</th>
                            <th>Publications</th>
                            <th>P-value</th>
                        </tr>
                        </thead>
                        <tbody>
                        {resData.results.map((value, index) => {
                            return (
                                <tr key={index}>
                                    <td>{value.geneId}</td>
                                    <td>{value.symbol}</td>
                                    <td>{value.meshId}</td>
                                    <td>{value.meshTerm}</td>
                                    <td>{value.publicationCount}<PubMedArticleListDisplay listData={fetchPubMedArticleList()}/></td>
                                    <td>{value.pValue}</td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </Table>
                    <div className="button-bar">
                        <Button variant="info" size="sm">Bookmark</Button>
                        <Button variant="info" size="sm">Export</Button>
                    </div>
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
