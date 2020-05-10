import React, {useState} from "react";
import {Link, Redirect, useLocation} from "react-router-dom";
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
    const [isTokenValid, setIsTokenValid] = useState(true);
    const {authToken} = useAuth();

    const query = new URLSearchParams(useLocation().search);
    console.info(`ResetPassword: ${JSON.stringify(useLocation().search)}`)

    React.useEffect(() => {
        let token = query.get("token")
        console.info(`ResetPassword: token: ${token}`)
        if (token === undefined || token === '') {
            setIsTokenValid(false)
        }
        if (authToken) {
            setLoggedIn(true);
        }
    }, [authToken, isLoggedIn, query]);

    const resetForm = useFormik({
        initialValues: {
            userPasswordNew: '',
            passwordConfirm: '',
            token: query.get("token")
        },
        validationSchema: resetSchema,
        onSubmit: values => {
            postResetPassword(values)
        },
    });

    /* Submits user credentials to API login endpoint. */
    function postResetPassword(values) {
        const credentials = {
            userPasswordNew: values.userPasswordNew,
            passwordConfirm: values.passwordConfirm,
            token: values.token
        };
        resetPassword(credentials)
            .then(() => {
                setAlertType('success')
                setError('')
            })
            .catch(error => {
                setAlertType("danger");
                if (error.response.status >= 400 || error.response.status <= 499) {
                    setError(`${error.response.data.error}`);
                } else {
                    setError(`Server error: ${parseAPIError(error)}`);
                }
            });
    }

    if (!isTokenValid) return (
        <Redirect to={{
            pathname: `/error`,
            state: {errorMessage: 'Your Password Reset token is invalid!'}
        }}/>
    )


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
                                type="hidden"
                                name="token"
                                value={resetForm.values.token}
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Control
                                type="password"
                                name="userPasswordNew"
                                autoComplete="new-password"
                                value={resetForm.values.userPasswordNew}
                                onChange={resetForm.handleChange}
                                onBlur={resetForm.handleBlur}
                                placeholder="New Password"
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
                                variant={!!(resetForm.errors.userPasswordNew || resetForm.errors.passwordConfirm) ? "danger" : ""}
                                show={!!(resetForm.errors.userPasswordNew || resetForm.errors.passwordConfirm)}
                            >
                                {resetForm.touched.userPasswordNew && resetForm.errors.userPasswordNew ?
                                    resetForm.errors.userPasswordNew : null}
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
                    <Alert variant={alertType} show={alertType === "success"}>
                        Password reset successfully, please <Alert.Link href="/login" title="Login">login</Alert.Link>.
                    </Alert>
                </Card.Footer>
            </Card>
        </div>
    )
    return ( /* Redirects to Profile if already logged in */
        <Redirect to={`/profile`}/>
    )
}

export default ResetPassword;