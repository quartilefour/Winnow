import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faUser } from "@fortawesome/free-solid-svg-icons";
import {useAuth} from "../context/auth";
import AuthService from "../service/AuthService";
import {Nav, Navbar} from "react-bootstrap";
import logoImg from "../img/logo.png";
import {Link, Redirect} from "react-router-dom";

/**
 * Renders the navigation bar for user interface, visible only to
 * authenticated users.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
const NavBar = (props) => {

    const {authToken, setAuthToken} = useAuth();
    const user = AuthService.getUserInfo();

    function logOut() {
        setAuthToken(null);
    }

    return authToken ?
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
                </Nav>
                <Nav.Link as={Link} to={{
                    pathname: '/profile',
                    state: {
                        userEmail: {user}
                    }
                }}>
                    <FontAwesomeIcon icon={faUser} color="blue" />
                </Nav.Link>
                <Nav.Item>
                    ({user})
                </Nav.Item>
                <Nav>
                    <Nav.Link href="#" onClick={logOut}>Log Out</Nav.Link>
                </Nav>
            </Navbar.Collapse>
        </Navbar>
        : null

};

export default NavBar;