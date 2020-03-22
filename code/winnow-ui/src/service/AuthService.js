import axios from 'axios';
import Cookies from 'js-cookie';

const WINNOW_API_BASE_URL = 'http://localhost:8080/api/';

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