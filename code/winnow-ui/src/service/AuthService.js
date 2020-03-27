import axios from 'axios';
import Cookies from 'js-cookie';
import * as Constants from '../constants';


export const fetchProfileData = () => {
    console.info(`fetchProfileData: ${Constants.WINNOW_API_BASE_URL}/profile`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/profile`,
            {
                headers: Constants.authHeader,
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 * The AuthService calls handles all the authentication API calls
 * for the application.
 */
class AuthService {

    /**
     * Passes user credentials to API login endpoint.
     *
     * @param credentials
     * @returns { await Promise<AxiosResponse<T>>}
     */
    login(credentials) {
        return axios.post(`${Constants.WINNOW_API_BASE_URL}/login`,
            credentials);
    }

    /**
     * Passes user information and credentials to API registration
     * endpoint.
     *
     * @param credentials
     * @returns { await Promise<AxiosResponse<T>>}
     */
    register(credentials) {
        return axios.post(`${Constants.WINNOW_API_BASE_URL}/registration`,
            credentials);
    }

    /**
     * Retrieves user's profile information from API profile
     * endpoint.
     *
     * @returns {await Promise<AxiosResponse<T>>}
     */
     async getProfile() {
        return axios.get(
            `${Constants.WINNOW_API_BASE_URL}/profile`,
            {
                headers: Constants.authHeader,
            }
        ).then(res => {
            if (res.status === 200) {
                res.data['statusCode'] = res.status;
                console.log(`getProfile(): ${JSON.stringify(res.data)}`);
                return res.data;
            } else {
                console.log(`getProfile(): Non-200 status from API: ${JSON.stringify(res)}`);
                return {
                    "statusCode": res.status,
                    "statusText": res.statusText
                }
            }
        }).catch(error => {
            console.log(`getProfile(): error from API: ${JSON.stringify(error)}`);
            return {
                "statusCode": error.response.status,
                "statusText": error.toString()
            }
        });
    }

    /**
     * Passes updated user profile information to API profile
     * endpoint.
     *
     * @param userInfo
     * @returns {await Promise<AxiosResponse<T>>}
     */
    updateProfile(userInfo) {
        return axios.patch(
            `${Constants.WINNOW_API_BASE_URL}/profile`,
            userInfo,
            {
                headers: Constants.authHeader,
            }
        );
    }

    /**
     * Passes new user password to API profile endpoint.
     *
     * @param credentials
     * @returns {await Promise<AxiosResponse<T>>}
     */
    changePassword(credentials) {
        return axios.put(
            `${Constants.WINNOW_API_BASE_URL}/profile`,
            credentials,
            {
                headers: Constants.authHeader,
            }
        );
    }

    /**
     * Parses and decodes the payload portion of the JWT received from the
     * API login endpoint;
     *
     * @param token
     * @returns {null|any}
     */
    parseToken(token) {
        if (token) {
            try {
                return JSON.parse(atob(token.split('.')[1]));
            } catch (error) {
                // ignore
            }
        }
        return null;
    }

    /**
     * Returns the current logged in userEmail.
     *
     * @returns {null|String}
     */
    getUserInfo() {
        let token = Cookies.get("token") ? Cookies.get("token") : null;
        if (token !== null) {
            console.info(`getUserInfo: ${this.parseToken(token).sub}`);
            return this.parseToken(token).sub;
        }
        return null;
    }

    /**
     * Verify that user token is still valid
     *
     * @returns boolean
     */
    isTokenExpired() {
        let token = Cookies.get("token") ? Cookies.get("token") : null;
        if (token !== null) {
            let dateNow = new Date();
            console.info(`isTokenExpired: ${this.parseToken(token).exp*1000} : ${dateNow.getTime()}`);
            return this.parseToken(token).exp*1000 < dateNow.getTime();
        }
        return true;
    }

    /**
     * Removes current logged in user's session server side.
     */
    logOut() {
        //localStorage.removeItem("userInfo");
        //return axios.post(Constants.WINNOW_API_BASE_URL + 'logout', {}, Constants.authHeader());
    }
}

export default new AuthService();