import axios from 'axios';
import Cookie from 'js-cookie';
import { useAuth} from "../context/auth";

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
        let token =  Cookie.get("token") ? Cookie.get("token") : null;
        if (token !== null) {
            console.log(this.parseToken(token));
            return this.parseToken(token);
        }
        return null;
    }

    authHeader() {
        // return authorization header with jwt token
        const { authToken } = useAuth();
        if (authToken) {
            return { Authorization: `Bearer ${authToken}` };
        } else {
            return {};
        }
    }

    logOut() {
        localStorage.removeItem("userInfo");
        return axios.post(USER_API_BASE_URL + 'logout', {}, this.getAuthHeader());
    }
}

export default new AuthService();