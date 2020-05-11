import React, {useState} from "react";
import {Link, Redirect} from "react-router-dom";
import {Card, Button, Form, Alert} from "react-bootstrap";
import {forgotPassword, forgotSchema} from "../service/AuthService";
import logoImg from "../img/logo.png";
import {useAuth} from "../context/auth";
import {useFormik} from "formik";
import {parseAPIError} from "../service/ApiService";

/**
 * Functional component to render ForgotPassword form and handle response from API.
 *
 * @returns {*}
 * @constructor
 */
function ForgotPassword() {

    const [isLoggedIn, setLoggedIn] = useState(false);
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');
    const {authToken} = useAuth();

    React.useEffect(() => {
        if (authToken) {
            setLoggedIn(true);
        }
    }, [authToken, isLoggedIn]);

    const forgotForm = useFormik({
        initialValues: {
            userEmail: '',
        },
        validationSchema: forgotSchema,
        onSubmit: values => {
            postForgotPassword(values)
        },
    });

    /* Submits user credentials to API login endpoint. */
    function postForgotPassword(values) {
        const credentials = {
            userEmail: values.userEmail,
        };
        forgotPassword(credentials)
            .then(() => {
                setAlertType('info')
                setError('Please check your email and follow the Winnow Reset Password link.')
            })
            .catch(error => {
                setAlertType("danger");
                if (error.response.status >= 400 || error.response.status <= 499) {
                    setError(`Invalid E-mail`);
                } else {
                    setError(`Server error: ${parseAPIError(error)}`);
                }
            });
    }

    /* Displays ForgotPassword form */
    if (!isLoggedIn) return (
        <div>
            <Card
                border="info"
                className="text-center entry-form"
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
                    <Form onSubmit={forgotForm.handleSubmit}>
                        <Form.Group className="form-group">
                            <Form.Control
                                autoComplete="username"
                                id="userEmail"
                                className="form-control"
                                name="userEmail"
                                placeholder="E-mail Address"
                                aria-placeholder="E-mail Address"
                                type="email"
                                onChange={forgotForm.handleChange}
                                onBlur={forgotForm.handleBlur}
                                value={forgotForm.values.userEmail}
                            />
                            <Alert
                                variant="danger"
                                show={!!(forgotForm.touched.userEmail && forgotForm.errors.userEmail)}
                            >
                                {forgotForm.errors.userEmail}
                            </Alert>
                        </Form.Group>
                        <Button
                            block
                            type="submit"
                            variant="info"
                        >
                            Send Link
                        </Button>
                    </Form>
                </Card.Body>
                <Card.Footer>
                    <Alert variant={alertType} show={error.length > 0}>{error}</Alert>
                    <Link to="/login" title="Log in">
                        Login
                    </Link>
                </Card.Footer>
            </Card>
        </div>
    )
    return ( /* Redirects to Profile if already logged in */
        <Redirect to={`/profile`}/>
    )
}

export default ForgotPassword;