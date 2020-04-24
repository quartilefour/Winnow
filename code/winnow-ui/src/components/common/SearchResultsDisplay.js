import React, {useState} from "react";
import {Form, Button, Table} from "react-bootstrap";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faChevronLeft} from "@fortawesome/free-solid-svg-icons";
import PubMedArticleListDisplay from "../pubmed/PubMedArticleListDisplay";
import SaveSearchModal from "./SaveSearchModal";
import GeneDetailModal from "../gene/GeneDetailModal";
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
    const [activeGeneDetail, setActiveGeneDetail] = useState(null);
    const [bookmarkEnabled, setBookmarkEnabled] = useState(false);


    React.useEffect(() => {
        if (resData.results !== undefined && resData.results.length > 0) {
            setBookmarkEnabled(true);
        }
        if (!haveResults) {
            setResData(props.resData);
            setIsLoaded(true);
        }
        return () => {
        }
    }, [haveResults, props, resData, activeGeneDetail]);

    /* Enable display of PubMed articles associated with given result. */
    function executePubMedArticleListDisplay(index) {
        setSelectedIndex(index);
        setHaveResults(true);
    }

    function returnToResults() {
        setHaveResults(false)
    }

    if (isLoaded) {
        if (!haveResults) {
            return (
                <div>
                    <Form>
                        <span
                            className="exit-results"
                        >
                            <Button
                                variant="outline-info"
                                size="sm"
                                onClick={props.history}
                            >
                            <FontAwesomeIcon icon={faChevronLeft} color="cornflowerblue"/>
                            Back
                            </Button>
                        </span>
                        <h3>Results</h3>
                        <Table size="sm" striped bordered hover>
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
                            <tbody style={{overflow: "auto"}}>
                            {resData.results.map((value, index) => {
                                return (
                                    <tr key={index}>
                                        <td>
                                            <Button
                                                variant="outline-info"
                                                size="sm"
                                                title={`Details for ${value.symbol}`}
                                                onClick={() => setActiveGeneDetail(index)}
                                            >
                                                {value.geneId}
                                            </Button>
                                            <GeneDetailModal
                                                id={`gdm${index}`}
                                                show={activeGeneDetail === index}
                                                onHide={() => setActiveGeneDetail(null)}
                                                geneid={value.geneId}
                                                active={activeGeneDetail === index ? 1 : 0}
                                            />
                                        </td>
                                        <td>{value.symbol}</td>
                                        <td>{value.meshId}</td>
                                        <td>{value.name}</td>
                                        <td><Button
                                            title={`Publication list for ${value.symbol} - ${value.name}`}
                                            size="sm"
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
            return (
                <div><PubMedArticleListDisplay listData={resData.results[selectedIndex]} history={returnToResults}/>
                </div>
            )
        }
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default SearchResultsDisplay;
