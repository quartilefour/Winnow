import SearchResultsDisplay from "./SearchResultsDisplay";
import React, {useEffect, useState} from "react";
import {Form, Button, Table} from "react-bootstrap";
import {fetchPubMedArticleList, fetchSearchResults} from "../service/ApiService";

/**
 * PubMedArticleListDisplay displays a list of PubMed articles found in a previous search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function PubMedArticleListDisplay(props) {

    const [isLoaded, setIsLoaded] = useState(false);
    const [listData, setListData] = useState('');
    const [haveResults, setHaveResults] = useState(false);
    const [selectedIndex, setSelectedIndex] = useState(0);

    useEffect(() => {
        if (!haveResults) {
            setListData(props.listData);
            setSelectedIndex(props.selectedIndex);
            setHaveResults(true);
            setIsLoaded(true);
        } else {
            fetchPubMedArticleList(
                {searchQuery:"12345",
                queryType: "gene",
                queryFormat: "geneID",
                geneId: "gene1",
                symbol: "awesomegene",
                meshId: "mesh1",
                meshTerm: "xfactor"})
                .then(res => {
                    setListData(res);
                    setIsLoaded(true);
                }).catch(err => {
                setIsLoaded(true);
            });
        }
    }, [haveResults, props, selectedIndex]);

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
                                    <td><a target="_blank" href={value.publicationURLBase + value.publicationID}>{value.publicationID}</a></td>
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
