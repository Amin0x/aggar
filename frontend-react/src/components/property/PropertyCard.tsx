import { Link } from 'react-router-dom';
import type { Property } from '../../types';
import { Heart, Bed, Bath, Square, MapPin } from 'lucide-react';
import { cn, formatPrice, getListingTypeLabel, getStatusColor, getStatusLabel } from '../../lib/utils';

interface PropertyCardProps {
  property: Property;
  onSave?: (id: number) => void;
  saved?: boolean;
}

export function PropertyCard({ property, onSave, saved = false }: PropertyCardProps) {
  const mainImage = property.images && property.images.length > 0
    ? property.images[0].url
    : 'https://via.placeholder.com/400x250?text=No+Image';

  return (
    <div className="group bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden hover:shadow-lg hover:-translate-y-1 transition-all duration-300">
      {/* Image */}
      <div className="relative img-zoom">
        <Link to={`/properties/${property.id}`}>
          <img
            src={mainImage}
            alt={property.title}
            className="w-full h-48 object-cover"
          />
        </Link>

        {/* Badges */}
        <div className="absolute top-3 left-3 flex gap-2">
          <span className={cn(
            'px-2.5 py-1 rounded-full text-xs font-semibold',
            property.listingType === 'SALE'
              ? 'bg-green-500 text-white'
              : 'bg-blue-500 text-white'
          )}>
            {getListingTypeLabel(property.listingType)}
          </span>
          {property.status && property.status !== 'available' && (
            <span className={cn(
              'px-2.5 py-1 rounded-full text-xs font-semibold text-white',
              `bg-${getStatusColor(property.status)}-500`
            )}>
              {getStatusLabel(property.status)}
            </span>
          )}
        </div>

        {/* Save Button */}
        {onSave && (
          <button
            onClick={(e) => { e.preventDefault(); onSave(property.id); }}
            className="absolute top-3 right-3 w-8 h-8 bg-white/80 backdrop-blur-sm rounded-full flex items-center justify-center hover:bg-white transition"
          >
            <Heart className={cn('w-4 h-4', saved ? 'fill-red-500 text-red-500' : 'text-slate-600')} />
          </button>
        )}
      </div>

      {/* Content */}
      <div className="p-4">
        {/* Price */}
        <div className="mb-2">
          <span className="text-xl font-bold text-slate-900">
            {formatPrice(property.price, property.currency)}
          </span>
          {property.listingType === 'RENT' && property.pricePeriod && (
            <span className="text-sm text-slate-500">/{property.pricePeriod.toLowerCase()}</span>
          )}
        </div>

        {/* Title */}
        <Link to={`/properties/${property.id}`}>
          <h3 className="font-semibold text-slate-900 mb-2 line-clamp-2 hover:text-primary-600 transition">
            {property.title}
          </h3>
        </Link>

        {/* Location */}
        <div className="flex items-center gap-1 text-sm text-slate-500 mb-3">
          <MapPin className="w-3.5 h-3.5" />
          <span>
            {property.cityName || 'City'}
            {property.neighborhoodName && `, ${property.neighborhoodName}`}
          </span>
        </div>

        {/* Details */}
        <div className="flex items-center gap-4 pt-3 border-t border-slate-100 text-sm text-slate-600">
          {property.bedrooms !== undefined && (
            <div className="flex items-center gap-1">
              <Bed className="w-4 h-4 text-slate-400" />
              <span>{property.bedrooms} beds</span>
            </div>
          )}
          {property.bathrooms !== undefined && (
            <div className="flex items-center gap-1">
              <Bath className="w-4 h-4 text-slate-400" />
              <span>{property.bathrooms} baths</span>
            </div>
          )}
          {property.area !== undefined && property.area > 0 && (
            <div className="flex items-center gap-1">
              <Square className="w-4 h-4 text-slate-400" />
              <span>{property.area.toLocaleString()} sqft</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}