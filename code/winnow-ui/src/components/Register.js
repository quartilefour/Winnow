import React, {useState} from "react";
import {Link, Redirect} from "react-router-dom";
import {Card, Logo, Form, Input, Button, Error} from "./AuthForm";
import AuthService from "../service/AuthService";
import logoImg from "../img/logo.png";

function Register(props) {

    const [isRegistered, setIsRegistered] = useState(false);
    const [error, setError] = useState(null);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [userEmail, setUserEmail] = useState("");
    const [userPassword, setUserPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");

    function postRegistration() {
        const credentials = {
            firstName: firstName,
            lastName: lastName,
            userEmail: userEmail,
            userPassword: userPassword,
            passwordConfirm: passwordConfirm
        };
        AuthService.register(credentials).then(res => {
            if (res.status === 201) {
                setIsRegistered(true);
            } else {
                console.log("Non-200 status from API: " + JSON.stringify(res));
                setError(res.statusText);
            }
        })
            .catch(error => {
                console.log("Registration: " + error.toString());
                setError(error.toString());
            });
    }

    if (isRegistered) {
        console.log(`Successfully registered: ${userEmail}`);
        return  <Redirect to="/login" />;
    }

    return (
        <Card>
            <Logo src={logoImg}/>
            <Form>
                <Error>{error}</Error>
                <Input
                    type="text"
                    name="firstName"
                    value={firstName}
                    onChange={e => {
                        setFirstName(e.target.value);
                    }}
                    placeholder="First Name"
                />
                <Input
                    type="text"
                    name="lastName"
                    value={lastName}
                    onChange={e => {
                        setLastName(e.target.value);
                    }}
                    placeholder="Last Name"
                />
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
                <Input
                    type="password"
                    name="passwordConfirm"
                    value={passwordConfirm}
                    onChange={e => {
                        setPasswordConfirm(e.target.value);
                    }}
                    placeholder="Confirm Password"
                />
                <Button onClick={postRegistration}>Register</Button>
            </Form>
            <Link to="/login">Already have an account?</Link>
        </Card>
    );
}

export default Register;