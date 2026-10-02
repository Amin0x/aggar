import { Cookie, ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export function CookiePolicy() {
  return (
    <div className="min-h-screen bg-white py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          {/* Header */}
          <div className="flex items-center gap-3 mb-8">
            <div className="w-10 h-10 bg-primary-50 rounded-xl flex items-center justify-center">
              <Cookie className="w-5 h-5 text-primary-600" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-slate-900">Cookie Policy</h1>
              <p className="text-sm text-slate-500">Last updated: May 6, 2026</p>
            </div>
          </div>

          <div className="prose prose-slate max-w-none">
            <p className="lead text-lg text-slate-600 mb-8">
              This Cookie Policy explains how Aggar uses cookies and similar technologies to recognize
              you when you visit our platform.
            </p>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">What Are Cookies</h2>
              <p className="text-slate-600">
                Cookies are small text files that are placed on your computer or mobile device when you visit
                a website. They are widely used to make websites work more efficiently and provide
                information to site owners.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">How We Use Cookies</h2>
              <p className="text-slate-600 mb-4">We use cookies for the following purposes:</p>
              <ul className="list-disc pl-6 text-slate-600 space-y-2">
                <li><strong>Essential:</strong> Required for the platform to function properly</li>
                <li><strong>Preferences:</strong> Remember your settings and preferences</li>
                <li><strong>Analytics:</strong> Understand how visitors use our platform</li>
                <li><strong>Marketing:</strong> Show you relevant property advertisements</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">Types of Cookies We Use</h2>
              <div className="space-y-4">
                <div className="bg-slate-50 rounded-xl p-4">
                  <h3 className="font-semibold text-slate-900 mb-2">Session Cookies</h3>
                  <p className="text-sm text-slate-600">Temporary cookies that expire when you close your browser.</p>
                </div>
                <div className="bg-slate-50 rounded-xl p-4">
                  <h3 className="font-semibold text-slate-900 mb-2">Persistent Cookies</h3>
                  <p className="text-sm text-slate-600">Remain on your device until deleted or they expire.</p>
                </div>
                <div className="bg-slate-50 rounded-xl p-4">
                  <h3 className="font-semibold text-slate-900 mb-2">Third-Party Cookies</h3>
                  <p className="text-sm text-slate-600">Set by our partners for analytics and advertising.</p>
                </div>
              </div>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">Managing Cookies</h2>
              <p className="text-slate-600">
                Most browsers allow you to control cookies through their settings. You can:
              </p>
              <ul className="list-disc pl-6 text-slate-600 space-y-2 mt-4">
                <li>View and delete cookies in your browser settings</li>
                <li>Block third-party cookies</li>
                <li>Set notifications when cookies are being set</li>
                <li>Browse in private/incognito mode</li>
              </ul>
              <p className="text-slate-600 mt-4">
                Note: Disabling certain cookies may affect the functionality of our platform.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">Contact Us</h2>
              <p className="text-slate-600">
                For questions about our Cookie Policy, please contact us at:
                <br />
                <strong>Email:</strong> privacy@aggar.com
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
