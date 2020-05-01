import React from "react";
import {Card} from "react-bootstrap";
import logoImg from "../../img/logo.png";

/**
 * Renders Login form and handles response from API.
 *
 * @returns {*}
 * @constructor
 */
function Maintenance() {
    return (
        <div>
            <Card
                border="info"
                className="text-center"
                style={{
                    flexDirection: 'column',
                    maxWidth: '410px',
                    display: 'flex',
                    margin: '10% auto',
                    width: '50%'
                }}>
                <Card.Title>Winnow</Card.Title>
                <Card.Subtitle>Gene Function Navigator</Card.Subtitle>
                <Card.Img variant="top" src={logoImg} style={{margin: 'auto', width: '50%'}}/>
                <Card.Body>
                    Winnow API is currently unavailable.
                </Card.Body>
            </Card>
        </div>
    )
}

export default Maintenance;