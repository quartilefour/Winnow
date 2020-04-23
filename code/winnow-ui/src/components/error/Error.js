import React, {useEffect, useState} from "react";
import {Card, Image} from "react-bootstrap";
import Oops from "../../img/oops.png";

function Error(props) {
    const [error, setError] = useState('Page Not Found');

    React.useEffect(() => {
        if (props && props.error) {
            setError(props.error)
        }
    }, [props, setError]);

    return (
        <div>
            <Card
                className="text-center tab"
                style={{
                    flexDirection: 'column',
                    maxWidth: '720px',
                    display: 'flex',
                    margin: '5% auto',
                    width: '75%'
                }}>
                <Card.Header>
                    <Card.Title>
                        Error
                    </Card.Title>
                    <Card.Subtitle>
                        {error}
                    </Card.Subtitle>
                </Card.Header>
                <Card.Body>
                    <Card.Text><Image src={Oops} /></Card.Text>
                </Card.Body>
            </Card>
        </div>
    )
}

export default Error;