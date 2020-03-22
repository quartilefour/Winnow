import axios from 'axios';
import Cookies from 'js-cookie';

const WINNOW_API_BASE_URL = 'http://localhost:8080/api';
const authHeader = {'Authorization': `Bearer ${Cookies.get("token")}`};

/**
 * The ApiService class provides access to all the Winnow API calls except
 * those related to authentication/authorization.
 */
class ApiService {

    /**
     * Returns a list of all genes.
     *
     * @returns {await Promise<AxiosResponse<T>>}
     */
    getAllGenes() {
        return axios.get(
            `${WINNOW_API_BASE_URL}/genes`, {
                headers: authHeader,
            });
    }

    /**
     * Returns information about a gene identified by geneId.
     *
     * @param geneId
     * @returns {await Promise<AxiosResponse<T>>}
     */
    getGene(geneId) {
        return axios.get(
            `${WINNOW_API_BASE_URL}/genes/${geneId}`, {
                headers: authHeader,
            });
    }

    fetchUserById(userId) {
        return axios.get(WINNOW_API_BASE_URL + '/' + userId);
    }

    deleteUser(userId) {
        return axios.delete(WINNOW_API_BASE_URL + '/' + userId);
    }

    addUser(user) {
        return axios.post(""+WINNOW_API_BASE_URL, user);
    }

    editUser(user) {
        return axios.put(WINNOW_API_BASE_URL + '/' + user.id, user);
    }

}

export default new ApiService();
