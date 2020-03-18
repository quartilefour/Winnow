import axios from 'axios';

const USER_API_BASE_URL = 'http://localhost:8080/token/';

class AuthService {

    login(credentials) {
        return '{ "res": [ "data": [ "status": 200, "result": ["username": "esantora"] ]] }';
        // return axios.post(USER_API_BASE_URL + "generate-token", credentials);
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