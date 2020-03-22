import axios from 'axios';
import Cookies from 'js-cookie';

const USER_API_BASE_URL = 'http://localhost:8080/api';
const authHeader = {'Authorization': `Bearer ${Cookies.get("token")}`};

class ApiService {

    getAllGenes() {
        return axios.get(
            `${USER_API_BASE_URL}/genes`, {
                headers: authHeader,
            });
    }

    fetchUserById(userId) {
        return axios.get(USER_API_BASE_URL + '/' + userId);
    }

    deleteUser(userId) {
        return axios.delete(USER_API_BASE_URL + '/' + userId);
    }

    addUser(user) {
        return axios.post(""+USER_API_BASE_URL, user);
    }

    editUser(user) {
        return axios.put(USER_API_BASE_URL + '/' + user.id, user);
    }

}

export default new ApiService();
