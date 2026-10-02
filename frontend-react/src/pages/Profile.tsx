import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../hooks/useAuth';
import { userApi, propertyApi } from '../lib/api';
import { PropertyCard } from '../components/property/PropertyCard';
import { Button } from '../components/ui/button';
import { User, Mail, Phone, Calendar, Building2, Heart, Settings, Edit } from 'lucide-react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { cn, formatDate } from '../lib/utils';

export function Profile() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('properties');

  const { data: properties, isLoading } = useQuery({
    queryKey: ['my-properties'],
    queryFn: () => userApi.getMyProperties().then((res) => res.data),
    enabled: activeTab === 'properties',
  });

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Cover Photo */}
      <div className="h-48 md:h-64 bg-gradient-to-r from-primary-600 to-primary-800 relative">
        <div className="absolute inset-0 opacity-20">
          <div className="absolute top-10 right-20 w-96 h-96 bg-white rounded-full blur-3xl" />
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Profile Info Card */}
        <div className="relative -mt-20 mb-8">
          <div className="bg-white rounded-2xl shadow-lg p-6 md:p-8">
            <div className="flex flex-col md:flex-row items-start md:items-end gap-6">
              {/* Avatar */}
              <div className="w-24 h-24 md:w-32 md:h-32 bg-primary-100 rounded-2xl flex items-center justify-center border-4 border-white shadow-lg">
                <User className="w-12 h-12 text-primary-600" />
              </div>

              {/* Info */}
              <div className="flex-1">
                <h1 className="text-2xl md:text-3xl font-bold text-slate-900">{user?.name}</h1>
                <div className="flex flex-wrap gap-4 mt-2 text-sm text-slate-500">
                  <span className="flex items-center gap-1">
                    <Mail className="w-4 h-4" /> {user?.email}
                  </span>
                  {user?.phone && (
                    <span className="flex items-center gap-1">
                      <Phone className="w-4 h-4" /> {user.phone}
                    </span>
                  )}
                  <span className="flex items-center gap-1">
                    <Calendar className="w-4 h-4" /> Member since {user?.createdAt ? formatDate(user.createdAt) : '...'}
                  </span>
                </div>
              </div>

              {/* Actions */}
              <Link to="/profile/settings">
                <Button variant="outline" size="sm">
                  <Edit className="w-4 h-4 mr-2" /> Edit Profile
                </Button>
              </Link>
            </div>

            {/* Stats */}
            <div className="grid grid-cols-3 gap-4 mt-8 pt-6 border-t border-slate-100">
              <div className="text-center">
                <div className="text-2xl font-bold text-slate-900">{properties?.length || 0}</div>
                <div className="text-sm text-slate-500">Properties</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-slate-900">0</div>
                <div className="text-sm text-slate-500">Saved</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-slate-900">0</div>
                <div className="text-sm text-slate-500">Inquiries</div>
              </div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
          <div className="flex border-b border-slate-200">
            {[
              { id: 'properties', label: 'My Properties', icon: Building2 },
              { id: 'saved', label: 'Saved', icon: Heart },
              { id: 'settings', label: 'Settings', icon: Settings },
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={cn(
                  'flex items-center gap-2 px-6 py-4 text-sm font-medium transition-colors',
                  activeTab === tab.id
                    ? 'border-b-2 border-primary-600 text-primary-600'
                    : 'text-slate-500 hover:text-slate-700'
                )}
              >
                <tab.icon className="w-4 h-4" /> {tab.label}
              </button>
            ))}
          </div>

          <div className="p-6">
            {activeTab === 'properties' && (
              <>
                <div className="flex justify-between items-center mb-6">
                  <h2 className="text-xl font-semibold">My Properties</h2>
                  <Link to="/properties/add">
                    <Button variant="primary" size="sm">Add Property</Button>
                  </Link>
                </div>

                {isLoading ? (
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {[1, 2, 3].map((i) => (
                      <div key={i} className="animate-pulse">
                        <div className="bg-slate-200 h-48 rounded-xl mb-3" />
                      </div>
                    ))}
                  </div>
                ) : properties && properties.length > 0 ? (
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {properties.map((p) => (
                      <PropertyCard key={p.id} property={p} />
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-12">
                    <Building2 className="w-12 h-12 text-slate-300 mx-auto mb-3" />
                    <p className="text-slate-500 mb-4">No properties yet</p>
                    <Link to="/properties/add">
                      <Button variant="primary">Add Your First Property</Button>
                    </Link>
                  </div>
                )}
              </>
            )}

            {activeTab === 'saved' && (
              <div className="text-center py-12">
                <Heart className="w-12 h-12 text-slate-300 mx-auto mb-3" />
                <p className="text-slate-500 mb-4">No saved properties yet</p>
                <Link to="/properties">
                  <Button variant="primary">Browse Properties</Button>
                </Link>
              </div>
            )}

            {activeTab === 'settings' && (
              <div className="text-center py-12">
                <Settings className="w-12 h-12 text-slate-300 mx-auto mb-3" />
                <p className="text-slate-500 mb-4">Go to Settings to update your profile</p>
                <Link to="/profile/settings">
                  <Button variant="primary">Open Settings</Button>
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
