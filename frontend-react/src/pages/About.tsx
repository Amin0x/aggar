import { Building2, Users, Award, Shield, MapPin, Phone, Mail } from 'lucide-react';

export function About() {
  return (
    <div className="min-h-screen bg-white">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-primary-600 to-primary-800 text-white py-20 md:py-28">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 className="text-4xl md:text-5xl font-bold mb-4">About Aggar</h1>
          <p className="text-xl text-primary-100 max-w-3xl mx-auto">
            Your trusted partner in finding the perfect property since 2015
          </p>
        </div>
      </section>

      {/* Mission & Vision */}
      <section className="py-16 md:py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-3xl font-bold text-slate-900 mb-4">Our Mission</h2>
              <p className="text-lg text-slate-600 leading-relaxed mb-6">
                To revolutionize the real estate experience by connecting buyers, sellers, and renters
                with their ideal properties through innovative technology and personalized service.
              </p>
              <h2 className="text-3xl font-bold text-slate-900 mb-4">Our Vision</h2>
              <p className="text-lg text-slate-600 leading-relaxed">
                To become the world's most trusted real estate platform, making property transactions
                seamless, transparent, and accessible to everyone.
              </p>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-primary-50 rounded-2xl p-6 text-center">
                <Shield className="w-8 h-8 text-primary-600 mx-auto mb-3" />
                <h3 className="font-semibold text-slate-900 mb-1">Trust</h3>
                <p className="text-sm text-slate-600">Verified listings & agents</p>
              </div>
              <div className="bg-amber-50 rounded-2xl p-6 text-center mt-8">
                <Award className="w-8 h-8 text-amber-600 mx-auto mb-3" />
                <h3 className="font-semibold text-slate-900 mb-1">Excellence</h3>
                <p className="text-sm text-slate-600">Award-winning service</p>
              </div>
              <div className="bg-green-50 rounded-2xl p-6 text-center">
                <Users className="w-8 h-8 text-green-600 mx-auto mb-3" />
                <h3 className="font-semibold text-slate-900 mb-1">Community</h3>
                <p className="text-sm text-slate-600">2000+ happy clients</p>
              </div>
              <div className="bg-sky-50 rounded-2xl p-6 text-center mt-8">
                <Building2 className="w-8 h-8 text-sky-600 mx-auto mb-3" />
                <h3 className="font-semibold text-slate-900 mb-1">Innovation</h3>
                <p className="text-sm text-slate-600">Cutting-edge technology</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Stats */}
      <section className="bg-slate-50 py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {[
              { number: '10+', label: 'Years Experience' },
              { number: '5000+', label: 'Properties Sold' },
              { number: '15', label: 'Cities Covered' },
              { number: '2000+', label: 'Happy Clients' },
            ].map((stat) => (
              <div key={stat.label} className="text-center">
                <div className="text-4xl md:text-5xl font-bold text-primary-600 mb-2">{stat.number}</div>
                <div className="text-slate-500">{stat.label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Team Section */}
      <section className="py-16 md:py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-slate-900 mb-4">Our Leadership Team</h2>
            <p className="text-lg text-slate-500">Meet the people behind Aggar</p>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {[
              { name: 'Ahmed Hassan', role: 'CEO & Founder', initials: 'AH' },
              { name: 'Sarah Mitchell', role: 'COO', initials: 'SM' },
              { name: 'James Chen', role: 'CTO', initials: 'JC' },
              { name: 'Emily Rodriguez', role: 'Head of Sales', initials: 'ER' },
            ].map((member) => (
              <div key={member.name} className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 text-center">
                <div className="w-20 h-20 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <span className="text-xl font-bold text-primary-700">{member.initials}</span>
                </div>
                <h3 className="font-semibold text-slate-900 mb-1">{member.name}</h3>
                <p className="text-sm text-slate-500">{member.role}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Contact CTA */}
      <section className="bg-primary-600 text-white py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl font-bold mb-4">Ready to Work With Us?</h2>
          <p className="text-xl text-primary-100 mb-8">We'd love to hear from you</p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <div className="flex items-center gap-2">
              <Phone className="w-5 h-5" />
              <span>+1 (555) 123-4567</span>
            </div>
            <div className="flex items-center gap-2">
              <Mail className="w-5 h-5" />
              <span>info@aggar.com</span>
            </div>
            <div className="flex items-center gap-2">
              <MapPin className="w-5 h-5" />
              <span>New York, NY</span>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
