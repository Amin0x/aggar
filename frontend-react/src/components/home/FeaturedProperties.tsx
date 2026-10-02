import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { propertyApi } from '../../lib/api';
import { PropertyCard } from '../property/PropertyCard';
import { Button } from '../ui/button';
import { ArrowRight } from 'lucide-react';

export function FeaturedProperties() {
  const { data, isLoading } = useQuery({
    queryKey: ['properties', 'featured'],
    queryFn: () => propertyApi.list({ size: 6 }),
  });

  return (
    <section className="py-16 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-end mb-10">
          <div>
            <h2 className="text-3xl md:text-4xl font-bold text-slate-900 mb-4">
              Featured Properties
            </h2>
            <p className="text-lg text-slate-500 max-w-2xl">
              Handpicked properties that match your preferences and budget
            </p>
          </div>
          <Link to="/properties" className="mt-4 md:mt-0">
            <Button variant="ghost" className="group">
              View All
              <ArrowRight className="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" />
            </Button>
          </Link>
        </div>

        {isLoading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="animate-pulse">
                <div className="bg-slate-200 rounded-xl h-48 mb-4" />
                <div className="space-y-2">
                  <div className="h-4 bg-slate-200 rounded w-3/4" />
                  <div className="h-4 bg-slate-200 rounded w-1/2" />
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {data?.data.content.map((property) => (
              <PropertyCard key={property.id} property={property} />
            ))}
          </div>
        )}
      </div>
    </section>
  );
}