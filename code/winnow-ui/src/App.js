import React, {useState} from 'react';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";
import * as C from "./constants";
import NavBar from "./components/common/NavBar";
import Login from "./routes/Login";
import Register from "./routes/Register";
import Dashboard from "./routes/Dashboard";
import {AuthContext} from "./context/auth";
import PrivateRoute from "./PrivateRoute";
import Cookies from 'js-cookie';
import Profile from "./routes/Profile";
import Support from "./routes/Support";
import Admin from "./routes/Admin";
import Error from "./routes/Error";
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
    const token = Cookies.get(C.WINNOW_TOKEN) ? Cookies.get(C.WINNOW_TOKEN) : null;
    const [authToken, setAuthToken] = useState(token);

    React.useEffect(() => {
        fetchApiStatus()
            .then(() => {
                setApiReady(true);
                setTimeOut(0)
            })
            .catch(() => {
                setApiReady(false);
                setTimeOut(setTimeout(fetchApiStatus, 15000))
            })
    }, [timeOut])
    /**
     * Updates the user's JWT upon login/logout.
     *
     * Sets cookie to Secure (requires HTTPS) in PROD environments.
     *
     * @param data - JWT
     */
    const setToken = (data) => {
        if (data === null) {
            Cookies.remove(C.WINNOW_TOKEN);
            sessionStorage.clear();
        } else {
            Cookies.set(
                C.WINNOW_TOKEN,
                data,
                {
                    secure: C.W_ENV === 'PROD',
                    sameSite: 'strict'
                }
            );
        }
        setAuthToken(data);
    };

    /* Displays application when API is available */
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
        return (
            <Maintenance/>
        )
    }
}

export default App;
