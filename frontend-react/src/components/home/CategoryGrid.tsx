import { Link } from 'react-router-dom';
import {
  Building2,
  Home,
  Crown,
  Building,
  Hotel,
  Store,
  Map,
  LandPlot,
} from 'lucide-react';
import { cn } from '../../lib/utils';

const categories = [
  { id: 'apartment', label: 'Apartments', icon: Building2, count: 450, color: 'blue' },
  { id: 'house', label: 'Houses', icon: Home, count: 320, color: 'green' },
  { id: 'villa', label: 'Villas', icon: Crown, count: 180, color: 'amber' },
  { id: 'condo', label: 'Condos', icon: Building, count: 280, color: 'sky' },
  { id: 'townhouse', label: 'Townhouses', icon: Hotel, count: 150, color: 'purple' },
  { id: 'office', label: 'Office Spaces', icon: Building2, count: 200, color: 'slate' },
  { id: 'retail', label: 'Retail Spaces', icon: Store, count: 120, color: 'orange' },
  { id: 'land', label: 'Land & Plots', icon: LandPlot, count: 90, color: 'emerald' },
];

const colorMap: Record<string, string> = {
  blue: 'bg-blue-50 text-blue-600 border-blue-200 hover:bg-blue-100',
  green: 'bg-green-50 text-green-600 border-green-200 hover:bg-green-100',
  amber: 'bg-amber-50 text-amber-600 border-amber-200 hover:bg-amber-100',
  sky: 'bg-sky-50 text-sky-600 border-sky-200 hover:bg-sky-100',
  purple: 'bg-purple-50 text-purple-600 border-purple-200 hover:bg-purple-100',
  slate: 'bg-slate-50 text-slate-600 border-slate-200 hover:bg-slate-100',
  orange: 'bg-orange-50 text-orange-600 border-orange-200 hover:bg-orange-100',
  emerald: 'bg-emerald-50 text-emerald-600 border-emerald-200 hover:bg-emerald-100',
};

export function CategoryGrid() {
  return (
    <section className="py-16 bg-slate-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-slate-900 mb-4">
            Browse by Property Type
          </h2>
          <p className="text-lg text-slate-500 max-w-2xl mx-auto">
            Explore our wide range of properties across different categories
          </p>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 md:gap-6">
          {categories.map((cat, index) => (
            <Link
              key={cat.id}
              to={`/properties?category=${cat.id}`}
              className={cn(
                'group relative p-6 bg-white rounded-2xl border-2 transition-all duration-300',
                'hover:shadow-lg hover:-translate-y-1',
                colorMap[cat.color] || colorMap.blue
              )}
              style={{ animationDelay: `${index * 50}ms` }}
            >
              <div className="flex flex-col items-center text-center">
                <div className={cn(
                  'w-14 h-14 rounded-2xl flex items-center justify-center mb-4 transition-transform group-hover:scale-110',
                  `bg-${cat.color}-50`
                )}>
                  <cat.icon className={cn('w-7 h-7', `text-${cat.color}-600`)} />
                </div>
                <h3 className="font-semibold text-slate-900 mb-1">{cat.label}</h3>
                <p className="text-sm text-slate-500">{cat.count}+ Properties</p>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
}