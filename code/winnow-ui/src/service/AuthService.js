import axios from 'axios';

const USER_API_BASE_URL = 'http://localhost:8080/api/';

class AuthService {

    login(credentials) {
        //console.log("Login creds: "+JSON.stringify(credentials));
        return axios.post(USER_API_BASE_URL + "login", credentials);
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
        return JSON.parse(localStorage.getItem("userInfo"));
    }

    getAuthHeader() {
        return {headers: {Authorization: 'Bearer ' + this.getUserInfo().token}};
    }

    logOut() {
        localStorage.removeItem("userInfo");
        return axios.post(USER_API_BASE_URL + 'logout', {}, this.getAuthHeader());
    }
}

export default new AuthService();