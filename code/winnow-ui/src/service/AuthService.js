import axios from 'axios';
import Cookies from 'js-cookie';

const USER_API_BASE_URL = 'http://localhost:8080/api/';

class AuthService {

    login(credentials) {
        return axios.post(USER_API_BASE_URL + "login", credentials);
    }

    register(credentials) {
        return axios.post(USER_API_BASE_URL + "registration", credentials);
    }

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

    getUserInfo() {
        let token = Cookies.get("token") ? Cookies.get("token") : null;
        if (token !== null) {
            console.info(`getUserInfo: ${this.parseToken(token).sub}`);
            return this.parseToken(token).sub;
        }
        return null;
    }

    logOut() {
        //localStorage.removeItem("userInfo");
        //return axios.post(USER_API_BASE_URL + 'logout', {}, this.authHeader());
    }
}

export default new AuthService();