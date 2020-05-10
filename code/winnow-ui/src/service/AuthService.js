/**
 * The AuthService provides access to the Winnow API calls used for
 * functions related to authentication/authorization and user management.
 */
import axios from 'axios';
import Cookies from 'js-cookie';
import * as C from '../constants';
import * as Yup from 'yup';

/**
 * Retrieves User's profile data, scuh as name and email.
 *
 * @return {Promise<>}
 */
export const fetchProfileData = () => {
    return axios.get(
        `${C.WINNOW_API_BASE_URL}/profile`,
        {
            headers: getAuthHeader()
        }
    )
};

/**
 * Submits new User information to create an account in the application.
 *
 * @param credentials - JSON object containing user information and credentials.
 * @return {Promise<>}
 */
export const sendRegistration = (credentials) => {
    return axios.post(
        `${C.WINNOW_API_BASE_URL}/registration`,
        credentials,
    )
};

/**
 * Submits User credentials for logging into the application.
 *
 * @param credentials - JSON object containing user credentials.
 * @return {Promise<>}
 */
export const sendLoginCredentials = (credentials) => {
    return axios.post(
        `${C.WINNOW_API_BASE_URL}/login`,
        credentials,
    )
};

/**
 * Passes updated user profile information to API profile
 * endpoint.
 *
 * @param userInfo
 * @returns {Promise<>}
 */
export const sendProfileUpdate = (userInfo) => {
    return axios.put(
        `${C.WINNOW_API_BASE_URL}/profile`,
        userInfo,
        {
            headers: getAuthHeader(),
        }
    )
};

/**
 * Passes new user password to API profile endpoint.
 *
 * @param credentials
 * @return {Promise<>}
 */
export const sendChangePassword = (credentials) => {
    return axios.patch(
        `${C.WINNOW_API_BASE_URL}/profile`,
        credentials,
        {
            headers: getAuthHeader(),
        }
    )
};

/**
 * Sends the userEmail for sending a forgot password link.
 *
 * @param credentials
 * @return {Promise<>}
 */
export const forgotPassword = (credentials) => {
    return axios.post(
        `${C.WINNOW_API_BASE_URL}/forgot`,
        credentials
    )
};

/**
 * Passes new user password to API profile endpoint.
 *
 * @param credentials
 * @return {Promise<>}
 */
export const resetPassword = (credentials) => {
    return axios.post(
        `${C.WINNOW_API_BASE_URL}/reset`,
        credentials
    )
};

/**
 * Parses and decodes the payload portion of the JWT received from the
 * API login endpoint;
 *
 * @param token
 * @returns {null|any}
 */
export const parseToken = (token) => {
    if (token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (error) {
            return null
        }
    }
    return null;
}

/**
 * Verify that user token is still valid
 *
 * @returns boolean
 */
export const isTokenExpired = () => {
    let token = Cookies.get(C.WINNOW_TOKEN) ? Cookies.get(C.WINNOW_TOKEN) : null;
    if (token !== null) {
        let dateNow = new Date();
        let tokenExpiry = new Date(parseToken(token).exp * 1000);
        return dateNow > tokenExpiry;
    }
    return true;
}

/**
 * Creates the Authorization Header with the User's JWT for authenticated API calls.
 *
 * @return {{Authorization: string}}
 */
export const getAuthHeader = () => {
    let token = Cookies.get(C.WINNOW_TOKEN) ? Cookies.get(C.WINNOW_TOKEN) : null;
    return {'Authorization': `Bearer ${token}`};
}

/**
 * Login form validation schema
 */
export const loginSchema = Yup.object().shape({
    userEmail: Yup.string()
        .trim()
        .email(`Invalid email\n`)
        .required(`E-mail address required\n`),
    userPassword: Yup.string()
        .required(`Password required\n`),
})

/**
 * Forgot Password form validation schema
 */
export const forgotSchema = Yup.object().shape({
    userEmail: Yup.string()
        .trim()
        .email(`Invalid email\n`)
        .required(`E-mail address required\n`),
})

/**
 * Reset Password form validation schema
 */
export const resetSchema = Yup.object().shape({
    userPassword: Yup.string()
        .min(C.PASS_MIN_LEN, `Password must be at least ${C.PASS_MIN_LEN} characters\n`)
        .max(C.PASS_MAX_LEN, `Password cannot be more than ${C.PASS_MAX_LEN} characters\n`)
        .required(`Password required\n`),
    passwordConfirm: Yup.string()
        .required(`Password confirmation required\n`)
        .oneOf(
            [Yup.ref('userPassword'), null],
            `Passwords must match\n`,
        )
})

/**
 * Registration form validation schema
 */
export const registerSchema = Yup.object().shape({
    firstName: Yup.string()
        .trim()
        .min(C.USER_MIN_LEN, `Must be ${C.USER_MIN_LEN}-${C.USER_MAX_LEN} characters\n`)
        .max(C.USER_MAX_LEN, `Must be ${C.USER_MIN_LEN}-${C.USER_MAX_LEN} characters\n`)
        .required(`First name required\n`),
    lastName: Yup.string()
        .trim()
        .min(C.USER_MIN_LEN, `Must be ${C.USER_MIN_LEN}-${C.USER_MAX_LEN} characters\n`)
        .max(C.USER_MAX_LEN, `Must be ${C.USER_MIN_LEN}-${C.USER_MAX_LEN} characters\n`)
        .required(`Last name required\n`),
    userEmail: Yup.string()
        .trim()
        .email(`Invalid email\n`)
        .required(`E-mail address required\n`),
    userPassword: Yup.string()
        .min(C.PASS_MIN_LEN, `Password must be at least ${C.PASS_MIN_LEN} characters\n`)
        .max(C.PASS_MAX_LEN, `Password cannot be more than ${C.PASS_MAX_LEN} characters\n`)
        .required(`Password required\n`),
    passwordConfirm: Yup.string()
        .required(`Password confirmation required\n`)
        .oneOf(
            [Yup.ref('userPassword'), null],
            `Passwords must match\n`,
        ),
})

/**
 * Profile update form validation schema
 */
export const profileSchema = Yup.object().shape({
    firstName: Yup.string()
        .trim()
        .min(C.USER_MIN_LEN, `Must be ${C.USER_MIN_LEN}-${C.USER_MAX_LEN} characters\n`)
        .max(C.USER_MAX_LEN, `Must be ${C.USER_MIN_LEN}-${C.USER_MAX_LEN} characters\n`)
        .required(`First name required\n`),
    lastName: Yup.string()
        .trim()
        .min(C.USER_MIN_LEN, `Must be ${C.USER_MIN_LEN}-${C.USER_MAX_LEN} characters\n`)
        .max(C.USER_MAX_LEN, `Must be ${C.USER_MIN_LEN}-${C.USER_MAX_LEN} characters\n`)
        .required(`Last name required\n`),
    userEmail: Yup.string()
        .trim()
        .email(`Invalid email\n`)
        .required(`E-mail address required\n`),
})

/**
 * Password change form validation schema
 */
export const passwordSchema = Yup.object().shape({
    userPassword: Yup.string()
        .required(`Current password required\n`),
    userPasswordNew: Yup.string()
        .min(C.PASS_MIN_LEN, `Password must be at least ${C.PASS_MIN_LEN} characters\n`)
        .max(C.PASS_MAX_LEN, `Password cannot be more than ${C.PASS_MAX_LEN} characters\n`)
        .required(`New password required\n`),
    passwordConfirm: Yup.string()
        .required(`Password confirmation required\n`)
        .oneOf(
            [Yup.ref('userPasswordNew'), null],
            `Passwords must match\n`,
        ),
})
