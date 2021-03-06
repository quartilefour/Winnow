import React, {useState} from "react";
import PropTypes from 'prop-types';
import {Redirect, Link} from "react-router-dom";
import {Card, Button, Form, Alert} from "react-bootstrap";
import {sendLoginCredentials, loginSchema} from "../service/AuthService";
import logoImg from "../img/logo.png";
import {useAuth} from "../context/auth";
import {createSearchHistory} from "../service/SearchService";
import {useFormik} from "formik";
import {parseAPIError} from "../service/ApiService";

/**
 * Functional component to render Login form and handle response from API.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Login(props) {

    Login.propTypes = {
        location: PropTypes.object
    }

    const {location} = props;


    const [isLoggedIn, setLoggedIn] = useState(false);
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');
    const {authToken, setAuthToken} = useAuth();
    const referer = (location.state !== undefined) ? location.state.referer : '/';

    React.useEffect(() => {
        if (authToken) {
            setLoggedIn(true);
        }
    }, [authToken, isLoggedIn]);

    const loginForm = useFormik({
        initialValues: {
            userEmail: '',
            userPassword: '',
        },
        validationSchema: loginSchema,
        onSubmit: values => {
            postLogin(values)
        },
    });

    /* Submits user credentials to API login endpoint. */
    function postLogin(values) {
        const credentials = {
            userEmail: values.userEmail,
            userPassword: values.userPassword
        };
        sendLoginCredentials(credentials)
            .then(res => {
                setAuthToken(res.headers['authorization'].split(' ')[1]);
                createSearchHistory();
                setLoggedIn(true);
            })
            .catch(error => {
                setAlertType("danger");
                if (error.response.status >= 400 || error.response.status <= 499) {
                    setError(`Invalid E-mail or password`);
                } else {
                    setError(`Server error: ${parseAPIError(error)}`);
                }
            });
    }

    /* Displays Login form */
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
                    <Form onSubmit={loginForm.handleSubmit}>
                        <Form.Group className="form-group">
                            <Form.Control
                                autoComplete="username"
                                id="userEmail"
                                className="form-control"
                                name="userEmail"
                                placeholder="E-mail Address"
                                aria-placeholder="E-mail Address"
                                type="email"
                                onChange={loginForm.handleChange}
                                onBlur={loginForm.handleBlur}
                                value={loginForm.values.userEmail}
                            />
                            {/* <Form.Control.Feedback /> */}
                            <Alert
                                variant="danger"
                                show={!!(loginForm.touched.userEmail && loginForm.errors.userEmail)}
                            >
                                {loginForm.errors.userEmail}
                            </Alert>
                        </Form.Group>
                        <Form.Group>
                            <Form.Control
                                autoComplete="current-password"
                                id="userPassword"
                                className="form-control"
                                name="userPassword"
                                placeholder="Password"
                                aria-placeholder="Password"
                                type="password"
                                onChange={loginForm.handleChange}
                                onBlur={loginForm.handleBlur}
                                value={loginForm.values.userPassword}
                            />
                            <Alert
                                variant="danger"
                                show={!!(loginForm.touched.userPassword && loginForm.errors.userPassword)}
                            >
                                {loginForm.errors.userPassword}
                            </Alert>
                        </Form.Group>
                        <Button
                            block
                            type="submit"
                            variant="info"
                        >
                            Login
                        </Button>
                    </Form>
                </Card.Body>
                <Card.Footer>
                    <Alert variant={alertType} show={error.length > 0}>{error}</Alert>
                    <Link to="/register" title="Register for an account">
                        Don't have an account?
                    </Link>
                    <span className="sep"/>
                    <Link to="/forgot" title="Forgot password">
                        Forgot password?
                    </Link>
                </Card.Footer>
            </Card>
        </div>
    )
    return ( /* Redirects after successful log in */
        <Redirect to={referer}/>
    )
}

export default Login;