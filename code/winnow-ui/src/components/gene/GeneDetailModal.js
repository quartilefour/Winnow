import React, {useEffect, useState} from 'react';
import {Alert, Button, Image, Modal, Table} from "react-bootstrap";
import PageLoader from "../common/PageLoader";
import {fetchGeneDetails, fetchNCBIGeneDetails} from "../../service/ApiService";
import dnaStrand from "../../img/dna-lg.png";

/**
 * GeneDetailModal renders the information for a given Gene.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function GeneDetailModal(props) {

    const [geneDetail, setGeneDetail] = useState({});
    const [geneDetailNCBI, setGeneDetailNCBI] = useState('');
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);
    const [alertType, setAlertType] = useState('');

    useEffect(() => {
        fetchGeneDetails(props.geneid)
            .then(res => {
                setGeneDetail(res);
                fetchNCBIGeneDetails(props.geneid)
                    .then(ncbiRes => {
                        setGeneDetailNCBI(ncbiRes.result[props.geneid])
                        setIsLoaded(true)
                    })
                    .catch(err => {

                    })
            })
            .catch(err => {
                setError(err);
                setAlertType('danger');
            })
        return function () {

        }
    }, [props]);

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
                                width: "50%"
                            }}
                            fluid
                        />
                    </Modal.Title>
                    <Alert variant={alertType}>{error}</Alert>
                </Modal.Header>
                <Modal.Body>
                    <div>
                        <h2 className="gene-detail-symbol">{geneDetail.symbol}</h2>
                        <h3 className="gene-detail-desc">{geneDetail.description}</h3>
                        <h5 className="gene-detail-index">{geneDetail.geneId} - {geneDetailNCBI.genomicinfo[0].chraccver}</h5>
                    </div>
                    <div id={`seqv_`}>
                    </div>
                    <p className="gene-detail-summary">{geneDetailNCBI.summary}</p>
                    <div className="gene-detail-table-div">
                        <h3>MeSH Terms Enriched for {geneDetail.symbol}</h3>
                        <Table size="sm" striped bordered hover>
                            <thead>
                            <tr>
                                <th>MeSH Term</th>
                                <th>p-Value</th>
                                <th>Publications</th>
                            </tr>
                            </thead>
                            <tbody style={{overflow: "auto"}}>
                            {geneDetail.meshResults.map((value, index) => {
                                return (
                                    <tr key={index}>
                                        <td>{value.name}</td>
                                        <td>{value.pvalue}</td>
                                        <td>{value.publicationCount}</td>
                                    </tr>
                                )
                            })}
                            </tbody>
                        </Table>
                        <Button
                            size="sm"
                            variant="info"
                            disabled={!geneDetail.meshResults.length}
                        >
                            Export MeSH Terms
                        </Button>
                    </div>
                    <div className="gene-detail-table-div">
                        <h3>Genes Co-occurring in Publications with {geneDetail.symbol}</h3>
                        <Table size="sm" striped bordered hover>
                            <thead>
                            <tr>
                                <th>Gene</th>
                                <th>p-Value</th>
                                <th>Publications</th>
                            </tr>
                            </thead>
                            <tbody style={{overflow: "auto"}}>
                            {geneDetail.geneResults.map((value, index) => {
                                return (
                                    <tr key={index}>
                                        <td>{value.symbol}</td>
                                        <td>{value.pvalue}</td>
                                        <td>{value.publicationCount}</td>
                                    </tr>
                                )
                            })}
                            </tbody>
                        </Table>
                        <Button
                            size="sm"
                            variant="info"
                            disabled={!geneDetail.geneResults.length}
                        >
                            Export Co-occurring Genes
                        </Button>
                    </div>
                </Modal.Body>
            </Modal>
        );
    } else {
        return (
            <PageLoader/>
        )
    }
}

export default GeneDetailModal;