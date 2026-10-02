import { FileText, ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export function TermsOfService() {
  return (
    <div className="min-h-screen bg-white py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          {/* Header */}
          <div className="flex items-center gap-3 mb-8">
            <div className="w-10 h-10 bg-primary-50 rounded-xl flex items-center justify-center">
              <FileText className="w-5 h-5 text-primary-600" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-slate-900">Terms of Service</h1>
              <p className="text-sm text-slate-500">Last updated: May 6, 2026</p>
            </div>
          </div>

          <div className="prose prose-slate max-w-none">
            <p className="lead text-lg text-slate-600 mb-8">
              These Terms of Service govern your use of the Aggar platform. By accessing or using our
              services, you agree to be bound by these terms.
            </p>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">1. Acceptance of Terms</h2>
              <p className="text-slate-600">
                By creating an account or using Aggar's services, you agree to comply with and be bound by
                these Terms of Service. If you do not agree to these terms, please do not use our platform.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">2. User Accounts</h2>
              <p className="text-slate-600 mb-4">When creating an account, you agree to:</p>
              <ul className="list-disc pl-6 text-slate-600 space-y-2">
                <li>Provide accurate and complete registration information</li>
                <li>Maintain the security of your account credentials</li>
                <li>Notify us immediately of any unauthorized use of your account</li>
                <li>Accept responsibility for all activities under your account</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">3. Property Listings</h2>
              <p className="text-slate-600">
                Users who list properties must ensure that all information provided is accurate, complete,
                and not misleading. Aggar reserves the right to remove any listing that violates
                these terms or contains false information.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">4. Prohibited Activities</h2>
              <p className="text-slate-600 mb-4">You agree not to:</p>
              <ul className="list-disc pl-6 text-slate-600 space-y-2">
                <li>Post false or misleading property information</li>
                <li>Use the platform for any illegal purpose</li>
                <li>Interfere with or disrupt the platform's functionality</li>
                <li>Attempt to gain unauthorized access to other accounts</li>
                <li>Scrape or harvest data from the platform</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">5. Limitation of Liability</h2>
              <p className="text-slate-600">
                Aggar acts as a platform connecting buyers, sellers, and renters. We are not a party to
                any transactions between users. We disclaim all liability for any disputes, losses,
                or damages arising from property transactions.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">6. Changes to Terms</h2>
              <p className="text-slate-600">
                We may modify these terms at any time. We will notify users of significant changes
                via email or platform notification. Continued use of the platform constitutes
                acceptance of the modified terms.
              </p>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-slate-900 mb-4">7. Contact Information</h2>
              <p className="text-slate-600">
                For questions about these Terms of Service, please contact us at:
                <br />
                <strong>Email:</strong> legal@aggar.com
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
