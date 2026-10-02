import { useQuery } from '@tanstack/react-query';
import { propertyApi } from '../../lib/api';
import { Building2, Users, MapPin, TrendingUp } from 'lucide-react';
import { cn } from '../../lib/utils';

interface StatItemProps {
  icon: React.ElementType;
  value: string;
  label: string;
  delay?: number;
}

function StatItem({ icon: Icon, value, label, delay = 0 }: StatItemProps) {
  return (
    <div className={cn("text-center", "animate-fade-in")} style={{ animationDelay: `${delay}ms` }}>
      <div className="inline-flex items-center justify-center w-12 h-12 bg-primary-50 text-primary-600 rounded-xl mb-3">
        <Icon className="w-6 h-6" />
      </div>
      <div className="text-3xl md:text-4xl font-bold text-slate-900 mb-1">{value}</div>
      <div className="text-sm text-slate-500">{label}</div>
    </div>
  );
}

export function StatsSection() {
  const { data, isLoading } = useQuery({
    queryKey: ['stats'],
    queryFn: async () => {
      const res = await propertyApi.list({ size: 1 });
      return {
        totalProperties: res.data.totalElements || 0,
        cities: 15,
        happyClients: 2000,
        dealsClosed: 1500,
      };
    },
  });

  return (
    <section className="py-16 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
          <StatItem
            icon={Building2}
            value={isLoading ? '...' : `${data?.totalProperties?.toLocaleString() || 0}+`}
            label="Properties Listed"
            delay={0}
          />
          <StatItem
            icon={MapPin}
            value={isLoading ? '...' : `${data?.cities || 15}+`}
            label="Cities Covered"
            delay={100}
          />
          <StatItem
            icon={Users}
            value={isLoading ? '...' : `${data?.happyClients?.toLocaleString() || 0}+`}
            label="Happy Clients"
            delay={200}
          />
          <StatItem
            icon={TrendingUp}
            value={isLoading ? '...' : `${data?.dealsClosed?.toLocaleString() || 0}+`}
            label="Deals Closed"
            delay={300}
          />
        </div>
      </div>
    </section>
  );
}