import React, {useState} from "react";
import {Link, Redirect} from "react-router-dom";
import {Card, Form, Button, Col, Alert} from "react-bootstrap";
import {registerSchema, sendRegistration} from "../../service/AuthService";
import logoImg from "../../img/logo.png";
import {useFormik} from "formik";
import {parseAPIError} from "../../service/ApiService";

/**
 * Functional component to render Registration form and handle responses from API.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Register(props) {

    const [isRegistered, setIsRegistered] = useState(false);
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');

    const registrationForm = useFormik({
        initialValues: {
            firstName: '',
            lastName: '',
            userEmail: '',
            userPassword: '',
            passwordConfirm: ''
        },
        validationSchema: registerSchema,
        onSubmit: values => {
            postRegistration(values)
        }
    })

    /* Submits new user data to the API registration endpoint. */
    function postRegistration(values) {
        const credentials = {
            firstName: values.firstName,
            lastName: values.lastName,
            userEmail: values.userEmail,
            userPassword: values.userPassword,
            passwordConfirm: values.passwordConfirm
        };
        sendRegistration(credentials)
            .then(() => {
                setIsRegistered(true);
            })
            .catch(error => {
                setAlertType("danger");
                if (error.response.status === 409) {
                    setError(`${error.response.data}`);
                } else {
                    setError(`Server error: ${parseAPIError(error)}`);
                }
            });
    }

    if (!isRegistered) { /* Displays Registration form */
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
                    <Form onSubmit={registrationForm.handleSubmit}>
                        <Form.Row>
                            <Form.Group as={Col}>
                                <Form.Control
                                    aria-placeholder="First Name"
                                    type="text"
                                    name="firstName"
                                    autoComplete="given-name"
                                    value={registrationForm.values.firstName}
                                    onChange={registrationForm.handleChange}
                                    onBlur={registrationForm.handleBlur}
                                    placeholder="First Name"
                                />
                            </Form.Group>
                            <Form.Group as={Col}>
                                <Form.Control
                                    type="text"
                                    name="lastName"
                                    autoComplete="family-name"
                                    value={registrationForm.values.lastName}
                                    onChange={registrationForm.handleChange}
                                    onBlur={registrationForm.handleBlur}
                                    placeholder="Last Name"
                                />
                            </Form.Group>
                        </Form.Row>
                        <Alert
                            variant="danger"
                            show={!!(registrationForm.errors.firstName || registrationForm.errors.lastName)}
                        >
                            {registrationForm.touched.firstName && registrationForm.errors.firstName ?
                                registrationForm.errors.firstName : null}
                            {registrationForm.touched.lastName && registrationForm.errors.lastName ?
                                registrationForm.errors.lastName : null}
                        </Alert>
                        <Form.Group>
                            <Form.Control
                                type="email"
                                name="userEmail"
                                autoComplete="username"
                                value={registrationForm.values.userEmail}
                                onChange={registrationForm.handleChange}
                                onBlur={registrationForm.handleBlur}
                                placeholder="E-mail Address"
                            />
                            <Alert
                                variant="danger"
                                show={!!(registrationForm.touched.userEmail && registrationForm.errors.userEmail)}
                            >
                                {registrationForm.errors.userEmail}
                            </Alert>
                        </Form.Group>
                        <Form.Group>
                            <Form.Control
                                type="password"
                                name="userPassword"
                                autoComplete="new-password"
                                value={registrationForm.values.userPassword}
                                onChange={registrationForm.handleChange}
                                onBlur={registrationForm.handleBlur}
                                placeholder="Password"
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Control
                                type="password"
                                autoComplete="new-password"
                                name="passwordConfirm"
                                value={registrationForm.values.passwordConfirm}
                                onChange={registrationForm.handleChange}
                                onBlur={registrationForm.handleBlur}
                                placeholder="Confirm Password"
                            />
                            <Alert
                                variant="danger"
                                show={!!(registrationForm.errors.userPassword || registrationForm.errors.passwordConfirm)}
                            >
                                {registrationForm.touched.userPassword && registrationForm.errors.userPassword ?
                                    registrationForm.errors.userPassword : null}
                                {registrationForm.touched.passwordConfirm && registrationForm.errors.passwordConfirm ?
                                    registrationForm.errors.passwordConfirm : null}
                            </Alert>
                        </Form.Group>
                        <Button
                            block
                            type="submit"
                            variant="info"
                        >
                            Register
                        </Button>
                    </Form>
                </Card.Body>
                <Card.Footer>
                    <Alert variant={alertType} show={error.length > 0}>{error}</Alert>
                    <Link to="/login" title="Log in with an existing account">
                        Already have an account?
                    </Link>
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