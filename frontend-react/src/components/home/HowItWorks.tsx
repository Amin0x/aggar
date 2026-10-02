import { Search, Home, Key, CheckCircle } from 'lucide-react';
import { cn } from '../../lib/utils';

const steps = [
  {
    icon: Search,
    title: 'Search Properties',
    description: 'Browse thousands of listings with advanced filters to find exactly what you\'re looking for.',
    step: '01',
    color: 'blue',
  },
  {
    icon: Home,
    title: 'Visit & Compare',
    description: 'Schedule viewings and compare properties side by side to find your perfect match.',
    step: '02',
    color: 'amber',
  },
  {
    icon: Key,
    title: 'Make It Yours',
    description: 'Make an offer, negotiate terms, and close the deal with our expert guidance.',
    step: '03',
    color: 'green',
  },
];

const colorMap: Record<string, string> = {
  blue: 'bg-blue-50 text-blue-600 border-blue-200',
  amber: 'bg-amber-50 text-amber-600 border-amber-200',
  green: 'bg-green-50 text-green-600 border-green-200',
};

const iconColorMap: Record<string, string> = {
  blue: 'text-blue-600',
  amber: 'text-amber-600',
  green: 'text-green-600',
};

export function HowItWorks() {
  return (
    <section className="py-16 bg-slate-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-slate-900 mb-4">
            How It Works
          </h2>
          <p className="text-lg text-slate-500 max-w-2xl mx-auto">
            Finding your dream property has never been easier
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 md:gap-12">
          {steps.map((step, index) => (
            <div key={index} className="relative">
              {/* Connector Line */}
              {index < steps.length - 1 && (
                <div className="hidden md:block absolute top-16 left-1/2 w-full h-0.5 bg-slate-200 -z-10" />
              )}

              <div className="text-center">
                <div className={cn(
                  'inline-flex items-center justify-center w-16 h-16 rounded-2xl border-2 mb-6 relative',
                  colorMap[step.color]
                )}>
                  <step.icon className={cn('w-8 h-8', iconColorMap[step.color])} />
                  <span className="absolute -top-3 -right-3 w-7 h-7 bg-white border-2 rounded-full flex items-center justify-center text-xs font-bold text-slate-600">
                    {step.step}
                  </span>
                </div>
                <h3 className="text-xl font-semibold text-slate-900 mb-3">{step.title}</h3>
                <p className="text-slate-500 leading-relaxed">{step.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}