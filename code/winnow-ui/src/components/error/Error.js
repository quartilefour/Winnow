import React, {useEffect, useState} from "react";
import {Card} from "react-bootstrap";

function Error(props) {
    const [error, setError] = useState(null);

    useEffect(() => {
        if (props) {
            if (props.error) {
                setError(props.error)
            }
        }
    }, [props, setError]);

    return (
        <div>
            <Card>
                <Card.Header>
                    <Card.Title>
                        Page Not Found
                    </Card.Title>
                    <Card.Subtitle>
                        Error 404 Page Not Found
                    </Card.Subtitle>
                </Card.Header>
                <Card.Body>
                    <Card.Text>{error}</Card.Text>
                </Card.Body>
            </Card>
        </div>
    )
}

export default Error;