import React, {useState} from 'react';
import {Card, Form, Col, Button, Nav, Tab, Alert} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faUser} from "@fortawesome/free-solid-svg-icons";
import {fetchProfileData, sendChangePassword, sendProfileUpdate} from "../../service/AuthService";
import PageLoader from "../common/PageLoader";

/**
 * Functional component to render User Profile form.
 *
 * @return {*}
 * @constructor
 */
function Profile() {

    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);
    const [alertType, setAlertType] = useState('');
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [userEmail, setUserEmail] = useState("");
    const [userCurrentPassword, setCurrentUserPassword] = useState("");
    const [userNewPassword, setNewUserPassword] = useState("");
    const [newPasswordConfirm, setNewPasswordConfirm] = useState("");

    React.useEffect(() => {
        if (error) {
            (error === "Success")
                ? setAlertType("success")
                : setAlertType("danger")
        }
        fetchProfileData()
            .then(res => {
                setFirstName(res.firstName);
                setLastName(res.lastName);
                setUserEmail(res.userEmail);
                setIsLoaded(true);
            }).catch(err => {
            setError(err);
            setIsLoaded(true);
        });
    }, [error, alertType]);

    /* Updates user name and/or email */
    function updateProfile() {
        const userInfo = {
            firstName: firstName,
            lastName: lastName,
            userEmail: userEmail
        };
        sendProfileUpdate(userInfo)
            .then(res => {
                setAlertType("success");
                setError("Profile successfully updated.")
            })
            .catch(err => {
                setAlertType("danger");
                setError(err);
            });
    }

    /* Changes user password */
    function changePassword() {
        const credentials = {
            userPassword: userNewPassword,
            passwordConfirm: newPasswordConfirm
        };
        sendChangePassword(credentials)
            .then(res => {
                setAlertType("success");
                setError("Password successfully changed.")
            })
            .catch(err => {
                setAlertType("danger");
                setError(err)
            });
    }

    /* Resets form entry errors */
    function resetError() {
        setError(null);
        setAlertType('');
    }

    /* Displays current Profile data once loaded from API */
    if (isLoaded) {
        return (
            <Card
                border="info"
                className="text-center tab entry-form"
                style={{
                    flexDirection: 'column',
                    maxWidth: '410px',
                    display: 'flex',
                    margin: '10% auto',
                    width: '50%'
                }}>
                <Card.Title>User Profile</Card.Title>
                <FontAwesomeIcon
                    icon={faUser}
                    color="cornflowerblue"
                    size="6x"
                    style={{margin: '10px auto', width: '50%'}}
                />
                <Tab.Container defaultActiveKey="profile" onSelect={resetError}>
                    <Card.Header>
                        <Nav variant="tabs" fill>
                            <Nav.Item>
                                <Nav.Link eventKey="profile">Profile</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="password">Password</Nav.Link>
                            </Nav.Item>
                        </Nav>
                    </Card.Header>
                    <Card.Body>
                        <Form>
                            <Tab.Content>
                                <Tab.Pane eventKey="profile" id="profile">
                                    <Form.Group>
                                        <Form.Row>
                                            <Col>
                                                <Form.Control
                                                    type="text"
                                                    name="firstName"
                                                    autoComplete="given-name"
                                                    value={firstName}
                                                    onChange={e => {
                                                        setFirstName(e.target.value);
                                                    }}
                                                    placeholder="First Name"
                                                />
                                            </Col>
                                            <Col>
                                                <Form.Control
                                                    type="text"
                                                    autoComplete="family-name"
                                                    name="lastName"
                                                    value={lastName}
                                                    onChange={e => {
                                                        setLastName(e.target.value);
                                                    }}
                                                    placeholder="Last Name"
                                                />
                                            </Col>
                                        </Form.Row>
                                        <Form.Row>
                                            <Col>
                                                <Form.Control
                                                    type="email"
                                                    autoComplete="username"
                                                    name="userEmail"
                                                    value={userEmail ? userEmail : undefined}
                                                    onChange={e => {
                                                        setUserEmail(e.target.value);
                                                    }}
                                                    placeholder="E-mail Address"
                                                />
                                            </Col>
                                        </Form.Row>
                                        <Button
                                            block
                                            onClick={updateProfile}
                                            disabled={false}
                                            variant="info"
                                            size="sm"
                                        >
                                            Update Profile
                                        </Button>
                                    </Form.Group>
                                </Tab.Pane>
                                <Tab.Pane eventKey="password" id="password">
                                    <Form.Group>
                                        <Form.Row>
                                            <Col>
                                                <Form.Control
                                                    type="password"
                                                    autoComplete="current-password"
                                                    name="userPasswordOld"
                                                    value={userCurrentPassword}
                                                    onChange={e => {
                                                        setCurrentUserPassword(e.target.value);
                                                    }}
                                                    placeholder="Current Password"
                                                />
                                            </Col>
                                        </Form.Row>
                                        <Form.Row>
                                            <Col>
                                                <Form.Control
                                                    type="password"
                                                    autoComplete="new-password"
                                                    name="userPassword"
                                                    value={userNewPassword}
                                                    onChange={e => {
                                                        setNewUserPassword(e.target.value);
                                                    }}
                                                    placeholder="New Password"
                                                />
                                            </Col>
                                        </Form.Row>
                                        <Form.Row>
                                            <Col>
                                                <Form.Control
                                                    type="password"
                                                    autoComplete="new-password"
                                                    name="passwordConfirm"
                                                    value={newPasswordConfirm}
                                                    onChange={e => {
                                                        setNewPasswordConfirm(e.target.value);
                                                    }}
                                                    placeholder="Confirm New Password"
                                                />
                                            </Col>
                                        </Form.Row>
                                        <Button
                                            block
                                            onClick={changePassword}
                                            disabled={false}
                                            variant="info"
                                            size="sm"
                                        >
                                            Change Password
                                        </Button>
                                    </Form.Group>
                                </Tab.Pane>
                            </Tab.Content>
                        </Form>
                    </Card.Body>
                    <Card.Footer>
                        <Alert variant={alertType}>{error}</Alert>
                    </Card.Footer>
                </Tab.Container>
            </Card>
        )
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default Profile;