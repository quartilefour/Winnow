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
                    <Input type="username" placeholder="username" />
                    <Input type="email" placeholder="email" />
                    <Input type="password" placeholder="password" />
                    <Input type="password" placeholder="password again" />
                    <Button>Register</Button>
                </Form>
                <Link to="/login">Already have an account?</Link>
            </Card>
        );
    }
}

export default Register;