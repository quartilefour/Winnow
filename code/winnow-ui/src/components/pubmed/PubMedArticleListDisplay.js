import React, {useState} from "react";
import {Form, Alert, Button} from "react-bootstrap";
import {fetchPubMedArticleList, parseAPIError} from "../../service/ApiService";
import PageLoader from "../common/PageLoader";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faChevronLeft, faExternalLinkAlt} from "@fortawesome/free-solid-svg-icons";
import ToolkitProvider, {Search} from "react-bootstrap-table2-toolkit";
import BootstrapTable from "react-bootstrap-table-next";
import paginationFactory from "react-bootstrap-table2-paginator";
import * as C from "../../constants";

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
    const [pubmedData, setPubmedData] = useState('');
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');

    /**
     * extract gene/mesh info from listData.
     * new state var for pubmed results.
     */
    React.useEffect(() => {
        let mounted = true;
        if (props.listData === undefined || props.listData === null) {
            setError('An error occurred while receiving the data from the search results.');
            setAlertType('danger');
        } else {
            setListData(props.listData);
            fetchPubMedArticleList(
                {
                    geneId: props.listData.geneId,
                    meshId: props.listData.meshId,
                    symbol: props.listData.symbol,
                    name: props.listData.name
                })
                .then(res => {
                    if (mounted) {
                        setPubmedData(res);
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
    }, [props]);

    /* Set up our table options and custom formatting */
    const columns = [
        {
            dataField: 'geneId',
            isDummyField: true,
            text: 'Gene Id',
            hidden: true,
            csvFormatter: (cell, row, rowIndex) => `${props.listData.geneId}`
        },
        {
            dataField: 'meshId',
            isDummyField: true,
            text: 'MeSH Id',
            hidden: true,
            csvFormatter: (cell, row, rowIndex) => `${props.listData.meshId}`
        },
        {
            dataField: 'publicationId',
            text: 'Publication Id',
            formatter: (cell, row) => {
                return (
                    <a
                        className="pubmed-art-link"
                        target="_blank"
                        rel="noopener noreferrer"
                        href={`${C.PUBMED_BASE_URL}/${row.publicationId}`}
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
            csvFormatter: (cell, row, rowIndex) => `${C.PUBMED_BASE_URL}/${row.publicationId}`
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
            text: 'Completed',
            type: 'date',
            sort: true
        },
        {
            dataField: 'dateRevised',
            text: 'Revised',
            type: 'date',
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
                disabled={!pubmedData.results.length > 0}
            >
                Export
            </Button>
        )
    }

    const BackButton = () => {
        return (
            <span className="exit-results">
            <Button variant="outline-info" size="sm" onClick={props.history}>
                  <FontAwesomeIcon icon={faChevronLeft} color="cornflowerblue"/>
                       Back
            </Button>
        </span>
        )
    }

    if (isLoaded) {
        if (error) {
            return (
                <div>
                    <Alert variant={alertType}>{error}</Alert>
                    <BackButton />
                </div>
            )
        } else {
            return (
                <div>
                    <Alert variant={alertType} show={error.length > 0}>{error}</Alert>
                    <Form>
                        <BackButton />
                        <h3> Publications for {listData.symbol} ({listData.geneId})
                            and {listData.name} ({listData.meshId})</h3>
                        <ToolkitProvider
                            keyField='publicationId'
                            data={pubmedData.results}
                            columns={columns}
                            search
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
                                                paginationFactory(C.T2_POPTS)
                                            }
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
            );
        }
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default PubMedArticleListDisplay;
