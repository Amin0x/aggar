import clsx, { type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatPrice(price: number, currency: string = 'USD'): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(price);
}

export function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }).format(date);
}

export function formatRelativeTime(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (diffInSeconds < 60) return 'just now';
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`;
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`;
  if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`;
  return formatDate(dateString);
}

export function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
}

export function getListingTypeLabel(type: string): string {
  const labels: Record<string, string> = {
    'SALE': 'For Sale',
    'RENT': 'For Rent',
  };
  return labels[type] || type;
}

export function getStatusLabel(status: string): string {
  const labels: Record<string, string> = {
    'available': 'Available',
    'pending': 'Pending',
    'sold': 'Sold',
    'rented': 'Rented',
    'off_market': 'Off Market',
  };
  return labels[status] || status;
}

export function getStatusColor(status: string): string {
  const colors: Record<string, string> = {
    'available': 'green',
    'pending': 'yellow',
    'sold': 'red',
    'rented': 'blue',
    'off_market': 'gray',
  };
  return colors[status] || 'gray';
}

export const PROPERTY_CATEGORIES = [
  { id: 'apartment', label: 'Apartments', icon: 'building' },
  { id: 'house', label: 'Houses', icon: 'home' },
  { id: 'condo', label: 'Condos', icon: 'city' },
  { id: 'townhouse', label: 'Townhouses', icon: 'house-user' },
  { id: 'villa', label: 'Villas', icon: 'crown' },
  { id: 'office', label: 'Office Spaces', icon: 'briefcase' },
  { id: 'retail', label: 'Retail Spaces', icon: 'store' },
  { id: 'land', label: 'Land & Plots', icon: 'mountain' },
];

export const AMENITIES_LIST = [
  'Air Conditioning',
  'Heating',
  'WiFi',
  'Parking',
  'Swimming Pool',
  'Gym',
  'Security',
  'Elevator',
  'Balcony',
  'Garden',
  'Pet Friendly',
  'Furnished',
  'Fireplace',
  'Basement',
  'Laundry Room',
  'Storage',
];
