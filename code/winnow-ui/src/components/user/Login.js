import React, {useEffect, useState} from "react";
import {Redirect} from "react-router-dom";
import {Card, Form, Button, Alert} from "react-bootstrap";
import {sendLoginCredentials} from "../../service/AuthService";
import logoImg from "../../img/logo.png";
import {useAuth} from "../../context/auth";
import {createSearchHistory} from "../../service/SearchService";

/**
 * Functional component to render Login form and handle response from API.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Login(props) {
    const [isLoggedIn, setLoggedIn] = useState(false);
    const [error, setError] = useState(null);
    const [alertType, setAlertType] = useState('');
    const [userEmail, setUserEmail] = useState("");
    const [userPassword, setUserPassword] = useState("");
    const {authToken, setAuthToken} = useAuth();
    const referer = (props.location.state !== undefined) ? props.location.state.referer : '/';

    useEffect(() => {
        if (authToken) {
            setLoggedIn(true);
        }
    }, [authToken, isLoggedIn]);

    /* Submits user credentials to API login endpoint. */
    function postLogin() {
        const credentials = {userEmail: userEmail, userPassword: userPassword};
        sendLoginCredentials(credentials).then(res => {
            setAuthToken(res);
            createSearchHistory();
            setLoggedIn(true);
        }).catch(error => {
            setAlertType("danger");
            setError("Invalid E-mail or password");
        });
    }

    /* Validates user input before submitting */
    function validateForm(e) {
        if (e.keyCode === 13) {
            if (userEmail !== "" && userPassword !== "") {
                postLogin()
            }
        }
    }

    /* Displays Login form */
    if (!isLoggedIn) {
        return (
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
                        <Form>
                            <Form.Group>
                                <Form.Control
                                    type="email"
                                    autoComplete="username"
                                    name="userEmail"
                                    value={userEmail}
                                    onChange={e => {
                                        setUserEmail(e.target.value);
                                    }}
                                    onKeyUp={e => {
                                        validateForm(e)
                                    }}
                                    placeholder="E-mail Address"
                                />
                            </Form.Group>
                            <Form.Group>
                                <Form.Control
                                    type="password"
                                    autoComplete="current-password"
                                    name="userPassword"
                                    value={userPassword}
                                    onChange={e => {
                                        setUserPassword(e.target.value);
                                    }}
                                    onKeyUp={e => {
                                        validateForm(e)
                                    }}
                                    placeholder="Password"
                                />
                            </Form.Group>
                            <Button
                                block
                                variant="info"
                                onClick={postLogin}>Login</Button>
                        </Form>
                    </Card.Body>
                    <Card.Footer>
                        <Alert variant={alertType}>{error}</Alert>
                        <a href="/register">Don't have an account?</a>
                    </Card.Footer>
                </Card>
            </div>
        )
    /* Redirects after successful log in */
    } else {
        return (
            <Redirect to={referer}/>
        )
    }
}

export default Login;