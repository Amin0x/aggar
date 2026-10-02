import { ChevronLeft, ChevronRight, Star } from 'lucide-react';
import { useState } from 'react';
import { cn } from '../../lib/utils';

const testimonials = [
  {
    name: 'Sarah Johnson',
    role: 'Homebuyer',
    avatar: 'https://i.pravatar.cc/100?img=1',
    content: 'Aggar made finding our dream home incredibly easy. The filters helped us narrow down exactly what we wanted, and the agent we worked with was fantastic.',
    rating: 5,
  },
  {
    name: 'Michael Chen',
    role: 'Property Investor',
    avatar: 'https://i.pravatar.cc/100?img=2',
    content: 'As a real estate investor, I need reliable data and quick access to new listings. Aggar delivers on both. Highly recommended!',
    rating: 5,
  },
  {
    name: 'Emily Rodriguez',
    role: 'First-time Buyer',
    avatar: 'https://i.pravatar.cc/100?img=3',
    content: 'The step-by-step guidance and mortgage calculator were lifesavers for me as a first-time buyer. Thank you, Aggar!',
    rating: 5,
  },
  {
    name: 'David Thompson',
    role: 'Renter',
    avatar: 'https://i.pravatar.cc/100?img=4',
    content: 'Found a great rental property within my budget in just 3 days. The search filters work perfectly for renters too.',
    rating: 4,
  },
];

export function Testimonials() {
  const [currentIndex, setCurrentIndex] = useState(0);

  const next = () => {
    setCurrentIndex((prev) => (prev + 1) % testimonials.length);
  };

  const prev = () => {
    setCurrentIndex((prev) => (prev - 1 + testimonials.length) % testimonials.length);
  };

  return (
    <section className="py-16 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-slate-900 mb-4">
            What Our Clients Say
          </h2>
          <p className="text-lg text-slate-500">
            Real stories from happy homeowners and renters
          </p>
        </div>

        <div className="max-w-4xl mx-auto">
          <div className="relative bg-slate-50 rounded-3xl p-8 md:p-12">
            {/* Stars */}
            <div className="flex justify-center mb-6">
              {[...Array(testimonials[currentIndex].rating)].map((_, i) => (
                <Star key={i} className="w-5 h-5 text-amber-400 fill-amber-400" />
              ))}
            </div>

            {/* Content */}
            <blockquote className="text-lg md:text-xl text-slate-700 text-center mb-8 leading-relaxed">
              "{testimonials[currentIndex].content}"
            </blockquote>

            {/* Author */}
            <div className="flex items-center justify-center gap-4">
              <img
                src={testimonials[currentIndex].avatar}
                alt={testimonials[currentIndex].name}
                className="w-12 h-12 rounded-full object-cover"
              />
              <div>
                <div className="font-semibold text-slate-900">{testimonials[currentIndex].name}</div>
                <div className="text-sm text-slate-500">{testimonials[currentIndex].role}</div>
              </div>
            </div>

            {/* Navigation */}
            <div className="flex justify-center gap-3 mt-8">
              <button
                onClick={prev}
                className="p-2 rounded-full bg-white shadow-sm border border-slate-200 hover:bg-slate-100 transition"
              >
                <ChevronLeft className="w-5 h-5" />
              </button>
              {testimonials.map((_, i) => (
                <button
                  key={i}
                  onClick={() => setCurrentIndex(i)}
                  className={cn(
                    'w-2.5 h-2.5 rounded-full transition-all',
                    i === currentIndex ? 'bg-primary-600 w-8' : 'bg-slate-300'
                  )}
                />
              ))}
              <button
                onClick={next}
                className="p-2 rounded-full bg-white shadow-sm border border-slate-200 hover:bg-slate-100 transition"
              >
                <ChevronRight className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}