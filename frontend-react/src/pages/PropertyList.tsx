import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { propertyApi } from '../lib/api';
import type { Property, PropertyFilters } from '../types';
import { PropertyCard } from '../components/property/PropertyCard';
import { FilterSidebar } from '../components/search/FilterSidebar';
import { Input } from '../components/ui/input';
import { Button } from '../components/ui/button';
import { SlidersHorizontal, Grid3X3, List, Search, ChevronLeft, ChevronRight, X } from 'lucide-react';
import { cn, formatPrice } from '../lib/utils';

export function PropertyList() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [mobileFiltersOpen, setMobileFiltersOpen] = useState(false);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');

  const [filters, setFilters] = useState<PropertyFilters>({
    listingType: searchParams.get('listingType') || undefined,
    state: searchParams.get('state') || undefined,
    city: searchParams.get('city') || undefined,
    category: searchParams.get('category') || undefined,
    q: searchParams.get('q') || undefined,
    minPrice: searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : undefined,
    maxPrice: searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : undefined,
    bedrooms: searchParams.get('bedrooms') ? Number(searchParams.get('bedrooms')) : undefined,
    page: searchParams.get('page') ? Number(searchParams.get('page')) : 0,
    size: 12,
    sort: searchParams.get('sort') || undefined,
    order: searchParams.get('order') || undefined,
  });

  const { data, isLoading, isFetching } = useQuery({
    queryKey: ['properties', filters],
    queryFn: () => propertyApi.list(filters),
  });

  const updateFilters = (newFilters: PropertyFilters) => {
    setFilters(newFilters);
    const params = new URLSearchParams();
    if (newFilters.listingType) params.set('listingType', newFilters.listingType);
    if (newFilters.state) params.set('state', newFilters.state);
    if (newFilters.city) params.set('city', newFilters.city);
    if (newFilters.category) params.set('category', newFilters.category);
    if (newFilters.q) params.set('q', newFilters.q);
    if (newFilters.minPrice) params.set('minPrice', newFilters.minPrice.toString());
    if (newFilters.maxPrice) params.set('maxPrice', newFilters.maxPrice.toString());
    if (newFilters.bedrooms) params.set('bedrooms', newFilters.bedrooms.toString());
    if (newFilters.sort) params.set('sort', newFilters.sort);
    if (newFilters.order) params.set('order', newFilters.order);
    setSearchParams(params);
  };

  const clearFilters = () => {
    setFilters({ size: 12, page: 0 });
    setSearchParams(new URLSearchParams());
  };

  const totalElements = data?.data.totalElements || 0;
  const totalPages = data?.data.totalPages || 0;
  const currentPage = filters.page || 0;

  return (
    <div className="min-h-screen bg-slate-50 py-6">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Page Header */}
        <div className="mb-6">
          <h1 className="text-2xl md:text-3xl font-bold text-slate-900 mb-2">
            {filters.listingType === 'SALE' ? 'Properties for Sale' :
              filters.listingType === 'RENT' ? 'Properties for Rent' : 'All Properties'}
          </h1>
          <p className="text-slate-500">
            {isLoading ? 'Loading...' : `${totalElements.toLocaleString()} properties found`}
          </p>
        </div>

        <div className="flex gap-6">
          {/* Desktop Filters Sidebar */}
          <div className="hidden lg:block w-72 flex-shrink-0">
            <FilterSidebar
              filters={filters}
              onFilterChange={updateFilters}
              onClear={clearFilters}
            />
          </div>

          {/* Main Content */}
          <div className="flex-1">
            {/* Toolbar */}
            <div className="flex items-center justify-between mb-4">
              <Button
                variant="outline"
                size="sm"
                className="lg:hidden"
                onClick={() => setMobileFiltersOpen(true)}
              >
                <SlidersHorizontal className="w-4 h-4 mr-2" />
                Filters
              </Button>

              <div className="flex items-center gap-3 ml-auto">
                {/* Search */}
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                  <input
                    type="text"
                    placeholder="Search in results..."
                    defaultValue={filters.q}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        updateFilters({ ...filters, q: (e.target as HTMLInputElement).value || undefined });
                      }
                    }}
                    className="pl-9 pr-4 py-2 border border-slate-300 rounded-lg text-sm w-48 focus:ring-2 focus:ring-primary-500 outline-none"
                  />
                </div>

                {/* View Toggle */}
                <div className="flex border border-slate-300 rounded-lg overflow-hidden">
                  <button
                    onClick={() => setViewMode('grid')}
                    className={cn(
                      'p-2 transition',
                      viewMode === 'grid' ? 'bg-primary-600 text-white' : 'bg-white text-slate-600 hover:bg-slate-50'
                    )}
                  >
                    <Grid3X3 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => setViewMode('list')}
                    className={cn(
                      'p-2 transition',
                      viewMode === 'list' ? 'bg-primary-600 text-white' : 'bg-white text-slate-600 hover:bg-slate-50'
                    )}
                  >
                    <List className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>

            {/* Loading State */}
            {(isLoading || isFetching) && (
              <div className={cn('grid gap-6', viewMode === 'grid' ? 'grid-cols-1 md:grid-cols-2' : 'grid-cols-1')}>
                {[1, 2, 3, 4, 5, 6].map((i) => (
                  <div key={i} className="animate-pulse">
                    <div className="bg-slate-200 rounded-xl h-48 mb-3" />
                    <div className="space-y-2">
                      <div className="h-4 bg-slate-200 rounded w-3/4" />
                      <div className="h-4 bg-slate-200 rounded w-1/2" />
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Results */}
            {!isLoading && data?.data.content && data.data.content.length > 0 && (
              <>
                <div className={cn('gap-6', viewMode === 'grid' ? 'grid grid-cols-1 md:grid-cols-2' : 'flex flex-col gap-4')}>
                  {data.data.content.map((property: Property) => (
                    <PropertyCard key={property.id} property={property} />
                  ))}
                </div>

                {/* Pagination */}
                {totalPages > 1 && (
                  <div className="flex items-center justify-center gap-2 mt-8">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => updateFilters({ ...filters, page: currentPage - 1 })}
                      disabled={currentPage === 0}
                    >
                      <ChevronLeft className="w-4 h-4" />
                    </Button>

                    {[...Array(Math.min(5, totalPages))].map((_, i) => {
                      let pageNum;
                      if (totalPages <= 5) {
                        pageNum = i;
                      } else if (currentPage < 3) {
                        pageNum = i;
                      } else if (currentPage > totalPages - 4) {
                        pageNum = totalPages - 5 + i;
                      } else {
                        pageNum = currentPage - 2 + i;
                      }

                      return (
                        <button
                          key={pageNum}
                          onClick={() => updateFilters({ ...filters, page: pageNum })}
                          className={cn(
                            'w-10 h-10 rounded-lg text-sm font-medium transition',
                            currentPage === pageNum
                              ? 'bg-primary-600 text-white'
                              : 'bg-white border border-slate-300 text-slate-600 hover:bg-slate-50'
                          )}
                        >
                          {pageNum + 1}
                        </button>
                      );
                    })}

                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => updateFilters({ ...filters, page: currentPage + 1 })}
                      disabled={currentPage >= totalPages - 1}
                    >
                      <ChevronRight className="w-4 h-4" />
                    </Button>
                  </div>
                )}
              </>
            )}

            {/* Empty State */}
            {!isLoading && (!data?.data.content || data.data.content.length === 0) && (
              <div className="text-center py-16">
                <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Search className="w-8 h-8 text-slate-400" />
                </div>
                <h3 className="text-xl font-semibold text-slate-900 mb-2">No properties found</h3>
                <p className="text-slate-500 mb-6">Try adjusting your filters or search terms</p>
                <Button variant="outline" onClick={clearFilters}>Clear All Filters</Button>
              </div>
            )}
          </div>
        </div>

        {/* Mobile Filters Overlay */}
        {mobileFiltersOpen && (
          <div className="fixed inset-0 z-50 lg:hidden">
            <div className="absolute inset-0 bg-black/50" onClick={() => setMobileFiltersOpen(false)} />
            <div className="absolute right-0 top-0 h-full w-80 bg-white shadow-xl overflow-y-auto p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="font-semibold text-lg">Filters</h3>
                <button onClick={() => setMobileFiltersOpen(false)}>
                  <X className="w-5 h-5" />
                </button>
              </div>
              <FilterSidebar
                filters={filters}
                onFilterChange={(f) => { updateFilters(f); setMobileFiltersOpen(false); }}
                onClear={() => { clearFilters(); setMobileFiltersOpen(false); }}
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}