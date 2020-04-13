import axios from 'axios';
import Cookies from 'js-cookie';
import * as C from '../constants';


/**
 *
 * @return {Promise<T>}
 */
export const fetchProfileData = () => {
    console.info(`fetchProfileData: ${C.WINNOW_API_BASE_URL}/profile`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${C.WINNOW_API_BASE_URL}/profile`,
            {
                headers: new AuthService().getAuthHeader()
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

/**
 *
 * @param credentials
 * @return {Promise<T>}
 */
export const sendRegistration = (credentials) => {
    console.info(`sendRegistration: ${C.WINNOW_API_BASE_URL}/registration`);
    return new Promise((resolve, reject) => {
        axios.post(
            `${C.WINNOW_API_BASE_URL}/registration`,
            credentials,
        )
            .then(res => {
                resolve("Registration Successful")
            })
            .catch(err => {
                if (err.response.status === 409) {
                    reject(err.response.data.error)
                }
                reject(err)
            });
    });
};

/**
 *
 * @param credentials
 * @return {Promise<T>}
 */
export const sendLoginCredentials = (credentials) => {
    console.info(`sendLoginCredentials: ${C.WINNOW_API_BASE_URL}/login`);
    return new Promise((resolve, reject) => {
        axios.post(
            `${C.WINNOW_API_BASE_URL}/login`,
            credentials,
        )
            .then(res => {
                resolve(res.headers['authorization'].split(' ')[1]);
            })
            .catch(err => reject(err));
    });
};

/**
 * Passes updated user profile information to API profile
 * endpoint.
 *
 * @param userInfo
 * @returns {Promise<T>}
 */
export const sendProfileUpdate = (userInfo) => {
    return new Promise((resolve, reject) => {
        axios.patch(
            `${C.WINNOW_API_BASE_URL}/profile`,
            userInfo,
            {
                headers: this.getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data)
            })
            .catch(err => reject(err));
    });
};

/**
 * Passes new user password to API profile endpoint.
 *
 * @param credentials
 * @return {Promise<T>}
 */
export const sendChangePassword = (credentials) => {
    return new Promise((resolve, reject) => {
        axios.put(
            `${C.WINNOW_API_BASE_URL}/profile`,
            credentials,
            {
                headers: this.getAuthHeader(),
            }
        )
            .then(res => {
                resolve(res.data)
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
     * Passes user information and credentials to API registration
     * endpoint.
     *
     * @param credentials
     * @returns { await Promise<AxiosResponse<T>>}
     */
    register(credentials) {
        return axios.post(`${C.WINNOW_API_BASE_URL}/registration`,
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
            `${C.WINNOW_API_BASE_URL}/profile`,
            {
                headers: this.getAuthHeader(),
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
                return null
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
        let token = Cookies.get(C.WINNOW_TOKEN) ? Cookies.get(C.WINNOW_TOKEN) : null;
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
        let token = Cookies.get(C.WINNOW_TOKEN) ? Cookies.get(C.WINNOW_TOKEN) : null;
        if (token !== null) {
            let dateNow = new Date();
            let tokenExpiry = new Date(this.parseToken(token).exp * 1000);
            console.info(`isTokenExpired: Now ${dateNow}; Token ${tokenExpiry}`);
            return dateNow > tokenExpiry;
        }
        return true;
    }

    getAuthHeader() {
        let token = Cookies.get(C.WINNOW_TOKEN) ? Cookies.get(C.WINNOW_TOKEN) : null;
        return {'Authorization': `Bearer ${token}`};
    }

    /**
     * Removes current logged in user's session server side.
     */
    logOut() {
        //localStorage.removeItem("userInfo");
        //return axios.post(C.WINNOW_API_BASE_URL + 'logout', {}, C.authHeader());
    }
}

export default new AuthService();