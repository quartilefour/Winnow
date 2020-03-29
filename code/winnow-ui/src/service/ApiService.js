import axios from 'axios';
import * as Constants from '../constants';

export const fetchMeshtermCat = () => {
    console.debug(`fetchMeshtermCat: ${Constants.WINNOW_API_BASE_URL}/meshterms/category`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/meshterms/category`,
            {
                headers: Constants.authHeader,
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

export const fetchMeshtermTree = (node) => {
    console.debug(`fetchMeshtermTree: ${Constants.WINNOW_API_BASE_URL}/meshterms/tree/parentid/${node}`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/meshterms/tree/parentid/${node}`,
            {
                headers: Constants.authHeader,
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};

export const fetchMeshtermNode = (node) => {
    console.debug(`fetchMeshtermNode: ${Constants.WINNOW_API_BASE_URL}/meshterms/tree/nodeid/${node}`);
    return new Promise((resolve, reject) => {
        axios.get(
            `${Constants.WINNOW_API_BASE_URL}/meshterms/tree/nodeid/${node}`,
            {
                headers: Constants.authHeader,
            }
        )
            .then(res => {
                resolve(res.data);
            })
            .catch(err => reject(err));
    });
};
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
