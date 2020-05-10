import React, {useState} from "react";
import PropTypes from 'prop-types';
import {Card, Image} from "react-bootstrap";
import Oops from "../img/oops.png";

function Error(props) {

    Error.propTypes = {
        errorMessage: PropTypes.string
    }

    Error.defaultProps = {
        errorMessage: ''
    }

    const {errorMessage, location} = props;

    const [error, setError] = useState('Page Not Found');

    React.useEffect(() => {
        if (errorMessage !== '') {
            setError(errorMessage)
        } else if (location.state.errorMessage !== undefined) {
            setError(location.state.errorMessage)
        }
    }, [errorMessage, location]);

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