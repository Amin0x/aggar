import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { propertyApi } from '../lib/api';
import type { Property } from '../types';
import { ImageGallery } from '../components/property/ImageGallery';
import { ContactForm } from '../components/property/ContactForm';
import { User, BadgeCheck, Bed, Bath, Square, MapPin, Home, Building2, Calendar, ArrowLeft, Share2, Heart } from 'lucide-react';
import { cn, formatPrice, getListingTypeLabel, getStatusColor, getStatusLabel } from '../lib/utils';
import { useState } from 'react';

export function PropertyDetail() {
  const { id } = useParams<{ id: string }>();
  const [isSaved, setIsSaved] = useState(false);

  const { data: property, isLoading, error } = useQuery({
    queryKey: ['property', id],
    queryFn: () => propertyApi.getById(Number(id)).then((res) => res.data),
    enabled: !!id,
  });

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="animate-pulse space-y-6">
          <div className="h-96 bg-slate-200 rounded-2xl" />
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-4">
              <div className="h-8 bg-slate-200 rounded w-3/4" />
              <div className="h-6 bg-slate-200 rounded w-1/2" />
              <div className="h-32 bg-slate-200 rounded" />
            </div>
            <div className="h-64 bg-slate-200 rounded-xl" />
          </div>
        </div>
      </div>
    );
  }

  if (error || !property) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 text-center">
        <h2 className="text-2xl font-bold text-slate-900 mb-4">Property Not Found</h2>
        <p className="text-slate-500 mb-6">The property you're looking for doesn't exist or has been removed.</p>
        <Link to="/properties" className="text-primary-600 hover:underline">
          &larr; Back to Properties
        </Link>
      </div>
    );
  }

  return (
    <div className="bg-white min-h-screen">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {/* Breadcrumb */}
        <div className="flex items-center gap-2 text-sm text-slate-500 mb-4">
          <Link to="/" className="hover:text-slate-700">Home</Link>
          <span>/</span>
          <Link to="/properties" className="hover:text-slate-700">Properties</Link>
          <span>/</span>
          <span className="text-slate-900 truncate max-w-xs">{property.title}</span>
        </div>

        {/* Back Button */}
        <Link to="/properties" className="inline-flex items-center gap-1 text-sm text-slate-500 hover:text-slate-700 mb-4">
          <ArrowLeft className="w-4 h-4" /> Back to listings
        </Link>

        {/* Image Gallery */}
        <ImageGallery
          images={property.images?.map((img) => ({ url: img.url })) || []}
          title={property.title}
        />

        {/* Content Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mt-6">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Header */}
            <div className="mb-6">
              <div className="flex items-start justify-between mb-3">
                <div>
                  <h1 className="text-2xl md:text-3xl font-bold text-slate-900 mb-2">
                    {property.title}
                  </h1>
                  <div className="flex items-center gap-2 text-slate-500">
                    <MapPin className="w-4 h-4" />
                    <span>
                      {property.cityName}
                      {property.neighborhoodName && `, ${property.neighborhoodName}`}
                    </span>
                  </div>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => setIsSaved(!isSaved)}
                    className="w-10 h-10 rounded-xl border border-slate-200 flex items-center justify-center hover:bg-slate-50 transition"
                  >
                    <Heart className={cn('w-5 h-5', isSaved ? 'fill-red-500 text-red-500' : 'text-slate-600')} />
                  </button>
                  <button className="w-10 h-10 rounded-xl border border-slate-200 flex items-center justify-center hover:bg-slate-50 transition">
                    <Share2 className="w-5 h-5 text-slate-600" />
                  </button>
                </div>
              </div>

              {/* Price & Badges */}
              <div className="flex items-center gap-3 flex-wrap">
                <span className="text-3xl font-bold text-slate-900">
                  {formatPrice(property.price, property.currency)}
                  {property.listingType === 'RENT' && property.pricePeriod && (
                    <span className="text-base font-normal text-slate-500">/{property.pricePeriod.toLowerCase()}</span>
                  )}
                </span>
                <span className={cn(
                  'px-3 py-1 rounded-full text-xs font-semibold text-white',
                  property.listingType === 'SALE' ? 'bg-green-500' : 'bg-blue-500'
                )}>
                  {getListingTypeLabel(property.listingType)}
                </span>
                {property.status !== 'available' && (
                  <span className={cn('px-3 py-1 rounded-full text-xs font-semibold text-white', `bg-${getStatusColor(property.status)}-500`)}>
                    {getStatusLabel(property.status)}
                  </span>
                )}
              </div>
            </div>

            {/* Property Details */}
            <div className="bg-slate-50 rounded-2xl p-6 mb-6">
              <h2 className="text-lg font-semibold text-slate-900 mb-4">Property Details</h2>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {property.bedrooms !== undefined && (
                  <div className="text-center p-3 bg-white rounded-xl">
                    <Bed className="w-5 h-5 text-primary-600 mx-auto mb-1" />
                    <div className="font-bold text-slate-900">{property.bedrooms}</div>
                    <div className="text-xs text-slate-500">Bedrooms</div>
                  </div>
                )}
                {property.bathrooms !== undefined && (
                  <div className="text-center p-3 bg-white rounded-xl">
                    <Bath className="w-5 h-5 text-primary-600 mx-auto mb-1" />
                    <div className="font-bold text-slate-900">{property.bathrooms}</div>
                    <div className="text-xs text-slate-500">Bathrooms</div>
                  </div>
                )}
                {property.rooms !== undefined && (
                  <div className="text-center p-3 bg-white rounded-xl">
                    <Home className="w-5 h-5 text-primary-600 mx-auto mb-1" />
                    <div className="font-bold text-slate-900">{property.rooms}</div>
                    <div className="text-xs text-slate-500">Rooms</div>
                  </div>
                )}
                {property.area !== undefined && property.area > 0 && (
                  <div className="text-center p-3 bg-white rounded-xl">
                    <Square className="w-5 h-5 text-primary-600 mx-auto mb-1" />
                    <div className="font-bold text-slate-900">{property.area}</div>
                    <div className="text-xs text-slate-500">sq ft</div>
                  </div>
                )}
                {property.floors !== undefined && (
                  <div className="text-center p-3 bg-white rounded-xl">
                    <Building2 className="w-5 h-5 text-primary-600 mx-auto mb-1" />
                    <div className="font-bold text-slate-900">{property.floors}</div>
                    <div className="text-xs text-slate-500">Floors</div>
                  </div>
                )}
                <div className="text-center p-3 bg-white rounded-xl">
                  <Calendar className="w-5 h-5 text-primary-600 mx-auto mb-1" />
                  <div className="font-bold text-slate-900 capitalize">{property.listingType.toLowerCase()}</div>
                  <div className="text-xs text-slate-500">Type</div>
                </div>
              </div>
            </div>

            {/* Description */}
            {property.description && (
              <div className="mb-6">
                <h2 className="text-lg font-semibold text-slate-900 mb-3">Description</h2>
                <div className="text-slate-600 leading-relaxed whitespace-pre-line">
                  {property.description}
                </div>
              </div>
            )}

            {/* Amenities */}
            {property.amenities && property.amenities.length > 0 && (
              <div className="mb-6">
                <h2 className="text-lg font-semibold text-slate-900 mb-3">Amenities</h2>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                  {Array.from(property.amenities).map((amenity) => (
                    <div key={amenity.id} className="flex items-center gap-2 p-3 bg-slate-50 rounded-lg">
                      <BadgeCheck className="w-4 h-4 text-primary-600" />
                      <span className="text-sm text-slate-700">{amenity.name}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Map Placeholder */}
            <div className="mb-6">
              <h2 className="text-lg font-semibold text-slate-900 mb-3">Location</h2>
              {property.locationLat && property.locationLng ? (
                <div className="h-64 bg-slate-100 rounded-xl flex items-center justify-center">
                  <p className="text-slate-500">
                    Map: {property.locationLat}, {property.locationLng}
                  </p>
                </div>
              ) : (
                <div className="h-64 bg-slate-100 rounded-xl flex items-center justify-center">
                  <div className="text-center">
                    <MapPin className="w-8 h-8 text-slate-300 mx-auto mb-2" />
                    <p className="text-slate-500">{property.cityName}, {property.neighborhoodName || 'Unknown'}</p>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Sidebar */}
          <div className="lg:col-span-1">
            <div className="sticky top-20">
              {/* Agent Card / Contact */}
              <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm">
                <h3 className="font-semibold text-slate-900 mb-1">Interested in this property?</h3>
                <p className="text-sm text-slate-500 mb-4">Get in touch with the agent</p>

                {/* Agent Info */}
                <div className="flex items-center gap-3 mb-4 pb-4 border-b border-slate-100">
                  <div className="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center">
                    <User className="w-6 h-6 text-primary-600" />
                  </div>
                  <div>
                    <div className="font-medium text-slate-900">{property.agentName || 'Agent'}</div>
                    <div className="text-xs text-slate-500">Property Agent</div>
                  </div>
                </div>

                <ContactForm propertyTitle={property.title} agentName={property.agentName} />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
