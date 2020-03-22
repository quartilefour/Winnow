import React from 'react';
import {useAuth} from "../context/auth";
import {Nav, Navbar} from "react-bootstrap";
import logoImg from "../img/logo.png";
import {Span} from "./AuthForm";

const NavBar = (props) => {

    const {authToken, setAuthToken} = useAuth();

    function logOut() {
        setAuthToken(null);
    }

    return authToken ?
        <Navbar bg="light" expand="lg">
            <Navbar.Brand href="/">
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
                    <Nav.Link href="/">Home</Nav.Link>
                </Nav>
                <Nav>
                    <Span>(me@example.com)</Span>
                    <Nav.Link href="#" onClick={logOut}>Log Out</Nav.Link>
                </Nav>
            </Navbar.Collapse>
        </Navbar>
        : null

};

export default NavBar;