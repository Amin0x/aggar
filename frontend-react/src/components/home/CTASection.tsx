import { Link } from 'react-router-dom';
import { Button } from '../ui/button';
import { Home, ArrowRight } from 'lucide-react';

export function CTASection() {
  return (
    <section className="py-20 bg-gradient-to-r from-primary-600 to-primary-800 relative overflow-hidden">
      {/* Background Pattern */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute -top-20 -right-20 w-96 h-96 bg-white rounded-full blur-3xl" />
        <div className="absolute -bottom-20 -left-20 w-96 h-96 bg-accent-500 rounded-full blur-3xl" />
      </div>

      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 bg-white/10 rounded-2xl mb-6">
          <Home className="w-8 h-8 text-white" />
        </div>
        <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold text-white mb-6">
          Ready to Find Your Dream Property?
        </h2>
        <p className="text-xl text-primary-100 mb-10 max-w-3xl mx-auto">
          Join thousands of satisfied clients who found their perfect property through Aggar.
          Start your search today!
        </p>
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link to="/properties">
            <Button variant="secondary" size="lg" className="bg-white text-primary-700 hover:bg-primary-50">
              Browse Properties
              <ArrowRight className="w-5 h-5 ml-2" />
            </Button>
          </Link>
          <Link to="/register">
            <Button
              variant="outline"
              size="lg"
              className="border-2 border-white/30 text-white hover:bg-white/10"
            >
              Create Account
            </Button>
          </Link>
        </div>
      </div>
    </section>
  );
}