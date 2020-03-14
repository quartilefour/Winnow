import React, {Component} from 'react';
import {Nav, Navbar} from "react-bootstrap";
import logoImg from "../img/logo.png";

class NavBar extends Component {
    render() {
        return (
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
                        <Nav.Link href="/logout">Log Out</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );
    }
}

export default NavBar;