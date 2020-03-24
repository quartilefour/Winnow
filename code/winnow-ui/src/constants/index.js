import Cookies from "js-cookie";

export const WINNOW_API_BASE_URL = 'http://localhost:8080/api';
export const authHeader = {'Authorization': `Bearer ${Cookies.get("token")}`};