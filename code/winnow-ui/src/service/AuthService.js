import axios from 'axios';
import Cookies from 'js-cookie';

const WINNOW_API_BASE_URL = 'http://localhost:8080/api/';
const authHeader = {'Authorization': `Bearer ${Cookies.get("token")}`};

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
        return axios.post(WINNOW_API_BASE_URL + "login", credentials);
    }

    /**
     * Passes user information and credentials to API registration
     * endpoint.
     *
     * @param credentials
     * @returns { await Promise<AxiosResponse<T>>}
     */
    register(credentials) {
        return axios.post(WINNOW_API_BASE_URL + "registration", credentials);
    }

    /**
     * Retrieves user's profile information from API profile
     * endpoint.
     *
     * @returns {await Promise<AxiosResponse<T>>}
     */
    getProfile() {
        return axios.get(
            `${WINNOW_API_BASE_URL}/profile`,
            {
                headers: authHeader,
            }
        );
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
            `${WINNOW_API_BASE_URL}/profile`,
            userInfo,
            {
                headers: authHeader,
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
            `${WINNOW_API_BASE_URL}/profile`,
            credentials,
            {
                headers: authHeader,
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
     * Removes current logged in user's session server side.
     */
    logOut() {
        //localStorage.removeItem("userInfo");
        //return axios.post(WINNOW_API_BASE_URL + 'logout', {}, this.authHeader());
    }
}

export default new AuthService();