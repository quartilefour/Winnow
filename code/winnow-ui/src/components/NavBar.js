import React, {useEffect, useState} from 'react';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faUser} from "@fortawesome/free-solid-svg-icons";
import {useAuth} from "../context/auth";
import AuthService from "../service/AuthService";
import {Nav, Navbar} from "react-bootstrap";
import logoImg from "../img/logo.png";
import {Link} from "react-router-dom";

/**
 * Renders the navigation bar for user interface, visible only to
 * authenticated users.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
const NavBar = () => {

    const {authToken, setAuthToken} = useAuth();
    const [isLoggingOut, setIsLoggingOut] = useState(false);
    const [user, setUser] = useState(null);
    const [isTokenValid, setIsTokenValid] = useState(false);

    useEffect(() => {
        if (isLoggingOut) {
            console.info(`NavBar: Clearing authToken: ${authToken}`);
            setAuthToken(null);
            console.info(`NavBar: Clearing authToken: ${authToken}`);

        }
        if (!AuthService.isTokenExpired()) {
            setIsTokenValid(true);
            setUser(AuthService.getUserInfo());
            console.info(`NavBar: Valid token: ${isTokenValid}`);
        }
    }, [isTokenValid, isLoggingOut, authToken, setAuthToken]);

    function logOut() {
        setIsLoggingOut(true);
        console.info(`NavBar: Logging out: ${isLoggingOut}`);
    }

    if (authToken) {
        return (
            <Navbar bg="light" expand="lg">
                <Navbar.Brand as={Link} to="/">
                    <img
                        alt={"Winnow Logo"}
                        src={logoImg}
                        width="30"
                        height="30"
                        className="d-inline-block align-top"
                    />{' '}
                    Winnow</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="mr-auto">
                        <Nav.Link as={Link} to="/">Dashboard</Nav.Link>
                        <Nav.Link as={Link} to="/admin">Admin</Nav.Link>
                    </Nav>
                    <Nav.Link as={Link} to='/profile'>
                        <FontAwesomeIcon icon={faUser} color="cornflowerblue"/>
                    </Nav.Link>
                    <Nav.Item>
                        ({user})
                    </Nav.Item>
                    <Nav>
                        <Nav.Link href="#" onClick={logOut}>Log Out</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        )
    } else {
        return null
    }

};

export default NavBar;