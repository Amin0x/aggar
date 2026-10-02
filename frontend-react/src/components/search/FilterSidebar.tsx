import { useState, useEffect } from 'react';
import type { PropertyFilters } from '../../types';
import { locationApi } from '../../lib/api';
import type { State, City } from '../../types';
import { Button } from '../ui/button';
import { SlidersHorizontal, ChevronDown, ChevronUp } from 'lucide-react';
import { cn } from '../../lib/utils';

interface FilterSidebarProps {
  filters: PropertyFilters;
  onFilterChange: (filters: PropertyFilters) => void;
  onClear: () => void;
  className?: string;
}

export function FilterSidebar({ filters, onFilterChange, onClear, className }: FilterSidebarProps) {
  const [expanded, setExpanded] = useState({
    location: true,
    price: true,
    details: true,
    category: true,
    sort: true,
  });

  const [states, setStates] = useState<State[]>([]);
  const [cities, setCities] = useState<City[]>([]);
  const [loadingStates, setLoadingStates] = useState(false);
  const [loadingCities, setLoadingCities] = useState(false);

  const toggle = (section: keyof typeof expanded) => {
    setExpanded((prev) => ({ ...prev, [section]: !prev[section] }));
  };

  const updateFilter = (key: keyof PropertyFilters, value: unknown) => {
    onFilterChange({ ...filters, [key]: value, page: 0 });
  };

  // Fetch states on mount
  useEffect(() => {
    setLoadingStates(true);
    locationApi.getStates()
      .then((res) => setStates(res.data))
      .finally(() => setLoadingStates(false));
  }, []);

  // Fetch cities when state changes
  useEffect(() => {
    if (filters.state) {
      setLoadingCities(true);
      locationApi.getCities(Number(filters.state))
        .then((res) => {
          setCities(res.data);
          // Clear city filter if the selected city doesn't belong to the new state
          if (filters.city) {
            const cityExists = res.data.some((c) => c.id.toString() === filters.city);
            if (!cityExists) {
              updateFilter('city', undefined);
            }
          }
        })
        .finally(() => setLoadingCities(false));
    } else {
      setCities([]);
    }
  }, [filters.state]);

  const listingTypes = [
    { value: 'SALE', label: 'For Sale' },
    { value: 'RENT', label: 'For Rent' },
  ];

  const categories = [
    { value: 'apartment', label: 'Apartment' },
    { value: 'house', label: 'House' },
    { value: 'villa', label: 'Villa' },
    { value: 'land', label: 'Land' },
    { value: 'office', label: 'Office' },
  ];

  const sortOptions = [
    { value: 'price,asc', label: 'Price: Low to High' },
    { value: 'price,desc', label: 'Price: High to Low' },
    { value: 'createdAt,desc', label: 'Newest First' },
    { value: 'createdAt,asc', label: 'Oldest First' },
    { value: 'area,desc', label: 'Largest First' },
    { value: 'area,asc', label: 'Smallest First' },
  ];

  const bedroomOptions = [1, 2, 3, 4, 5];
  const bathroomOptions = [1, 2, 3, 4];

  return (
    <div className={cn('bg-white rounded-xl border border-slate-200 p-5', className)}>
      <div className="flex items-center justify-between mb-5">
        <h3 className="font-semibold text-slate-900 flex items-center gap-2">
          <SlidersHorizontal className="w-4 h-4" /> Filters
        </h3>
        <button onClick={onClear} className="text-xs text-primary-600 hover:text-primary-700">
          Clear All
        </button>
      </div>

      {/* Location Filters */}
      <div className="mb-5">
        <div className="flex items-center justify-between cursor-pointer mb-2" onClick={() => toggle('location')}>
          <label className="text-sm font-medium text-slate-700">Location</label>
          {expanded.location ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
        </div>
        {expanded.location && (
          <div className="space-y-3">
            {/* State */}
            <div>
              <label className="block text-xs text-slate-500 mb-1">State</label>
              <select
                value={filters.state || ''}
                onChange={(e) => updateFilter('state', e.target.value || undefined)}
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 outline-none bg-white"
                disabled={loadingStates}
              >
                <option value="">All States</option>
                {states.map((state) => (
                  <option key={state.id} value={state.id.toString()}>{state.name}</option>
                ))}
              </select>
            </div>

            {/* City */}
            <div>
              <label className="block text-xs text-slate-500 mb-1">City</label>
              <select
                value={filters.city || ''}
                onChange={(e) => updateFilter('city', e.target.value || undefined)}
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 outline-none bg-white"
                disabled={loadingCities || !filters.state}
              >
                <option value="">All Cities</option>
                {cities.map((city) => (
                  <option key={city.id} value={city.id.toString()}>{city.name}</option>
                ))}
              </select>
            </div>
          </div>
        )}
      </div>

      {/* Listing Type */}
      <div className="mb-5">
        <label className="block text-sm font-medium text-slate-700 mb-2">Listing Type</label>
        <div className="flex gap-2">
          {listingTypes.map((type) => (
            <button
              key={type.value}
              onClick={() => updateFilter('listingType', filters.listingType === type.value ? undefined : type.value)}
              className={cn(
                'flex-1 py-2 px-3 rounded-lg text-sm font-medium transition',
                filters.listingType === type.value
                  ? 'bg-primary-600 text-white'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              )}
            >
              {type.label}
            </button>
          ))}
        </div>
      </div>

      {/* Category */}
      <div className="mb-5">
        <div className="flex items-center justify-between cursor-pointer mb-2" onClick={() => toggle('category')}>
          <label className="text-sm font-medium text-slate-700">Category</label>
          {expanded.category ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
        </div>
        {expanded.category && (
          <div className="grid grid-cols-2 gap-2">
            {categories.map((cat) => (
              <button
                key={cat.value}
                onClick={() => updateFilter('category', filters.category === cat.value ? undefined : cat.value)}
                className={cn(
                  'py-2 px-3 rounded-lg text-sm font-medium transition',
                  filters.category === cat.value
                    ? 'bg-primary-600 text-white'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                )}
              >
                {cat.label}
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Price Range */}
      <div className="mb-5">
        <div className="flex items-center justify-between cursor-pointer mb-2" onClick={() => toggle('price')}>
          <label className="text-sm font-medium text-slate-700">Price Range</label>
          {expanded.price ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
        </div>
        {expanded.price && (
          <div className="space-y-3">
            <div className="grid grid-cols-2 gap-2">
              <input
                type="number"
                placeholder="Min Price"
                value={filters.minPrice || ''}
                onChange={(e) => updateFilter('minPrice', e.target.value ? Number(e.target.value) : undefined)}
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 outline-none"
              />
              <input
                type="number"
                placeholder="Max Price"
                value={filters.maxPrice || ''}
                onChange={(e) => updateFilter('maxPrice', e.target.value ? Number(e.target.value) : undefined)}
                className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 outline-none"
              />
            </div>
          </div>
        )}
      </div>

      {/* Property Details */}
      <div className="mb-5">
        <div className="flex items-center justify-between cursor-pointer mb-2" onClick={() => toggle('details')}>
          <label className="text-sm font-medium text-slate-700">Property Details</label>
          {expanded.details ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
        </div>
        {expanded.details && (
          <div className="space-y-3">
            <div>
              <label className="block text-xs text-slate-500 mb-1">Bedrooms</label>
              <div className="flex gap-1.5">
                {bedroomOptions.map((num) => (
                  <button
                    key={num}
                    onClick={() => updateFilter('bedrooms', filters.bedrooms === num ? undefined : num)}
                    className={cn(
                      'w-9 h-9 rounded-lg text-sm font-medium transition',
                      filters.bedrooms === num
                        ? 'bg-primary-600 text-white'
                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    )}
                  >
                    {num}+
                  </button>
                ))}
              </div>
            </div>
            <div>
              <label className="block text-xs text-slate-500 mb-1">Bathrooms</label>
              <div className="flex gap-1.5">
                {bathroomOptions.map((num) => (
                  <button
                    key={num}
                    onClick={() => updateFilter('bathrooms', filters.bathrooms === num ? undefined : num)}
                    className={cn(
                      'w-9 h-9 rounded-lg text-sm font-medium transition',
                      filters.bathrooms === num
                        ? 'bg-primary-600 text-white'
                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    )}
                  >
                    {num}+
                  </button>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Sort / Order */}
      <div className="mb-5">
        <div className="flex items-center justify-between cursor-pointer mb-2" onClick={() => toggle('sort')}>
          <label className="text-sm font-medium text-slate-700">Sort By</label>
          {expanded.sort ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
        </div>
        {expanded.sort && (
          <div className="space-y-2">
            {sortOptions.map((option) => (
              <button
                key={option.value}
                onClick={() => {
                  const [sort, order] = option.value.split(',');
                  updateFilter('sort', sort);
                  updateFilter('order', order);
                }}
                className={cn(
                  'w-full text-left px-3 py-2 rounded-lg text-sm transition',
                  filters.sort === option.value.split(',')[0] && filters.order === option.value.split(',')[1]
                    ? 'bg-primary-600 text-white'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                )}
              >
                {option.label}
              </button>
            ))}
          </div>
        )}
      </div>

      <Button onClick={() => onFilterChange(filters)} variant="primary" className="w-full">
        Apply Filters
      </Button>
    </div>
  );
}