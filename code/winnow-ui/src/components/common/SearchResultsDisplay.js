import React, {useState} from "react";
import PropTypes from 'prop-types';
import {Form, Button} from "react-bootstrap";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faChevronLeft} from "@fortawesome/free-solid-svg-icons";
import PubMedArticleListDisplay from "../pubmed/PubMedArticleListDisplay";
import SaveSearchModal from "./SaveSearchModal";
import {T2_POPTS} from "../../constants";
import GeneDetailModal from "../gene/GeneDetailModal";
import PageLoader from "./PageLoader";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import 'react-bootstrap-table2-toolkit/dist/react-bootstrap-table2-toolkit.min.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';
import 'react-bootstrap-table2-paginator/dist/react-bootstrap-table2-paginator.min.css';
import {prettySearch} from "../../service/SearchService";


/**
 * SearchResults displays the results of searches.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function SearchResultsDisplay(props) {

    SearchResultsDisplay.propTypes = {
        resultData: PropTypes.object,
        history: PropTypes.func
    }

    const {history, resultData} = props;

    const [isLoaded, setIsLoaded] = useState(false);
    const [resData, setResData] = useState('');
    const [haveResults, setHaveResults] = useState(false);
    const [selectedIndex, setSelectedIndex] = useState(0);
    const [showSaveSearch, setShowSaveSearch] = useState(false);
    const [activeGeneDetail, setActiveGeneDetail] = useState(null);
    const [bookmarkEnabled, setBookmarkEnabled] = useState(false);

    React.useEffect(() => {
        if (!haveResults) { /* Display search results with Links to Gene details & Pub list */
            setResData(resultData);
            if (resultData.results !== undefined && resultData.results.length > 0) {
                setBookmarkEnabled(true);
            }
            setIsLoaded(true);
        }
    }, [haveResults, resultData, activeGeneDetail]);

    /* Set up our table options and custom formatting */
    const columns = [
        {
            dataField: 'index',
            text: 'Index',
            hidden: true,
            csvExport: false
        },
        {
            dataField: 'geneId',
            text: 'Gene Id',
            sort: true,
            formatter: (cell, row) => {
                return (
                    <Button
                        variant="outline-info"
                        size="sm"
                        title={`Details for ${row.symbol}`}
                        onClick={() => {setActiveGeneDetail(row.geneId)}}
                    >
                        {row.geneId}
                    </Button>
                )
            }
        },
        {
            dataField: 'symbol',
            text: 'Symbol',
            sort: true,
            title: (cell, row) => {
                return row.description;
            }
        },
        {
            dataField: 'meshId',
            text: 'MeSH Term Id',
            hidden: true
        },
        {
            dataField: 'name',
            text: 'MeSH Term',
            sort: true,
            style: {
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
            },
            title: (cell, row) => {
                return `${row.meshId} - ${row.name}`;
            }
        },
        {
            dataField: 'publicationCount',
            text: 'Publications',
            sort: true,
            formatter: (cell, row) => {
                return (
                    <Button
                        title={`Publication list for ${row.symbol} - ${row.name}`}
                        size="sm"
                        variant="info"
                        onClick={() => {
                            executePubMedArticleListDisplay(row.index)
                        }}
                    >{cell}</Button>
                )
            }
        },
        {
            dataField: 'pvalue',
            text: 'P-value',
            type: 'number',
            formatter: (cell, row) => {
                return Number.parseFloat(row.pvalue).toExponential(4)
            },
            sort: true
        }
    ];
    const {SearchBar} = Search;
    const ExportCSV = (props) => {
        const handleClick = () => {
            props.onExport();
        };
        return (
            <Button
                variant="info"
                size="sm"
                onClick={handleClick}
                disabled={!bookmarkEnabled}
            >
                Export
            </Button>
        )
    }

    /* Enable display of PubMed articles associated with given result. */
    function executePubMedArticleListDisplay(index) {
        setSelectedIndex(index);
        setHaveResults(true);
    }

    function returnToResults() {
        setHaveResults(false)
    }

    if (isLoaded) {
        if (!haveResults) { /* Display Gene/MeSH Combination search results */
            return (
                <div>
                    <Form>
                        <span className="exit-results">
                            <Button
                                variant="outline-info"
                                size="sm"
                                onClick={history}
                            >
                            <FontAwesomeIcon icon={faChevronLeft} color="cornflowerblue"/>
                            Back
                            </Button>
                        </span>
                        <h3>Results</h3>
                        <h5 className="result-query-disp">Search: {prettySearch(resData.searchQuery)}</h5>
                        <ToolkitProvider
                            keyField='index'
                            data={resData.results}
                            columns={columns}
                            search
                            exportCSV={{
                                fileName: `winnow_searchResults_${Date.now()}.csv`,
                                onlyExportFiltered: true,
                                exportAll: false
                            }}
                        >
                            {
                                props => (
                                    <div>
                                        <SearchBar {...props.searchProps} placeholder="Search result set..."/>
                                        <BootstrapTable
                                            {...props.baseProps}
                                            pagination={
                                                paginationFactory(T2_POPTS)
                                            }
                                            defaultSorted={[{dataField: 'pvalue', order: 'asc'}]}
                                            bootstrap4
                                            striped
                                            condensed
                                            hover
                                        />
                                        <div className="button-bar">
                                            <ExportCSV {...props.csvProps}/>
                                            <Button
                                                variant="info"
                                                size="sm"
                                                disabled={!bookmarkEnabled}
                                                onClick={() => setShowSaveSearch(true)}
                                            >
                                                Bookmark
                                            </Button>
                                        </div>
                                    </div>
                                )
                            }
                        </ToolkitProvider>
                        <GeneDetailModal
                            id={`gdm${activeGeneDetail}`}
                            show={activeGeneDetail !== null}
                            onHide={() => setActiveGeneDetail(null)}
                            geneid={activeGeneDetail}
                            active={(activeGeneDetail !== null) ? 1 : 0}
                        />
                        <SaveSearchModal
                            show={showSaveSearch}
                            onHide={() => setShowSaveSearch(false)}
                            searchdata={resData}
                        />
                    </Form>
                </div>
            );
        } else { /* Display PubMed Article List for selected Gene-MeSH combination */
            return (
                <div>
                    <PubMedArticleListDisplay listData={resData.results[selectedIndex]} history={returnToResults}/>
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
