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
}

export default new ApiService();
