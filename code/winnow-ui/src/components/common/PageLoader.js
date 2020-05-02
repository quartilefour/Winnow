import React from "react";
import PropTypes from "prop-types"
import {ProgressBar} from "react-bootstrap";

/**
 * Displays a loading animation while waiting for API calls to return.
 *
 * @param props - Message to show in loading bar
 * @return {*}
 * @constructor
 */
function PageLoader(props) {

    PageLoader.propTypes = {
        message: PropTypes.string,
        now: PropTypes.number,
        variant: PropTypes.string,
    }

    PageLoader.defaultProps = {
        message: `Loading...`,
        now: 100,
        variant: 'info',
    }

    const {message, now, variant} = props

    return (
        <ProgressBar animated now={now} variant={variant} label={message}/>
    )
}

export default PageLoader;