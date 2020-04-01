import React from "react";
import {Card} from "react-bootstrap";

function Support() {
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
            }}
        >
            <Card.Header>
            <Card.Title>Winnow Support</Card.Title>
                <Card.Subtitle>FAQ</Card.Subtitle>
            </Card.Header>
            <Card.Body>
                <Card.Text>
                    Here in your hour of need.
                </Card.Text>
                <ul style={{textAlign: 'left'}}>
                    <li>Question 1</li>
                    <li>Question 2</li>
                    <li>Question 3</li>
                </ul>
            </Card.Body>
        </Card>
    </div>
    );
}

export default Support;