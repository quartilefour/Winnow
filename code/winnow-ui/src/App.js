import React, {useEffect, useState} from 'react';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";
import NavBar from "./components/common/NavBar";
import Login from "./components/user/Login";
import Register from "./components/user/Register";
import Dashboard from "./components/user/Dashboard";
import './App.css';
import {AuthContext} from "./context/auth";
import PrivateRoute from "./PrivateRoute";
import Cookies from 'js-cookie';
import Profile from "./components/user/Profile";
import Support from "./components/user/Support";
import Admin from "./components/admin/Admin";
import Error from "./components/error/Error";
import {fetchApiStatus} from "./service/ApiService";
import Maintenance from "./components/error/Maintenance";

/**
 * Renders the User Interface to the Winnow application.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function App(props) {
    const [apiReady, setApiReady] = useState(false);
    const [timeOut, setTimeOut] = useState(0)
    /* Authentication stateful objects */
    const token = Cookies.get("token") ? Cookies.get("token") : null;
    const [authToken, setAuthToken] = useState(token);

    useEffect(() => {
        fetchApiStatus()
            .then (res => {
                setApiReady(true);
                setTimeOut(0)
            })
            .catch(err => {
                setApiReady(false);
                setTimeOut(setTimeout(fetchApiStatus, 15000))
                console.info(`apistatus: ${timeOut}`);
            })
    })
    /**
     * Updates the user's JWT upon login/logout.
     *
     * @param data - JWT
     */
    const setToken = (data) => {
        if (data === null) {
            Cookies.remove("token");
            sessionStorage.clear();
        } else {
            Cookies.set("token", data);
        }
        setAuthToken(data);
    };

    if (apiReady) {
        return (
            <AuthContext.Provider value={{authToken, setAuthToken: setToken}}>
                <Router>
                    <div>
                        <NavBar/>
                        <Switch>
                            <Route path="/login" component={Login}/>
                            <Route path="/register" component={Register}/>
                            <PrivateRoute path="/profile" component={Profile}/>
                            <PrivateRoute path="/support" component={Support}/>
                            <PrivateRoute exact path="/" component={Dashboard}/>
                            <PrivateRoute path="/admin" component={Admin}/>
                            <PrivateRoute component={Error}/>
                        </Switch>
                    </div>
                </Router>
            </AuthContext.Provider>
        );
    } else {
        return  (
            <Maintenance/>
        )
    }
}

export default App;
