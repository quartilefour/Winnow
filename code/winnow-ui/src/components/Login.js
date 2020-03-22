import React, {useState} from "react";
import {Link, Redirect} from "react-router-dom";
import {Card, Logo, Form, Input, Button, Error} from './HTMLElements';
import AuthService from "../service/AuthService";
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
    const {setAuthToken} = useAuth();
    const referer = props.location.state.referer || '/';


    /* Submits user credentials to API login endpoint. */
    function postLogin() {
        const credentials = {userEmail: userEmail, userPassword: userPassword};
        AuthService.login(credentials).then(res => {
            if (res.status === 200) {
                let token = res.headers['authorization'].split(' ')[1];
                console.log("Return status from API: " + AuthService.parseToken(token));
                setAuthToken(token);
                setLoggedIn(true);
            } else {
                console.log("Non-200 status from API: " + JSON.stringify(res));
                setError(res.statusText);
            }
        }).catch(error => {
                if (error.response.status === 403) {
                    console.log("Login error: " + error);
                    setError("Invalid E-mail or password");
                } else {
                    console.log("Error: " + error.toString());
                    setError(error.toString());
                }
            });
    }

    /* Redirects successfully authenticated user to requested page */
    if (isLoggedIn) {
        console.info(`Login.js: Logging ${userEmail} in...`)
        return <Redirect to={referer}/>;
    }


    return (
        <div>
            <Card>
                <Logo src={logoImg}/>
                <Form>
                    <Error>{error}</Error>
                    <Input
                        type="email"
                        name="userEmail"
                        value={userEmail}
                        onChange={e => {
                            setUserEmail(e.target.value);
                        }}
                        placeholder="E-mail Address"
                    />
                    <Input
                        type="password"
                        name="userPassword"
                        value={userPassword}
                        onChange={e => {
                            setUserPassword(e.target.value);
                        }}
                        placeholder="Password"
                    />
                    <Button onClick={postLogin}>Login</Button>
                </Form>
                <Link to="/register">Don't have an account?</Link>
            </Card>
        </div>
    );
}

export default Login;