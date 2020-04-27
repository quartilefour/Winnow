import React, {useState} from 'react';
import {Card, Form, Col, Button, Nav, Tab, Alert} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faUser} from "@fortawesome/free-solid-svg-icons";
import {
    fetchProfileData,
    sendChangePassword,
    sendProfileUpdate,
    profileSchema,
    passwordSchema
} from "../../service/AuthService";
import PageLoader from "../common/PageLoader";
import {useFormik} from "formik";
import {parseAPIError} from "../../service/ApiService";

/**
 * Functional component to render User Profile form.
 *
 * @return {*}
 * @constructor
 */
function Profile() {

    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [userEmail, setUserEmail] = useState("");

    React.useEffect(() => {
        fetchProfileData()
            .then(res => {
                setFirstName(res.firstName);
                setLastName(res.lastName);
                setUserEmail(res.userEmail);
                setIsLoaded(true);
            })
            .catch(error => {
                setError(`Error retrieving profile.\n${parseAPIError(error)}`);
                setIsLoaded(true);
            });
    });

    const profileForm = useFormik({
        enableReinitialize: true,
        initialValues: {
            firstName: firstName,
            lastName: lastName,
            userEmail: userEmail,
        },
        validationSchema: profileSchema,
        onSubmit: values => {
            updateProfile(values)
        }
    })

    const passwordForm = useFormik({
        initialValues: {
            userPassword: '',
            userPasswordNew: '',
            passwordConfirm: ''
        },
        validationSchema: passwordSchema,
        onSubmit: values => {
            changePassword(values)
        }
    })

    /* Updates user name and/or email */
    function updateProfile(values) {
        const userInfo = {
            firstName: values.firstName,
            lastName: values.lastName,
            userEmail: values.userEmail
        };
        sendProfileUpdate(userInfo)
            .then(() => {
                setAlertType("success");
                setError("Profile successfully updated.")
            })
            .catch(error => {
                setAlertType("danger");
                setError(`Profile update error.\n${parseAPIError(error)}`);
            });
    }

    /* Changes user password */
    function changePassword(values) {
        const credentials = {
            userPassword: values.userPassword,
            userPasswordNew: values.userPasswordNew,
            passwordConfirm: values.passwordConfirm
        };
        sendChangePassword(credentials)
            .then(() => {
                setAlertType("success");
                setError("Password successfully changed.")
            })
            .catch(error => {
                setAlertType("danger");
                setError(`Password change error.\n${parseAPIError(error)}`)
            });
    }

    /* Resets form entry errors */
    function resetError() {
        setError('');
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
                        <Tab.Content>
                            <Tab.Pane eventKey="profile" id="profile">
                                <Form onSubmit={profileForm.handleSubmit}>
                                    <Form.Group>
                                        <Form.Row>
                                            <Col>
                                                <Form.Control
                                                    type="text"
                                                    name="firstName"
                                                    autoComplete="given-name"
                                                    value={profileForm.values.firstName}
                                                    onChange={profileForm.handleChange}
                                                    onBlur={profileForm.handleBlur}
                                                    placeholder="First Name"
                                                />
                                            </Col>
                                            <Col>
                                                <Form.Control
                                                    type="text"
                                                    autoComplete="family-name"
                                                    name="lastName"
                                                    value={profileForm.values.lastName}
                                                    onChange={profileForm.handleChange}
                                                    onBlur={profileForm.handleBlur}
                                                    placeholder="Last Name"
                                                />
                                            </Col>
                                        </Form.Row>
                                        <Alert
                                            variant="danger"
                                            show={!!(profileForm.errors.firstName || profileForm.errors.lastName)}
                                        >
                                            {profileForm.touched.firstName && profileForm.errors.firstName ?
                                                profileForm.errors.firstName : null}
                                            {profileForm.touched.lastName && profileForm.errors.lastName ?
                                                profileForm.errors.lastName : null}
                                        </Alert>
                                        <Form.Row>
                                            <Col>
                                                <Form.Control
                                                    type="email"
                                                    autoComplete="username"
                                                    name="userEmail"
                                                    value={profileForm.values.userEmail}
                                                    onChange={profileForm.handleChange}
                                                    onBlur={profileForm.handleBlur}
                                                    placeholder="E-mail Address"
                                                />
                                            </Col>
                                            <Alert
                                                variant="danger"
                                                show={!!(profileForm.touched.userEmail && profileForm.errors.userEmail)}
                                            >
                                                {profileForm.errors.userEmail}
                                            </Alert>
                                        </Form.Row>
                                        <Button
                                            block
                                            type="submit"
                                            disabled={false}
                                            variant="info"
                                            size="sm"
                                        >
                                            Update Profile
                                        </Button>
                                    </Form.Group>
                                </Form>
                            </Tab.Pane>
                            <Tab.Pane eventKey="password" id="password">
                                <Form onSubmit={passwordForm.handleSubmit}>
                                    <Form.Group>
                                        <Form.Row>
                                            <Col>
                                                <Form.Control
                                                    type="password"
                                                    autoComplete="current-password"
                                                    name="userPassword"
                                                    value={passwordForm.values.userPassword}
                                                    onChange={passwordForm.handleChange}
                                                    onBlur={passwordForm.handleBlur}
                                                    placeholder="Current Password"
                                                />
                                            </Col>
                                        </Form.Row>
                                        <Form.Row>
                                            <Col>
                                                <Form.Control
                                                    type="password"
                                                    autoComplete="new-password"
                                                    name="userPasswordNew"
                                                    value={passwordForm.values.userPasswordNew}
                                                    onChange={passwordForm.handleChange}
                                                    onBlur={passwordForm.handleBlur}
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
                                                    value={passwordForm.values.passwordConfirm}
                                                    onChange={passwordForm.handleChange}
                                                    onBlur={passwordForm.handleBlur}
                                                    placeholder="Confirm New Password"
                                                />
                                            </Col>
                                        </Form.Row>
                                        <Alert
                                            variant="danger"
                                            show={!!(passwordForm.errors.userPassword || passwordForm.errors.userPasswordNew || passwordForm.errors.passwordConfirm)}
                                        >
                                            {passwordForm.touched.userPassword && passwordForm.errors.userPassword ?
                                                passwordForm.errors.userPassword : null}
                                            {passwordForm.touched.userPasswordNew && passwordForm.errors.userPasswordNew ?
                                                passwordForm.errors.userPasswordNew : null}
                                            {passwordForm.touched.passwordConfirm && passwordForm.errors.passwordConfirm ?
                                                passwordForm.errors.passwordConfirm : null}
                                        </Alert>
                                        <Button
                                            block
                                            type="submit"
                                            disabled={false}
                                            variant="info"
                                            size="sm"
                                        >
                                            Change Password
                                        </Button>
                                    </Form.Group>
                                </Form>
                            </Tab.Pane>
                        </Tab.Content>
                    </Card.Body>
                    <Card.Footer>
                        <Alert variant={alertType} show={error.length > 0}>{error}</Alert>
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