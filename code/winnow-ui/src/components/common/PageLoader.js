import React from "react";
import {ProgressBar} from "react-bootstrap";

function PageLoader(props) {
    return (
        <ProgressBar animated now={100} variant={'info'} label={`Loading...`}/>
    )
}

export default PageLoader;