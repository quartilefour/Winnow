import React from "react";
import {ProgressBar} from "react-bootstrap";

/**
 * Displays a loading animation while waiting for API calls to return.
 *
 * @return {*}
 * @constructor
 */
function PageLoader() {
    return (
        <ProgressBar animated now={100} variant={'info'} label={`Loading...`}/>
    )
}

export default PageLoader;