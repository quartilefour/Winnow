import axios from 'axios';
import * as Constants from '../constants';

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
            `${Constants.WINNOW_API_BASE_URL}/genes`, {
                headers: Constants.authHeader,
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
            `${Constants.WINNOW_API_BASE_URL}/genes/${geneId}`, {
                headers: Constants.authHeader,
            });
    }
}

export default new ApiService();
