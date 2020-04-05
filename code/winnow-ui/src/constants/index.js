let curHost = window.location.host;
console.info(`${curHost}`);
export const WINNOW_API_BASE_URL = (curHost === "localhost:3000")
    ? 'http://localhost:8080/api'
    : 'https://winnow-api.olympus3.com/api';
export const WINNOW_TOKEN = 'winnow-token';
export const PUBMED_BASE_URL = 'https://pubmed.ncbi.nlm.nih.gov';
