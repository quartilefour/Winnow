import React, {useState} from "react";
import {Form, Button, Table} from "react-bootstrap";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faChevronLeft} from "@fortawesome/free-solid-svg-icons";
import PubMedArticleListDisplay from "../pubmed/PubMedArticleListDisplay";
import SaveSearchModal from "./SaveSearchModal";
import * as C from "../../constants";
import GeneDetailModal from "../gene/GeneDetailModal";
import PageLoader from "./PageLoader";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import ToolkitProvider, {CSVExport, Search} from 'react-bootstrap-table2-toolkit';
import 'react-bootstrap-table2-toolkit/dist/react-bootstrap-table2-toolkit.min.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';
import 'react-bootstrap-table2-paginator/dist/react-bootstrap-table2-paginator.min.css';


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

    /* Set up our table options and custom formatting */
    const columns = [
        {
            dataField: 'index',
            hidden: true
        },
        {
            dataField: 'geneId',
            text: 'Gene Id',
            sort: true,
            formatter: (cell, row) => {
                console.info(`table2: ${row.index}`);
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
            dataField: 'name',
            text: 'Meshterms',
            sort: true,
            style: {
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
            },
            title: (cell, row) => {
                return row.meshId;
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
        if (!haveResults) {
            return (
                <div>
                    <Form>
                        <span className="exit-results">
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
                        <ToolkitProvider
                            keyField='index'
                            data={resData.results}
                            columns={columns}
                            search
                            exportCSV={{
                                filename: 'winnow.csv',
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
                                                paginationFactory(C.SRD_POPTS)
                                            }
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
                            active={activeGeneDetail !== null ? 1 : 0}
                        />
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
