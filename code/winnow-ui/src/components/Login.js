import React, {useEffect, useState} from "react";
import {Link, Redirect} from "react-router-dom";
import {Card, Form, Button} from "react-bootstrap";
import {Error} from './HTMLElements';
import {sendLoginCredentials} from "../service/AuthService";
import logoImg from "../img/logo.png";
import {useAuth} from "../context/auth";

/**
 * Renders Login form and handles response from API.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Login(props) {
    const [isLoggedIn, setLoggedIn] = useState(false);
    const [error, setError] = useState(null);
    const [userEmail, setUserEmail] = useState("");
    const [userPassword, setUserPassword] = useState("");
    const {authToken, setAuthToken} = useAuth();
    const referer = (props.location.state !== undefined) ? props.location.state.referer : '/';

    useEffect(() => {
        if (authToken) {
            console.info(`Login: Have ${authToken}, setting isLoggedIn to 'true'`);
            setLoggedIn(true);
        }
    }, [authToken, isLoggedIn]);

    /* Submits user credentials to API login endpoint. */
    function postLogin() {
        const credentials = {userEmail: userEmail, userPassword: userPassword};
        sendLoginCredentials(credentials).then(res => {
                console.log(`Return status from API: ${res}`);
                setAuthToken(res);
                setLoggedIn(true);
        }).catch(error => {
                console.log(`Login error: ${error}`);
                setError("Invalid E-mail or password");
        });
    }

    if (!isLoggedIn) {
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
                        <Form>
                            <Error>{error}</Error>
                            <Form.Group>
                                <Form.Control
                                    type="email"
                                    autoComplete="username"
                                    name="userEmail"
                                    value={userEmail}
                                    onChange={e => {
                                        setUserEmail(e.target.value);
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
                        <Link to="/register">Don't have an account?</Link>
                    </Card.Footer>
                </Card>
            </div>
        )
    } else {
        return (
            <Redirect to={referer}/>
        )
    }
}

export default Login;