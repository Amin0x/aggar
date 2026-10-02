import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Home, DollarSign, Ruler, MapPin, Image, CheckCircle, ArrowLeft, ArrowRight } from 'lucide-react';
import { cn } from '../lib/utils';

const steps = [
  { id: 1, label: 'Basic Info', icon: Home },
  { id: 2, label: 'Pricing', icon: DollarSign },
  { id: 3, label: 'Details', icon: Ruler },
  { id: 4, label: 'Location', icon: MapPin },
  { id: 5, label: 'Media & Amenities', icon: Image },
  { id: 6, label: 'Review', icon: CheckCircle },
];

export function AddProperty() {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(1);
  const [submitted, setSubmitted] = useState(false);

  const { register, handleSubmit, watch } = useForm();

  const nextStep = () => setCurrentStep((prev) => Math.min(prev + 1, 6));
  const prevStep = () => setCurrentStep((prev) => Math.max(prev - 1, 1));

  const onSubmit = (data: any) => {
    if (currentStep < 6) {
      nextStep();
      return;
    }
    console.log('Submit property:', data);
    setSubmitted(true);
    setTimeout(() => navigate('/profile'), 2000);
  };

  if (submitted) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 px-4">
        <div className="text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <CheckCircle className="w-8 h-8 text-green-600" />
          </div>
          <h2 className="text-2xl font-bold text-slate-900 mb-2">Property Added Successfully!</h2>
          <p className="text-slate-500">Redirecting to your profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="max-w-3xl mx-auto px-4 sm:px-6">
        <div className="mb-8">
          <button onClick={() => navigate(-1)} className="inline-flex items-center gap-1 text-sm text-slate-500 hover:text-slate-700 mb-4">
            <ArrowLeft className="w-4 h-4" /> Back
          </button>
          <h1 className="text-2xl font-bold text-slate-900">Add New Property</h1>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 md:p-8">
          <form onSubmit={handleSubmit(onSubmit)}>
            {currentStep === 1 && (
              <div className="space-y-5">
                <h2 className="text-xl font-semibold text-slate-900 mb-4">Basic Information</h2>
                <Input label="Property Title" placeholder="e.g. Modern 3-Bedroom Apartment" {...register('title', { required: true })} />
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1.5">Description</label>
                  <textarea rows={4} placeholder="Describe your property..." className="w-full px-4 py-2.5 border border-slate-300 rounded-xl focus:ring-2 focus:ring-primary-500 outline-none resize-none" {...register('description')} />
                </div>
              </div>
            )}

            {currentStep === 2 && (
              <div className="space-y-5">
                <h2 className="text-xl font-semibold text-slate-900 mb-4">Pricing Information</h2>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <Input label="Price *" type="number" placeholder="0.00" {...register('price', { required: true })} />
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1.5">Currency</label>
                    <select {...register('currency')} className="w-full px-4 py-2.5 border border-slate-300 rounded-xl focus:ring-2 focus:ring-primary-500 outline-none">
                      <option value="USD">USD</option>
                      <option value="EUR">EUR</option>
                      <option value="GBP">GBP</option>
                    </select>
                  </div>
                </div>
              </div>
            )}

            {currentStep >= 3 && currentStep <= 6 && (
              <div className="space-y-5">
                <h2 className="text-xl font-semibold text-slate-900 mb-4">Step {currentStep} Content</h2>
                <p className="text-slate-500">Content for step {currentStep} would go here.</p>
              </div>
            )}

            <div className="flex justify-between mt-8 pt-6 border-t border-slate-100">
              {currentStep > 1 ? (
                <Button type="button" variant="outline" onClick={prevStep}>
                  <ArrowLeft className="w-4 h-4 mr-2" /> Previous
                </Button>
              ) : (
                <div />
              )}
              <Button type="submit" variant="primary">
                {currentStep === 6 ? 'Submit Property' : 'Next'} {currentStep < 6 && <ArrowRight className="w-4 h-4 ml-2" />}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
