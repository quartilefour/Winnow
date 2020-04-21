import React, {useEffect, useState} from "react";
import {Redirect} from "react-router-dom";
import {Card, Form, Button, Col, Alert} from "react-bootstrap";
import {sendRegistration} from "../../service/AuthService";
import logoImg from "../../img/logo.png";

/**
 * Functional component to render Registration form and handle responses from API.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Register(props) {

    const [isRegistered, setIsRegistered] = useState(false);
    const [error, setError] = useState(null);
    const [alertType, setAlertType] = useState('');
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [userEmail, setUserEmail] = useState("");
    const [userPassword, setUserPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");

    useEffect(() => {

    });

    /* Submits new user data to the API registration endpoint. */
    function postRegistration() {
        const credentials = {
            firstName: firstName,
            lastName: lastName,
            userEmail: userEmail,
            userPassword: userPassword,
            passwordConfirm: passwordConfirm
        };
        sendRegistration(credentials).then(res => {
            setIsRegistered(true);
        })
            .catch(error => {
                setAlertType("danger");
                setError(error.toString());
            });
    }

    /* Displays Registration form */
    if (!isRegistered) {
        return (
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
                        <Form.Row>
                            <Form.Group as={Col}>
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
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Control
                                    type="text"
                                    name="lastName"
                                    autoComplete="family-name"
                                    value={lastName}
                                    onChange={e => {
                                        setLastName(e.target.value);
                                    }}
                                    placeholder="Last Name"
                                />
                            </Form.Group>
                        </Form.Row>
                        <Form.Group>
                            <Form.Control
                                type="email"
                                name="userEmail"
                                autoComplete="username"
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
                                name="userPassword"
                                autoComplete="new-password"
                                value={userPassword}
                                onChange={e => {
                                    setUserPassword(e.target.value);
                                }}
                                placeholder="Password"
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Control
                                type="password"
                                autoComplete="new-password"
                                name="passwordConfirm"
                                value={passwordConfirm}
                                onChange={e => {
                                    setPasswordConfirm(e.target.value);
                                }}
                                placeholder="Confirm Password"
                            />
                        </Form.Group>
                        <Button
                            block
                            variant="info"
                            onClick={postRegistration}
                        >
                            Register
                        </Button>
                    </Form>
                </Card.Body>
                <Card.Footer>
                    <Alert variant={alertType}>{error}</Alert>
                    <a href="/login">Already have an account?</a>
                </Card.Footer>
            </Card>
        )
        /* Redirect to Login page after successful registration */
    } else {
        return (
            <Redirect to="/login"/>
        )

    }
}

export default Register;