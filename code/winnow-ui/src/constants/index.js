/**
 * Constants
 *
 */
let currentHost = window.location.host;
export const WINNOW_API_BASE_URL = (currentHost === "localhost:3000")
    ? 'http://localhost:8080/api'
    : 'https://winnow-api.olympus3.com/api';
export const W_ENV = (currentHost === "localhost:3000")
    ? 'DEV'
    : 'PROD'
export const WINNOW_API_TIMEOUT = 35000;
export const WINNOW_TOKEN = '_winnow_token';
export const USER_MIN_LEN = 1;
export const USER_MAX_LEN = 300;
export const PASS_MIN_LEN = 8;
export const PASS_MAX_LEN = 64;
export const SS_SH = 'searchHistory';
export const SS_SI = 'useBatch';
export const PUBMED_BASE_URL = 'https://pubmed.ncbi.nlm.nih.gov';
export const GENEDB_BASE_URL = 'https://www.ncbi.nlm.nih.gov/gene';
export const MESHDB_BASE_URL = 'https://www.ncbi.nlm.nih.gov/mesh?term=';
export const NCBI_API_BASE = 'https://eutils.ncbi.nlm.nih.gov/entrez/eutils';
export const NCBI_API_TOKEN = 'be6a6f419f4f31e609e53403ecb4c0c92b08';
export const QUERY_FORMATS = {
    GENE_ID: {label: 'Gene Id', value: 'geneId'},
    GENE_SYM: {label: 'Gene Symbol', value: 'symbol'},
    GENE_DESC: {label: 'Gene Description', value: 'description'},
    MESH_ID: {label: 'MeSH Id', value: 'meshId'},
    MESH_TREEID: {label: 'MeSH Tree Id', value: 'meshTreeId'},
    MESH_NAME: {label: 'MeSH Term', value: 'name'}
}
export const T2_POPTS = {
    paginationSize: 3,
    pageStartIndex: 1,
    hideSizePerPage: true, // Hide the sizePerPage dropdown always
    hidePageListOnlyOnePage: true, // Hide the pagination list when only one page
    firstPageText: 'First',
    prePageText: 'Back',
    nextPageText: 'Next',
    lastPageText: 'Last',
    nextPageTitle: 'First page',
    prePageTitle: 'Pre page',
    firstPageTitle: 'Next page',
    lastPageTitle: 'Last page',
    showTotal: true,
    disablePageTitle: true,
    sizePerPageList: [{
        text: '10', value: 10
    }]
}