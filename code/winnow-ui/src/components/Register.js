import React, { Component } from "react";
import {Link} from "react-router-dom";
import {Card, Logo, Form, Input, Button} from "./AuthForm";
import logoImg  from "../img/logo.png";

class Register extends Component {
    render() {
        return (
            <Card>
                <Logo src={logoImg} />
                <Form>
                    <Input type="name" placeholder="First Name" />
                    <Input type="name" placeholder="Last Name" />
                    <Input type="email" placeholder="E-mail Address" />
                    <Input type="password" placeholder="Password" />
                    <Input type="password" placeholder="Confirm password" />
                    <Button>Register</Button>
                </Form>
                <Link to="/login">Already have an account?</Link>
            </Card>
        );
    }
}

export default Register;