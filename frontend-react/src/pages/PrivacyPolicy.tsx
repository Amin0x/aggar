import { Shield, ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export function PrivacyPolicy() {
  const sections = [
    { id: 'information-collect', title: 'Information We Collect' },
    { id: 'how-we-use', title: 'How We Use Your Information' },
    { id: 'information-sharing', title: 'Information Sharing' },
    { id: 'cookies', title: 'Cookies & Tracking' },
    { id: 'your-rights', title: 'Your Rights' },
    { id: 'contact-us', title: 'Contact Us' },
  ];

  return (
    <div className="min-h-screen bg-white py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          {/* Header */}
          <div className="flex items-center gap-3 mb-8">
            <div className="w-10 h-10 bg-primary-50 rounded-xl flex items-center justify-center">
              <Shield className="w-5 h-5 text-primary-600" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-slate-900">Privacy Policy</h1>
              <p className="text-sm text-slate-500">Last updated: May 6, 2026</p>
            </div>
          </div>

          <div className="prose prose-slate max-w-none">
            <p className="lead text-lg text-slate-600 mb-8">
              At Aggar, we take your privacy seriously. This Privacy Policy explains how we collect,
              use, and protect your personal information when you use our platform.
            </p>

            <section id="information-collect" className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">1. Information We Collect</h2>
              <p className="text-slate-600 mb-4">
                We collect information you provide directly when you:
              </p>
              <ul className="list-disc pl-6 text-slate-600 space-y-2">
                <li>Create an account (name, email, phone, username)</li>
                <li>List a property (property details, images, location)</li>
                <li>Contact us (name, email, message content)</li>
              </ul>
              <p className="text-slate-600 mt-4">
                We also automatically collect: IP address, browser type, device information,
                and usage patterns through cookies and similar technologies.
              </p>
            </section>

            <section id="how-we-use" className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">2. How We Use Your Information</h2>
              <p className="text-slate-600 mb-4">We use your information to:</p>
              <ul className="list-disc pl-6 text-slate-600 space-y-2">
                <li>Provide and maintain our services</li>
                <li>Process transactions and send confirmations</li>
                <li>Send you relevant property matches and updates</li>
                <li>Improve our platform and user experience</li>
                <li>Communicate with you about your account</li>
              </ul>
            </section>

            <section id="information-sharing" className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">3. Information Sharing</h2>
              <p className="text-slate-600">
                We do not sell your personal information. We may share your information with:
                property agents (for inquiries), service providers (for platform operations),
                and legal authorities (when required by law).
              </p>
            </section>

            <section id="cookies" className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">4. Cookies & Tracking</h2>
              <p className="text-slate-600">
                We use cookies to enhance your browsing experience, analyze site traffic,
                and personalize content. You can control cookie settings through your browser preferences.
              </p>
            </section>

            <section id="your-rights" className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">5. Your Rights</h2>
              <p className="text-slate-600 mb-4">You have the right to:</p>
              <ul className="list-disc pl-6 text-slate-600 space-y-2">
                <li>Access the personal data we hold about you</li>
                <li>Request correction of inaccurate data</li>
                <li>Request deletion of your account and data</li>
                <li>Opt-out of marketing communications</li>
              </ul>
            </section>

            <section id="contact-us" className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">6. Contact Us</h2>
              <p className="text-slate-600">
                If you have questions about this Privacy Policy, please contact us at:
                <br />
                <strong>Email:</strong> privacy@aggar.com
                <br />
                <strong>Mail:</strong> 123 Business Ave, Suite 100, New York, NY 10001
              </p>
            </section>
          </div>

          <div className="mt-12 pt-8 border-t border-slate-200">
            <Link to="/" className="text-primary-600 hover:underline inline-flex items-center gap-1">
              <ChevronRight className="w-4 h-4 rotate-180" /> Back to Home
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
