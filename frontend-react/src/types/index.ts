// Property related types
export type ListingType = 'SALE' | 'RENT';

export type PricePeriod = 'MONTHLY' | 'YEARLY' | 'WEEKLY' | 'DAILY';

export type PropertyStatus = 'available' | 'pending' | 'sold' | 'rented' | 'off_market';

export interface PropertyImage {
  id?: number;
  url: string;
  propertyId?: number;
}

export interface Amenity {
  id?: number;
  name: string;
}

export interface PriceHistory {
  id?: number;
  price: number;
  currency?: string;
  changedAt?: string;
  propertyId?: number;
}

export interface Property {
  id: number;
  title: string;
  slug: string;
  description?: string;
  price: number;
  currency: string;
  listingType: ListingType;
  pricePeriod?: PricePeriod;
  bedrooms?: number;
  bathrooms?: number;
  rooms?: number;
  floors?: number;
  area?: number;
  stateId?: number;
  stateName?: string;
  cityId?: number;
  cityName?: string;
  neighborhoodId?: number;
  neighborhoodName?: string;
  ownerId?: number;
  ownerName?: string;
  agentId?: number;
  agentName?: string;
  status: PropertyStatus;
  locationLat?: number;
  locationLng?: number;
  publishedAt?: string;
  createdAt?: string;
  updatedAt?: string;
  isDeleted?: boolean;
  images?: PropertyImage[];
  amenities?: Amenity[];
  priceHistory?: PriceHistory[];
}

export interface PropertyDto extends Omit<Property, 'stateName' | 'cityName' | 'neighborhoodName' | 'ownerName' | 'agentName'> {
  stateId?: number;
  cityId?: number;
  neighborhoodId?: number;
  ownerId?: number;
  agentId?: number;
}

// User related types
export type UserRole = 'USER' | 'AGENT' | 'ADMIN';

export interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  username: string;
  role: UserRole;
  createdAt?: string;
  ownedProperties?: Property[];
  agentProperties?: Property[];
}

// Location types
export interface State {
  id: number;
  name: string;
  code?: string;
  createdAt?: string;
  cities?: City[];
}

export interface City {
  id: number;
  stateId: number;
  stateName?: string;
  name: string;
  createdAt?: string;
  neighborhoods?: Neighborhood[];
}

export interface Neighborhood {
  id: number;
  cityId: number;
  cityName?: string;
  name: string;
  createdAt?: string;
}

// API response types
export interface PaginatedResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  empty: boolean;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

// Auth types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  phone?: string;
  username: string;
  password: string;
  confirmPassword: string;
  role: UserRole;
}

export interface AuthResponse {
  user: User;
  token: string;
}

// Filter types
export interface PropertyFilters {
  listingType?: string;
  state?: string;
  city?: string;
  category?: string;
  q?: string;
  minPrice?: number;
  maxPrice?: number;
  bedrooms?: number;
  bathrooms?: number;
  amenities?: string[];
  page?: number;
  size?: number;
  sort?: string;
  order?: string;
}

// Form types
export interface AddPropertyForm {
  listingType: ListingType;
  title: string;
  description?: string;
  price: number;
  currency: string;
  pricePeriod?: PricePeriod;
  bedrooms?: number;
  bathrooms?: number;
  rooms?: number;
  floors?: number;
  area?: number;
  stateId: number;
  cityId: number;
  neighborhoodId?: number;
  status: PropertyStatus;
  locationLat?: number;
  locationLng?: number;
  images?: File[];
  amenityIds?: number[];
}