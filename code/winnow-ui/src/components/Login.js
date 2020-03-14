import React, {Component} from "react";
import {Link} from "react-router-dom";
import {Card, Logo, Form, Input, Button, Error} from '../components/AuthForm';
import AuthService from "../service/AuthService";
import logoImg from "../img/logo.png";

class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            message: '',
        };
        this.login =this.login.bind(this);
    }
    componentDidMount() {
        localStorage.clear();
    }

    login = (e) => {
        localStorage.setItem("userInfo", JSON.stringify({"username": "esantora"}));
        this.props.history.push('/');
        return;
        e.preventDefault();
        const credentials = {username: this.state.username, password: this.state.password};
        AuthService.login(credentials).then(res => {
            if(res.data.status === 200){
                localStorage.setItem("userInfo", JSON.stringify(res.data.result));
                this.props.history.push('/');
            }else {
                this.setState({message: res.data.message});
            }
        });
    };

    onChange = (e) =>
        this.setState({ [e.target.name]: e.target.value });

    render() {
        return (
            <div>
                <Card>
                    <Logo src={logoImg}/>
                    <Form>
                        <Error>{this.state.message}</Error>
                        <Input
                            type="username"
                            value={this.state.username}
                            onChange={
                                this.onChange
                            }
                            placeholder="username"
                        />
                        <Input
                            type="password"
                            value={this.state.password}
                            onChange={
                                this.onChange
                            }
                            placeholder="password"
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