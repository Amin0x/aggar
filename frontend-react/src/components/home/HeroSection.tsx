import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Search, MapPin, Home, Building } from 'lucide-react';
import { Button } from '../ui/button';
import { cn } from '../../lib/utils';

const listingTypes = [
  { id: 'SALE', label: 'Buy', icon: Home },
  { id: 'RENT', label: 'Rent', icon: Building },
];

export function HeroSection() {
  const [selectedType, setSelectedType] = useState('SALE');
  const [location, setLocation] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = () => {
    const params = new URLSearchParams();
    params.append('listingType', selectedType);
    if (location) params.append('city', location);
    if (searchQuery) params.append('q', searchQuery);
    window.location.href = `/properties?${params.toString()}`;
  };

  return (
    <section className="relative bg-gradient-to-br from-primary-600 via-primary-700 to-primary-900 text-white py-20 md:py-28 overflow-hidden">
      {/* Background Pattern */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-20 left-10 w-72 h-72 bg-white rounded-full blur-3xl" />
        <div className="absolute bottom-10 right-10 w-96 h-96 bg-accent-500 rounded-full blur-3xl opacity-20" />
      </div>

      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-4 leading-tight">
          Find Your <span className="text-accent-400">Dream Property</span>
        </h1>
        <p className="text-xl md:text-2xl text-primary-100 mb-10 max-w-3xl mx-auto">
          Discover the perfect home from thousands of properties for sale and rent across the country.
        </p>

        {/* Search Card */}
        <div className="bg-white rounded-2xl shadow-2xl p-6 max-w-5xl mx-auto">
          {/* Listing Type Tabs */}
          <div className="flex gap-2 mb-4">
            {listingTypes.map((type) => (
              <button
                key={type.id}
                onClick={() => setSelectedType(type.id)}
                className={cn(
                  'flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-medium transition-all',
                  selectedType === type.id
                    ? 'bg-primary-600 text-white shadow-md'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                )}
              >
                <type.icon className="w-4 h-4" />
                {type.label}
              </button>
            ))}
          </div>

          {/* Search Inputs */}
          <div className="grid grid-cols-1 md:grid-cols-7 gap-3">
            <div className="md:col-span-3 relative">
              <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
              <input
                type="text"
                placeholder="Enter city, neighborhood..."
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                className="w-full pl-10 pr-4 py-3 border border-slate-300 rounded-xl text-slate-900 placeholder-slate-400 focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none"
              />
            </div>
            <div className="md:col-span-3 relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
              <input
                type="text"
                placeholder="Search by keyword..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-3 border border-slate-300 rounded-xl text-slate-900 placeholder-slate-400 focus:ring-2 focus:ring-primary-500 outline-none"
              />
            </div>
            <div className="md:col-span-1">
              <Button
                variant="primary"
                className="w-full py-3 text-base"
                onClick={handleSearch}
              >
                <Search className="w-5 h-5 mr-2" />
                Search
              </Button>
            </div>
          </div>

          {/* Quick Links */}
          <div className="flex flex-wrap gap-2 mt-4">
            <span className="text-xs text-slate-500">Popular:</span>
            {['Apartments in NY', 'Houses in LA', 'Condos in Miami'].map((term) => (
              <button
                key={term}
                onClick={() => { setSearchQuery(term); }}
                className="text-xs text-primary-600 hover:text-primary-700 bg-primary-50 px-3 py-1 rounded-full hover:bg-primary-100 transition"
              >
                {term}
              </button>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}