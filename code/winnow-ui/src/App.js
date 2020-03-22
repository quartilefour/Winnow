import React, {useState} from 'react';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";
import NavBar from "./components/NavBar";
import Login from "./components/Login";
import Register from "./components/Register";
import Dashboard from "./components/Dashboard";
import './App.css';
import {AuthContext} from "./context/auth";
import PrivateRoute from "./PrivateRoute";
import Cookies from 'js-cookie';
import Profile from "./components/Profile";

/**
 * Renders the User Interface to the Winnow application.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function App(props) {
    /* Authentication stateful objects */
    const token = Cookies.get("token") ? Cookies.get("token") : null;
    const [authToken, setAuthToken] = useState(token);

    /**
     * Updates the user's JWT upon login/logout.
     *
     * @param data - JWT
     */
    const setToken = (data) => {
        if (data === null) {
            Cookies.remove("token");
        } else {
            Cookies.set("token", data);
        }
        setAuthToken(data);
    };

    return (
        <AuthContext.Provider value={{authToken, setAuthToken: setToken}}>
            <Router>
                <div>
                    <NavBar/>
                    <Switch>
                        <Route path="/login" component={Login}/>
                        <Route path="/register" component={Register}/>
                        <PrivateRoute path="/profile" component={Profile}/>
                        <PrivateRoute exact path="/" component={Dashboard}/>
                    </Switch>
                </div>
            </Router>
        </AuthContext.Provider>
    );
}

export default App;
