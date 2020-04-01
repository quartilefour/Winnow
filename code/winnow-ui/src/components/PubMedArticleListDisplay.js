import SearchResultsDisplay from "./SearchResultsDisplay";
import React, {useEffect, useState} from "react";
import {Form, Button, Table} from "react-bootstrap";
import {fetchPubMedArticleList} from "../service/ApiService";

/**
 * SearchResults displays the results of searches.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function PubMedArticleListDisplay(props) {

    const [isLoaded, setIsLoaded] = useState(false);
    const [listData, setListData] = useState('');
    const [haveResults, setHaveResults] = useState(false);

    useEffect(() => {
        if (!haveResults) {
            setListData(props.listData);
            setHaveResults(true);
            setIsLoaded(true);
        } else {
            /* fetchPubMedArticleList() */
        }
    }, [haveResults, props]);

    if (isLoaded) {
        console.info(`PubMedArticleListDisplay selected: ${JSON.stringify(listData)}`);
        return (
            <div>
                <Form>
                    <h3> Publications for {listData.symbol} and {listData.meshTerm}</h3>
                    <Table striped bordered hover>
                        <thead>
                        <tr>
                            <th>Title</th>
                            <th>PubMed ID</th>
                            <th>Author</th>
                            <th>Publication Date</th>
                            <th>URL</th>
                        </tr>
                        </thead>
                        <tbody>
                        {listData.results.map((value, index) => {
                            return (
                                <tr key={index}>
                                    <td>{value.publicationTitle}</td>
                                    <td>{value.publicationID}</td>
                                    <td>{value.publicationAuthor}</td>
                                    <td>{value.publicationDate}</td>
                                    <td>{value.publicationURLBase}{value.publicationID}</td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </Table>
                </Form>
            </div>
        );
    } else {
        return (
            <div>Loading...</div>
        )
    }
}

export default PubMedArticleListDisplay;
