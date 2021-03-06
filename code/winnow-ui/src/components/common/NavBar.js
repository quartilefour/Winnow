import React, {useState} from 'react';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faQuestionCircle} from "@fortawesome/free-regular-svg-icons";
import {faUser, faSignOutAlt} from "@fortawesome/free-solid-svg-icons";
import {useAuth} from "../../context/auth";
import {isTokenExpired, fetchProfileData} from "../../service/AuthService";
import {Nav, Navbar} from "react-bootstrap";
import logoImg from "../../img/logo.png";
import {Link} from "react-router-dom";

/**
 * Renders the navigation bar for user interface, visible only to
 * authenticated users.
 *
 * @returns {*}
 * @constructor
 */
const NavBar = () => {

    const {authToken, setAuthToken} = useAuth();
    const [isLoggingOut, setIsLoggingOut] = useState(false);
    const [user, setUser] = useState(null);
    const [isTokenValid, setIsTokenValid] = useState(false);

    React.useEffect(() => {
        if (isLoggingOut) {
            setAuthToken(null);
            setIsLoggingOut(false);
        }
        if (!isTokenExpired()) {
            setIsTokenValid(true);
            fetchProfileData()
                .then(res => {
                    setUser(res.data.userEmail)
                })
                .catch(() => {
                    setUser('Unknown')
                })
        }
    }, [isTokenValid, isLoggingOut, authToken, setAuthToken]);

    /* Logs user out of application, clears cookie and session */
    function logOut() {
        setIsLoggingOut(true);
    }

    if (authToken) return (
        <Navbar id="winnow-nav" expand="md">
            <Navbar.Brand as={Link} to="/">
                W<img
                alt={"Winnow Logo"}
                src={logoImg}
                width="30"
                height="30"
                className="d-inline-block align-top"
            />N N O W</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav"/>
            <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="mr-auto">
                    <Nav.Link as={Link} to="/">Dashboard</Nav.Link>
                    {/* <Nav.Link as={Link} to="/admin">Admin</Nav.Link> */}
                </Nav>
                <Nav.Link as={Link} to='/support'>
                    <FontAwesomeIcon icon={faQuestionCircle} color="cornflowerblue" title="Help"/>
                </Nav.Link>
                <Nav.Link as={Link} to='/profile'>
                    <FontAwesomeIcon icon={faUser} color="cornflowerblue" title="Profile"/>
                </Nav.Link>
                <Nav.Item>
                    ({user})
                </Nav.Item>
                <Nav>
                    <Nav.Link href="#" onClick={logOut} title="Log Out">
                        <FontAwesomeIcon icon={faSignOutAlt} color="cornflowerblue" title="Log Out"/>
                    </Nav.Link>
                </Nav>
            </Navbar.Collapse>
        </Navbar>
    )
    return (<Navbar/>)

};

export default NavBar;