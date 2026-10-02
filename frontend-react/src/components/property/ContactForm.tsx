import { useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Mail, Phone, User, MessageSquare } from 'lucide-react';

interface ContactFormProps {
  propertyTitle: string;
  agentName?: string;
}

export function ContactForm({ propertyTitle, agentName }: ContactFormProps) {
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // In a real app, this would send the message to the agent
    setSubmitted(true);
  };

  if (submitted) {
    return (
      <div className="text-center py-6">
        <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
          <MessageSquare className="w-6 h-6 text-green-600" />
        </div>
        <h3 className="font-semibold text-slate-900 mb-1">Message Sent!</h3>
        <p className="text-sm text-slate-500">The agent will contact you soon.</p>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-3">
      <Input label="Name" placeholder="Your name" required icon={<User className="w-4 h-4" />} />
      <Input label="Email" type="email" placeholder="Your email" required icon={<Mail className="w-4 h-4" />} />
      <Input label="Phone" type="tel" placeholder="Your phone" icon={<Phone className="w-4 h-4" />} />

      <div>
        <label className="block text-sm font-medium text-slate-700 mb-1.5">Message</label>
        <textarea
          defaultValue={`I'm interested in ${propertyTitle}. Please contact me with more information.`}
          rows={4}
          className="w-full px-4 py-2.5 border border-slate-300 rounded-xl text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none resize-none"
        />
      </div>

      <Button type="submit" variant="primary" className="w-full">
        Send Message
      </Button>
    </form>
  );
}