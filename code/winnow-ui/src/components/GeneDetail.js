import React from 'react';
import {Card} from "react-bootstrap";

/**
 * GeneDetail renders the information for a given Gene.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function GeneDetail(props) {
    let geneDetail = props.geneDetail;
    return (
        <Card>
            <Card.Title>{geneDetail.symbol}</Card.Title>
            <Card.Subtitle>{geneDetail.geneId}</Card.Subtitle>
            <Card.Body>{geneDetail.description}</Card.Body>
        </Card>
    );
}

export default GeneDetail;