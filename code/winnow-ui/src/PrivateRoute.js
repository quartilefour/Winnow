import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import {useAuth} from "./context/auth";


/**
 * The PrivateRoute function protects declared routes by redirecting
 * unauthenticated users to the Login page.
 *
 * @param Component
 * @param rest
 * @returns {*}
 * @constructor
 */
function PrivateRoute({ component: Component, ...rest }) {
    const { authToken } = useAuth();

    return(
        <Route {...rest} render={(props) => (
            authToken ? (
            <Component {...props} />
            ) : (
                <Redirect to={{ pathname: "/login", state: { referer: props.location } }} />
            )
        )}
        />
    );
}

export default PrivateRoute;