import React, {useState} from 'react';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";
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
import {callAPI} from "./service/ApiService";
import Maintenance from "./components/error/Maintenance";
import {API_RESOURCES, W_ENV, WINNOW_API_TIMEOUT, WINNOW_TOKEN} from "./constants";

/**
 * Renders the User Interface to the Winnow application.
 *
 * @returns {*}
 * @constructor
 */
function App() {

    const {GET_API_STATUS} = API_RESOURCES;

    const [apiReady, setApiReady] = useState(false);
    const [timeOut, setTimeOut] = useState(0)
    /* Authentication stateful objects */
    const token = Cookies.get(WINNOW_TOKEN) ? Cookies.get(WINNOW_TOKEN) : null;
    const [authToken, setAuthToken] = useState(token);

    React.useEffect(() => {
        /* Check to see if API is available. */
        callAPI(GET_API_STATUS)
            .then(() => {
                setApiReady(true);
                setTimeOut(0)
            })
            .catch(() => {
                setApiReady(false);
                setTimeOut(setTimeout(callAPI, WINNOW_API_TIMEOUT / 2))
            })
    }, [GET_API_STATUS, timeOut])

    /**
     * Updates the user's JWT upon login/logout.
     *
     * Sets cookie to Secure (requires HTTPS) in PROD environments.
     *
     * @param data - JWT
     */
    const setToken = (data) => {
        if (data === null) {
            Cookies.remove(WINNOW_TOKEN);
            sessionStorage.clear();
        } else {
            Cookies.set(
                WINNOW_TOKEN,
                data,
                {
                    secure: W_ENV === 'PROD',
                    sameSite: 'strict'
                }
            );
        }
        setAuthToken(data);
    };

    /* Displays application when API is available */
    if (apiReady) return (
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

    return (<Maintenance/>)
}

export default App;
