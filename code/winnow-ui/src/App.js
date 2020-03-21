import React, {useState} from 'react';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";
import NavBar from "./components/NavBar";
import Login from "./components/Login";
import Register from "./components/Register";
import Home from "./components/Home";
import './App.css';
import {AuthContext} from "./context/auth";
import PrivateRoute from "./PrivateRoute";
import Cookie from 'js-cookie';

function App(props) {
    const token = Cookie.get("token") ? Cookie.get("token") : null;
    const [authToken, setAuthToken] = useState(token);

    const setToken = (data) => {
        Cookie.set("token", data);
        setAuthToken(data);
    };

    return (
        <AuthContext.Provider value={{ authToken, setAuthToken: setToken }}>
        <Router>
            <div>
                <NavBar/>
                <Switch>
                    <Route path="/login" component={Login}/>
                    <Route path="/register" component={Register}/>
                    <PrivateRoute exact path="/" component={Home}/>
                </Switch>
            </div>
        </Router>
        </AuthContext.Provider>
    );
}

export default App;
