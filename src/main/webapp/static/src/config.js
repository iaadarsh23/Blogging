// API configuration
const API_BASE_URL = '/api';  // Use relative path to avoid CORS issues

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: `${API_BASE_URL}/auth/login`,
    REGISTER: `${API_BASE_URL}/auth/register`,
    LOGOUT: `${API_BASE_URL}/auth/logout`,
    STATUS: `${API_BASE_URL}/auth/status`
  },
  BLOGS: {
    ALL: `${API_BASE_URL}/blogs`,
    USER: `${API_BASE_URL}/blogs/user`,
    SINGLE: (id) => `${API_BASE_URL}/blogs/${id}`
  }
};

export default API_ENDPOINTS;
