import React, {Component} from 'react';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";
import NavBar from "./components/NavBar";
import Login from "./components/Login";
import Register from "./components/Register";
import Home from "./components/Home";
import './App.css';

class App extends Component {
    render() {
        return (
            <Router>
                <div>
                    <NavBar/>
                    <div className="auth-wrapper">
                        <div className="auth-inner">
                            <Switch>
                                <Route exact path="/" component={Home}/>
                                <Route path="/login" component={Login}/>
                                <Route path="/register" component={Register}/>
                            </Switch>
                        </div>
                    </div>
                </div>
            </Router>
        );
    }
}

export default App;
