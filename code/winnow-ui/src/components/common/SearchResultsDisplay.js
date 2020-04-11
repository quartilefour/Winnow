import React, {useEffect, useState} from "react";
import {Form, Button, Table} from "react-bootstrap";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faChevronLeft} from "@fortawesome/free-solid-svg-icons";
import PubMedArticleListDisplay from "../pubmed/PubMedArticleListDisplay";
import SaveSearchModal from "./SaveSearchModal";
import PageLoader from "./PageLoader";

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
    const [selectedIndex, setSelectedIndex] = useState(0);
    const [showSaveSearch, setShowSaveSearch] = useState(false);
    const [bookmarkEnabled, setBookmarkEnabled] = useState(false);


    function executePubMedArticleListDisplay(index) {
        setSelectedIndex(index);
        setHaveResults(true);
    }

    useEffect(() => {
        if (resData.results !== undefined && resData.results.length > 0) {
            setBookmarkEnabled(true);
        }
        if (!haveResults) {
            setResData(props.resData);
            //setHaveResults(true);
            setIsLoaded(true);
        } else {
            /* fetchSearchResults() */
        }
    }, [haveResults, props, resData]);

    if (isLoaded) {
        if (!haveResults) {
            console.info(`SearchResultsDisplay selected: ${JSON.stringify(resData)}`);
            return (
                <div>
                    <Form>
                        <span
                            className="exit-results"
                            onClick={props.history}
                        >
                            <FontAwesomeIcon icon={faChevronLeft} color="cornflowerblue"/>
                            Back
                        </span>
                        <h3>Results</h3>
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
                                console.info(`SearchResult ${index}: ${value.geneId} - ${value.meshId}`);
                                return (
                                    <tr key={index}>
                                        <td>{value.geneId}</td>
                                        <td>{value.symbol}</td>
                                        <td>{value.meshId}</td>
                                        <td>{value.name}</td>
                                        <td><Button
                                            variant="info"
                                            onClick={() => {
                                                executePubMedArticleListDisplay(index)
                                            }}
                                        >{value.publicationCount}</Button></td>
                                        <td>{value.pvalue}</td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </Table>
                        <div className="button-bar">
                            <Button
                                variant="info"
                                size="sm"
                                disabled={!bookmarkEnabled}
                                onClick={() => setShowSaveSearch(true)}
                            >Bookmark</Button>
                            <Button
                                variant="info"
                                size="sm"
                                disabled={!bookmarkEnabled}
                            >Export</Button>
                        </div>
                        <SaveSearchModal
                            show={showSaveSearch}
                            onHide={() => setShowSaveSearch(false)}
                            searchdata={resData}
                        />
                    </Form>
                </div>
            );
        } else {
            console.info(`PubMedArticleDisplay Selected: ${JSON.stringify(resData)}`);
            return (
                <div><PubMedArticleListDisplay listData={resData.results[selectedIndex]}/></div>
            )
        }
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default SearchResultsDisplay;
