import React, {useState} from "react";
import {Redirect} from "react-router-dom";
import {Card, Button, Form, Alert} from "react-bootstrap";
import {resetPassword, resetSchema} from "../service/AuthService";
import logoImg from "../img/logo.png";
import {useAuth} from "../context/auth";
import {useFormik} from "formik";
import {parseAPIError} from "../service/ApiService";

/**
 * Functional component to render ResetPassword form and handle response from API.
 *
 * @returns {*}
 * @constructor
 */
function ResetPassword() {

    const [isLoggedIn, setLoggedIn] = useState(false);
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');
    const {authToken} = useAuth();

    React.useEffect(() => {
        if (authToken) {
            setLoggedIn(true);
        }
    }, [authToken, isLoggedIn]);

    const resetForm = useFormik({
        initialValues: {
            userEmail: '',
        },
        validationSchema: resetSchema,
        onSubmit: values => {
            postResetPassword(values)
        },
    });

    /* Submits user credentials to API login endpoint. */
    function postResetPassword(values) {
        const credentials = {
            userEmail: values.userEmail,
        };
        resetPassword(credentials)
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

    /* Displays ResetPassword form */
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
                    <Form onSubmit={resetForm.handleSubmit}>
                        <Form.Group>
                            <Form.Control
                                type="password"
                                name="userPassword"
                                autoComplete="new-password"
                                value={resetForm.values.userPassword}
                                onChange={resetForm.handleChange}
                                onBlur={resetForm.handleBlur}
                                placeholder="Password"
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Control
                                type="password"
                                autoComplete="new-password"
                                name="passwordConfirm"
                                value={resetForm.values.passwordConfirm}
                                onChange={resetForm.handleChange}
                                onBlur={resetForm.handleBlur}
                                placeholder="Confirm Password"
                            />
                            <Alert
                                variant="danger"
                                show={!!(resetForm.errors.userPassword || resetForm.errors.passwordConfirm)}
                            >
                                {resetForm.touched.userPassword && resetForm.errors.userPassword ?
                                    resetForm.errors.userPassword : null}
                                {resetForm.touched.passwordConfirm && resetForm.errors.passwordConfirm ?
                                    resetForm.errors.passwordConfirm : null}
                            </Alert>
                        </Form.Group>
                        <Button
                            block
                            type="submit"
                            variant="info"
                        >
                            Reset Password
                        </Button>
                    </Form>
                </Card.Body>
                <Card.Footer>
                    <Alert variant={alertType} show={error.length > 0}>{error}</Alert>
                </Card.Footer>
            </Card>
        </div>
    )
    return ( /* Redirects to Profile if already logged in */
        <Redirect to={`/profile`}/>
    )
}

export default ResetPassword;