import axios from 'axios';
import type { Property, PropertyFilters, PaginatedResponse, User, State, City, Neighborhood, LoginRequest, RegisterRequest, AuthResponse } from '../types';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use((config) => {
  const language = window.location.pathname.split('/')[1];
  config.headers['Accept-Language'] = language === 'en' ? 'en' : 'ar';
  const token = localStorage.getItem('auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token');
      localStorage.removeItem('user');
      const language = window.location.pathname.split('/')[1] === 'en' ? 'en' : 'ar';
      window.location.href = `/${language}/login`;
    }
    return Promise.reject(error);
  }
);

// Property API
export const propertyApi = {
  list: (filters?: PropertyFilters) => {
    const params = new URLSearchParams();
    if (filters?.listingType) params.append('listingType', filters.listingType);
    if (filters?.state) params.append('state', filters.state);
    if (filters?.city) params.append('city', filters.city);
    if (filters?.category) params.append('category', filters.category);
    if (filters?.q) params.append('q', filters.q);
    if (filters?.minPrice) params.append('minPrice', filters.minPrice.toString());
    if (filters?.maxPrice) params.append('maxPrice', filters.maxPrice.toString());
    if (filters?.bedrooms) params.append('bedrooms', filters.bedrooms.toString());
    if (filters?.bathrooms) params.append('bathrooms', filters.bathrooms.toString());
    if (filters?.page !== undefined) params.append('page', filters.page.toString());
    if (filters?.size) params.append('size', filters.size.toString());
    if (filters?.sort) params.append('sort', filters.sort);
    if (filters?.order) params.append('order', filters.order);

    return api.get<PaginatedResponse<Property>>(`/properties?${params.toString()}`);
  },

  getById: (id: number) => api.get<Property>(`/properties/${id}`),

  create: (data: FormData | Record<string, unknown>) => api.post<Property>('/properties', data),

  update: (id: number, data: FormData | Record<string, unknown>) => api.put<Property>(`/properties/${id}`, data),

  delete: (id: number) => api.delete(`/properties/${id}`),

  saveProperty: (id: number) => api.post(`/properties/${id}/save`),

  unsaveProperty: (id: number) => api.delete(`/properties/${id}/save`),

  getSavedProperties: () => api.get<Property[]>('/properties/saved'),
};

// Auth API
export const authApi = {
  login: (data: LoginRequest) => api.post<AuthResponse>('/auth/login', data),

  register: (data: RegisterRequest) => api.post<AuthResponse>('/auth/register', data),

  logout: () => api.post('/auth/logout'),

  getProfile: () => api.get<User>('/users/me'),

  updateProfile: (data: Partial<User>) => api.put<User>('/users/me', data),
};

// Location API
export const locationApi = {
  getStates: () => api.get<State[]>('/states'),

  getCities: (stateId?: number) => {
    const params = stateId ? `?stateId=${stateId}` : '';
    return api.get<City[]>(`/cities${params}`);
  },

  getNeighborhoods: (cityId: number) => api.get<Neighborhood[]>(`/neighborhoods?cityId=${cityId}`),
};

// User API
export const userApi = {
  getProfile: () => api.get<User>('/users/me'),

  updateProfile: (data: Partial<User>) => api.put<User>('/users/me', data),

  getMyProperties: () => api.get<Property[]>('/users/me/properties'),

  getSavedProperties: () => api.get<Property[]>('/users/me/saved'),
};

export default api;