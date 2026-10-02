import { useQuery } from '@tanstack/react-query';
import { propertyApi } from '../lib/api';
import { PropertyCard } from '../components/property/PropertyCard';
import { Button } from '../components/ui/button';
import { Heart, Search } from 'lucide-react';
import { Link } from 'react-router-dom';

export function SavedProperties() {
  const { data: properties, isLoading } = useQuery({
    queryKey: ['saved-properties'],
    queryFn: () => propertyApi.getSavedProperties().then((res) => res.data),
  });

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-900 mb-2">Saved Properties</h1>
          <p className="text-slate-500">Properties you've bookmarked for later</p>
        </div>

        {isLoading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="animate-pulse">
                <div className="bg-slate-200 h-48 rounded-xl mb-3" />
                <div className="space-y-2">
                  <div className="h-4 bg-slate-200 rounded w-3/4" />
                  <div className="h-4 bg-slate-200 rounded w-1/2" />
                </div>
              </div>
            ))}
          </div>
        ) : properties && properties.length > 0 ? (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {properties.map((p) => (
                <PropertyCard key={p.id} property={p} saved />
              ))}
            </div>
          </>
        ) : (
          <div className="text-center py-16">
            <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <Heart className="w-8 h-8 text-slate-400" />
            </div>
            <h3 className="text-xl font-semibold text-slate-900 mb-2">No saved properties</h3>
            <p className="text-slate-500 mb-6">Start browsing and save properties you're interested in</p>
            <Link to="/properties">
              <Button variant="primary" size="lg">
                <Search className="w-5 h-5 mr-2" /> Browse Properties
              </Button>
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
