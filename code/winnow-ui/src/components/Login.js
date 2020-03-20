import React, {Component} from "react";
import {Link} from "react-router-dom";
import {Card, Logo, Form, Input, Button, Error} from '../components/AuthForm';
import AuthService from "../service/AuthService";
import logoImg from "../img/logo.png";

class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {
            userEmail: '',
            userPassword: '',
            message: '',
        };
        this.login = this.login.bind(this);
    }

    componentDidMount() {
        localStorage.clear();
    }

    login = (e) => {
        localStorage.setItem("userInfo", JSON.stringify({"username": "esantora"}));
        e.preventDefault();
        const credentials = {userEmail: this.state.userEmail, userPassword: this.state.userPassword};
        AuthService.login(credentials).then(res => {
            if (res.status === 200) {
                let token = JSON.stringify(res.headers['authorization'].split(' ')[1]);
                console.log("Return status from API: " + JSON.stringify(AuthService.parseToken(token)));
                localStorage.setItem("userInfo", token);
                this.props.history.push('/');
            } else if (res.status === 403) {
                this.setState({message: "Invalid E-mail or password"});
            } else {
                console.log("Non-200 status from API: " + JSON.stringify(res));
                this.setState({message: res.statusText});
            }
        })
            .catch(error => {
                console.log("Login error: " + error);
                this.setState({['message']: error.toString()});
            });
    };

    onChange = (e) => this.setState({[e.target.name]: e.target.value});

    render() {
        return (
            <div>
                <Card>
                    <Logo src={logoImg}/>
                    <Form>
                        <Error>{this.state.message}</Error>
                        <Input
                            type="email"
                            name="userEmail"
                            value={this.state.username}
                            onChange={
                                this.onChange
                            }
                            placeholder="E-mail Address"
                        />
                        <Input
                            type="password"
                            name="userPassword"
                            value={this.state.password}
                            onChange={
                                this.onChange
                            }
                            placeholder="Password"
                        />
                        <Button onClick={this.login}>Login</Button>
                    </Form>
                    <Link to="/register">Don't have an account?</Link>
                </Card>
            </div>
        );
    }
}

export default Login;