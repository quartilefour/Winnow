import React, {useEffect, useState} from "react";
import {Card, Alert} from "react-bootstrap";
import logoImg from "../../img/logo.png";
import PageLoader from "../common/PageLoader";

/**
 * Renders Login form and handles response from API.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Maintenance(props) {
    const [error, setError] = useState(null);
    const [alertType, setAlertType] = useState('');

    React.useEffect(() => {
        if (props.error) {
            setError(props.error);
            setAlertType('danger');
        }
    }, [props]);

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
                <Card.Footer>
                    <Alert variant={alertType}>{error}</Alert>
                </Card.Footer>
            </Card>
        </div>
    )
}

export default Maintenance;