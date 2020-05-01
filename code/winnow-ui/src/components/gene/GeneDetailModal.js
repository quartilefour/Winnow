import React, {useState} from 'react';
import PropTypes from 'prop-types';
import {Alert, Button, Image, Modal} from "react-bootstrap";
import PageLoader from "../common/PageLoader";
import {callAPI, parseAPIError} from "../../service/ApiService";
import dnaStrand from "../../img/dna-lg.png";
import {API_RESOURCES, GENEDB_BASE_URL, MESHDB_BASE_URL} from "../../constants";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faExternalLinkAlt} from "@fortawesome/free-solid-svg-icons";
import BootstrapTable from 'react-bootstrap-table-next';
import paginationFactory from 'react-bootstrap-table2-paginator';
import ToolkitProvider, {Search} from 'react-bootstrap-table2-toolkit';
import 'react-bootstrap-table2-toolkit/dist/react-bootstrap-table2-toolkit.min.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';
import 'react-bootstrap-table2-paginator/dist/react-bootstrap-table2-paginator.min.css';
import * as C from "../../constants";

/**
 * GeneDetailModal renders the information for a given Gene.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function GeneDetailModal(props) {

    GeneDetailModal.propTypes = {
        active: PropTypes.number,
        geneid: PropTypes.string,
        id: PropTypes.string,
        onHide: PropTypes.func,
        show: PropTypes.bool
    }

    const {GET_GENE_DETAIL, NCBI_GENE_DETAIL} = API_RESOURCES;

    const [geneDetail, setGeneDetail] = useState({});
    const [geneDetailNCBI, setGeneDetailNCBI] = useState('');
    const [isActive, setIsActive] = useState(0);
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');

    React.useEffect(() => {
        let mounted = true;
        setIsActive(props.active)
        if (isActive && props.geneid !== null) {
            callAPI(GET_GENE_DETAIL, props.geneid)
                .then(res => {
                    if (mounted) {
                        setGeneDetail(res.data);
                        callAPI(NCBI_GENE_DETAIL, props.geneid)
                            .then(ncbiRes => {
                                setGeneDetailNCBI(ncbiRes.data.result[props.geneid]);
                                setIsLoaded(true);
                            })
                            .catch(error => {
                                console.debug(`NCBI gene(${props.geneid}): ${error}`)
                                setError(`Error fetching gene details from NCBI.\n${parseAPIError(error)}`)
                                setAlertType('danger')
                            })
                    }
                })
                .catch(error => {
                    setError(`Error fetching Gene Details.\n${parseAPIError(error)}`);
                    setAlertType('danger');
                })
        }
        return () => {
            mounted = false
        };
    }, [GET_GENE_DETAIL, NCBI_GENE_DETAIL, props, isActive]);

    /* Set up our table options and custom formatting */
    const columnsMesh = [
        {
            dataField: 'geneId',
            isDummyField: true,
            text: 'Gene Id',
            hidden: true,
            csvFormatter: (cell, row, rowIndex) => `${props.geneid}`
        },
        {
            dataField: 'meshId',
            text: 'MeSH Id',
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
            formatter: (cell, row) => {
                return (
                    <a
                        className="co-gene-link"
                        target="_blank"
                        rel="noopener noreferrer"
                        href={`${MESHDB_BASE_URL}${row.meshId}`}
                    >
                        {cell}
                        <FontAwesomeIcon icon={faExternalLinkAlt} color="cornflowerblue" />
                    </a>
                )
            },
            title: (cell, row) => {
                return row.meshId;
            }
        },
        {
            dataField: 'publicationCount',
            text: 'Publications',
            type: 'number',
            sort: true
        },
        {
            dataField: 'pvalue',
            text: 'p-Value',
            type: 'number',
            sort: true
        }
    ];
    const columnsGeneCo = [
        {
            dataField: '_goi',
            isDummyField: true,
            text: 'Gene of Interest',
            hidden: true,
            csvFormatter: (cell, row, rowIndex) => `${props.geneid}`
        },
        {
            dataField: 'geneId',
            text: 'Co-occuring Gene Id',
            hidden: true
        },
        {
            dataField: 'symbol',
            text: 'Gene Symbol',
            sort: true,
            style: {
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
            },
            title: (cell, row) => {
                return row.description;
            },
            formatter: (cell, row) => {
                return (
                    <a
                        className="co-gene-link"
                        target="_blank"
                        rel="noopener noreferrer"
                        href={`${GENEDB_BASE_URL}/${row.geneId}`}
                    >
                        {cell}
                        <FontAwesomeIcon icon={faExternalLinkAlt} color="cornflowerblue" />
                    </a>
                )
            }
        },
        {
            dataField: 'publicationCount',
            text: 'Publications',
            type: 'number',
            sort: true
        }
    ];
    const {SearchBar} = Search;
    const ExportCSVMesh = (props) => {
        const handleClick = () => {
            props.onExport();
        };
        return (
            <Button
                variant="info"
                size="sm"
                onClick={handleClick}
                disabled={!geneDetail.meshResults.length}
            >
                Export MeSH Terms
            </Button>
        )
    }

    const ExportCSVGene = (props) => {
        const handleClick = () => {
            props.onExport();
        };
        return (
            <Button
                variant="info"
                size="sm"
                onClick={handleClick}
                disabled={!geneDetail.geneResults.length}
            >
                Export Co-occurring Genes
            </Button>
        )
    }

    if (isActive) {
        if (isLoaded) {
            return (
                <Modal
                    {...props}
                    size="xl"
                    backdrop="static"
                    aria-labelledby="contained-modal-title-vcenter"
                    scrollable={true}
                    centered
                >
                    <Modal.Header closeButton>
                        <Modal.Title className="text-center">
                            <Image
                                alt="DNA Helix"
                                src={dnaStrand}
                                style={{
                                    display: "inline-block",
                                    margin: "auto",
                                    height: "30px",
                                    width: "90%"
                                }}
                                fluid
                            />
                        </Modal.Title>
                        <Alert variant={alertType} show={error.length > 0}>{error}</Alert>
                    </Modal.Header>
                    <Modal.Body>
                        <div>
                            <h2 className="gene-detail-symbol">{geneDetail.symbol}</h2>
                            <h3 className="gene-detail-desc">{geneDetail.description}</h3>
                            <h5 className="gene-detail-index">
                                {geneDetail.geneId} - &nbsp;
                                <a
                                    className="co-gene-link"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    href={`${GENEDB_BASE_URL}/${geneDetail.geneId}`}
                                >
                                    {
                                        (geneDetailNCBI.genomicinfo[0] !== undefined)
                                            ? geneDetailNCBI.genomicinfo[0].chraccver
                                            : null
                                    }
                                    <FontAwesomeIcon icon={faExternalLinkAlt} color="cornflowerblue"/>
                                </a>
                            </h5>
                        </div>
                        <div id={`seqv_`}>
                        </div>
                        <p className="gene-detail-summary">{geneDetailNCBI.summary}</p>
                        <div className="gene-detail-table-div">
                            <h3>MeSH Terms Enriched for {geneDetail.symbol}</h3>
                            <ToolkitProvider
                                id='geneDetail_meshResults'
                                keyField='meshId'
                                data={geneDetail.meshResults}
                                columns={columnsMesh}
                                search
                                exportCSV={{
                                    fileName: `winnow_${geneDetail.geneId}_meshterms.csv`,
                                    onlyExportFiltered: true,
                                    exportAll: false
                                }}
                            >
                                {
                                    props => (
                                        <div>
                                            <SearchBar {...props.searchProps} placeholder="Search MeSH results..."/>
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
                                                <ExportCSVMesh {...props.csvProps} />
                                            </div>
                                        </div>
                                    )
                                }
                            </ToolkitProvider>
                        </div>
                        <div className="gene-detail-table-div">
                            <h3>Genes Co-occurring in Publications with {geneDetail.symbol}</h3>
                            <ToolkitProvider
                                id='geneDetail_geneResults'
                                keyField='geneId'
                                data={geneDetail.geneResults}
                                columns={columnsGeneCo}
                                search
                                exportCSV={{
                                    fileName: `winnow_${geneDetail.geneId}_geneco.csv`,
                                    onlyExportFiltered: true,
                                    exportAll: false
                                }}
                            >
                                {
                                    props => (
                                        <div>
                                            <SearchBar {...props.searchProps} placeholder="Search Co-occurring Genes..."/>
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
                                                <ExportCSVGene {...props.csvProps} />
                                            </div>
                                        </div>
                                    )
                                }
                            </ToolkitProvider>
                        </div>
                    </Modal.Body>
                </Modal>
            );
        } else {
            return ( /* TODO: Display in separate modal */
                <PageLoader/>
            )
        }
    } else {
        return (
            <span/>
        )
    }
}

export default GeneDetailModal;