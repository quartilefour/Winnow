import React, {useState} from "react";
import PropTypes from 'prop-types';
import {Form, Alert, Button} from "react-bootstrap";
import {callAPI, parseAPIError} from "../../service/ApiService";
import PageLoader from "../common/PageLoader";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faChevronLeft, faExternalLinkAlt} from "@fortawesome/free-solid-svg-icons";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import {API_RESOURCES, PUBMED_BASE_URL, T2_POPTS} from "../../constants";

/**
 * PubMedArticleListDisplay displays a list of PubMed articles found in a previous search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function PubMedArticleListDisplay(props) {

    PubMedArticleListDisplay.propTypes = {
        history: PropTypes.func,
        listData: PropTypes.object
    }

    const {history, listData} = props;

    const {GET_ARTICLES} = API_RESOURCES;

    const [isLoaded, setIsLoaded] = useState(false);
    const [publicationData, setPublicationData] = useState('');
    const [pubmedData, setPubmedData] = useState('');
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');

    /**
     * extract gene/mesh info from listData.
     * new state var for pubmed results.
     */
    React.useEffect(() => {
        let mounted = true;
        if (listData === undefined || listData === null) {
            setError('An error occurred while receiving the data from the search results.');
            setAlertType('danger');
        } else {
            setPublicationData(listData);
            callAPI(GET_ARTICLES,
                {
                    geneId: listData.geneId,
                    meshId: listData.meshId,
                    symbol: listData.symbol,
                    name: listData.name
                })
                .then(res => {
                    if (mounted) {
                        setPubmedData(res.data);
                        setIsLoaded(true);
                    }
                })
                .catch(error => {
                    setError(`A fatal error has occurred while fetching the Publication list.\n/
                ${parseAPIError(error)}`);
                    setAlertType('danger');
                    setIsLoaded(true);
                });
        }
        return () => {
            mounted = false
        };
    }, [GET_ARTICLES, listData]);

    /* Set up our table options and custom formatting */
    const columns = [
        {
            dataField: 'geneId',
            isDummyField: true,
            text: 'Gene Id',
            hidden: true,
            csvFormatter: (cell, row, rowIndex) => `${listData.geneId}`
        },
        {
            dataField: 'meshId',
            isDummyField: true,
            text: 'MeSH Id',
            hidden: true,
            csvFormatter: (cell, row, rowIndex) => `${listData.meshId}`
        },
        {
            dataField: 'publicationId',
            text: 'PubMed Id',
            headerStyle: () => {
                return {width: "15%"};
            },
            formatter: (cell, row) => {
                return (
                    <a
                        className="pubmed-art-link"
                        target="_blank"
                        rel="noopener noreferrer"
                        href={`${PUBMED_BASE_URL}/${row.publicationId}`}
                    >
                        {cell}
                        <FontAwesomeIcon icon={faExternalLinkAlt} color="cornflowerblue"/>
                    </a>
                )
            },
            title: (cell, row) => {
              return `View PubMed Article #${row.publicationId}`
            },
            sort: true
        },
        {
            dataField: 'pubmedUrl',
            isDummyField: true,
            text: 'Article URL',
            hidden: true,
            csvFormatter: (cell, row, rowIndex) => `${PUBMED_BASE_URL}/${row.publicationId}`
        },
        {
            dataField: 'title',
            text: 'Title',
            style: {
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
            },
            title: (cell, row) => {
                return row.title
            },
            sort: true
        },
        {
            dataField: 'authors',
            text: 'Authors',
            style: {
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
            },
            formatter: (cell, row) => {
                return (
                    <span>
                       {row.authors.map((author) => {
                           return (
                               author.lastName
                           )
                       }).join(", ")
                       }
                   </span>
                )
            },
            title: (cell, row) => {
                return row.authors.map((author) => {
                    return (
                        author.lastName
                    )
                }).join(", ")
            },
            csvFormatter: (cell, row, rowIndex) => {
                return row.authors.map((author) => {
                    return (
                        author.lastName
                    )
                }).join(", ")
            },
            sort: true
        },
        {
            dataField: 'completedDate',
            text: 'Published',
            headerStyle: () => {
                return {width: "15%"};
            },
            type: 'date',
            sort: true
        },
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
                disabled={!pubmedData.results.length > 0}
            >
                Export
            </Button>
        )
    }

    const BackButton = () => {
        return (
            <span className="exit-results">
            <Button variant="outline-info" size="sm" onClick={history}>
                  <FontAwesomeIcon icon={faChevronLeft} color="cornflowerblue"/>
                       Back
            </Button>
        </span>
        )
    }

    if (isLoaded) {
        if (error) return (
            <div>
                <Alert variant={alertType}>{error}</Alert>
                <BackButton/>
            </div>
        )
            return (
                <div>
                    <Alert variant={alertType} show={error.length > 0}>{error}</Alert>
                    <Form>
                        <BackButton/>
                        <h3> Publications for {publicationData.symbol} ({publicationData.geneId})
                            and {publicationData.name} ({publicationData.meshId})</h3>
                        <ToolkitProvider
                            keyField='publicationId'
                            data={pubmedData.results}
                            columns={columns}
                            search={{
                                searchFormatted: true
                            }}
                            exportCSV={{
                                fileName: `winnow_pubmed_${Date.now()}.csv`,
                                onlyExportFiltered: true,
                                exportAll: false
                            }}
                        >
                            {
                                props => (
                                    <div>
                                        <SearchBar {...props.searchProps} placeholder="Search article list..."/>
                                        <BootstrapTable
                                            {...props.baseProps}
                                            pagination={
                                                paginationFactory(T2_POPTS)
                                            }
                                            defaultSorted={[{dataField: 'completedDate', order: 'desc'}]}
                                            bootstrap4
                                            striped
                                            condensed
                                            hover
                                        />
                                        <div className="button-bar">
                                            <ExportCSV {...props.csvProps}/>
                                        </div>
                                    </div>
                                )
                            }
                        </ToolkitProvider>
                    </Form>
                </div>
            )
    } else {
        return (<div><PageLoader/></div>)
    }
}

export default PubMedArticleListDisplay;
