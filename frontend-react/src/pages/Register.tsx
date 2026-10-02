import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/button';
import { Home, User, Mail, Phone, Lock, Eye, EyeOff, Check } from 'lucide-react';
import { cn } from '../lib/utils';

const registerSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  email: z.string().email('Invalid email address'),
  phone: z.string().optional(),
  username: z.string().min(3, 'Username must be at least 3 characters'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string(),
  role: z.enum(['USER', 'AGENT'], { errorMap: () => ({ message: 'Please select a role' }) }),
  acceptTerms: z.boolean().refine((val) => val === true, 'You must accept the terms and conditions'),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
});

type RegisterFormData = z.infer<typeof registerSchema>;

export function Register() {
  const { register: registerUser, error, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      name: '',
      email: '',
      phone: '',
      username: '',
      password: '',
      confirmPassword: '',
      role: 'USER',
      acceptTerms: false,
    },
  });

  const password = watch('password');

  if (isAuthenticated) {
    navigate('/');
    return null;
  }

  const getPasswordStrength = (pass: string) => {
    if (!pass) return { score: 0, label: '' };
    let score = 0;
    if (pass.length >= 8) score++;
    if (/[A-Z]/.test(pass)) score++;
    if (/[0-9]/.test(pass)) score++;
    if (/[^A-Za-z0-9]/.test(pass)) score++;
    const labels = ['Weak', 'Fair', 'Good', 'Strong', 'Very Strong'];
    const colors = ['bg-red-500', 'bg-orange-500', 'bg-yellow-500', 'bg-green-500', 'bg-emerald-600'];
    return { score, label: labels[score], color: colors[score] };
  };

  const passwordStrength = getPasswordStrength(password || '');

  const onSubmit = async (data: RegisterFormData) => {
    setLoading(true);
    try {
      await registerUser({
        name: data.name,
        email: data.email,
        phone: data.phone,
        username: data.username,
        password: data.password,
        confirmPassword: data.confirmPassword,
        role: data.role,
      });
      navigate('/');
    } catch {
      // Error is handled by auth context
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-primary-50 px-4 py-12">
      <div className="w-full max-w-lg">
        {/* Logo */}
        <div className="text-center mb-8">
          <Link to="/" className="inline-flex items-center gap-2">
            <div className="w-12 h-12 bg-primary-600 rounded-xl flex items-center justify-center">
              <Home className="w-7 h-7 text-white" />
            </div>
            <span className="text-3xl font-bold text-slate-900">Aggar</span>
          </Link>
          <h1 className="text-2xl font-bold text-slate-900 mt-6 mb-2">Create Account</h1>
          <p className="text-slate-500">Join Aggar and find your dream property</p>
        </div>

        {/* Register Card */}
        <div className="bg-white rounded-2xl shadow-xl border border-slate-100 p-8">
          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {/* Name */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Full Name</label>
              <div className="relative">
                <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                <input
                  type="text"
                  placeholder="Enter your full name"
                  className={cn('w-full pl-10 pr-4 py-2.5 border rounded-xl text-slate-900 placeholder-slate-400',
                    'focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none',
                    errors.name && 'border-red-500')}
                  {...register('name')}
                />
              </div>
              {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>}
            </div>

            {/* Email & Phone Row */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1.5">Email</label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                  <input
                    type="email"
                    placeholder="Email address"
                    className={cn('w-full pl-10 pr-4 py-2.5 border rounded-xl text-slate-900 placeholder-slate-400',
                      'focus:ring-2 focus:ring-primary-500 outline-none',
                      errors.email && 'border-red-500')}
                    {...register('email')}
                  />
                </div>
                {errors.email && <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1.5">Phone (Optional)</label>
                <div className="relative">
                  <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                  <input
                    type="tel"
                    placeholder="Phone number"
                    className="w-full pl-10 pr-4 py-2.5 border border-slate-300 rounded-xl text-slate-900 placeholder-slate-400 focus:ring-2 focus:ring-primary-500 outline-none"
                    {...register('phone')}
                  />
                </div>
              </div>
            </div>

            {/* Username */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Username</label>
              <div className="relative">
                <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                <input
                  type="text"
                  placeholder="Choose a username"
                  className={cn('w-full pl-10 pr-4 py-2.5 border rounded-xl text-slate-900 placeholder-slate-400',
                    'focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none',
                    errors.username && 'border-red-500')}
                  {...register('username')}
                />
              </div>
              {errors.username && <p className="mt-1 text-sm text-red-600">{errors.username.message}</p>}
            </div>

            {/* Password */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Create a password"
                  className={cn('w-full pl-10 pr-10 py-2.5 border rounded-xl text-slate-900 placeholder-slate-400',
                    'focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none',
                    errors.password && 'border-red-500')}
                  {...register('password')}
                />
                <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400">
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
              {password && (
                <div className="mt-2">
                  <div className="flex gap-1 mb-1">
                    {[1, 2, 3, 4].map((i) => (
                      <div key={i} className={`h-1 flex-1 rounded-full ${i <= passwordStrength.score ? passwordStrength.color : 'bg-slate-200'}`} />
                    ))}
                  </div>
                  {passwordStrength.label && (
                    <p className="text-xs text-slate-500">Password strength: <span className="font-medium">{passwordStrength.label}</span></p>
                  )}
                </div>
              )}
              {errors.password && <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>}
            </div>

            {/* Confirm Password */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Confirm Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                <input
                  type={showConfirmPassword ? 'text' : 'password'}
                  placeholder="Confirm your password"
                  className={cn('w-full pl-10 pr-10 py-2.5 border rounded-xl text-slate-900 placeholder-slate-400',
                    'focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none',
                    errors.confirmPassword && 'border-red-500')}
                  {...register('confirmPassword')}
                />
                <button type="button" onClick={() => setShowConfirmPassword(!showConfirmPassword)} className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400">
                  {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
              {errors.confirmPassword && <p className="mt-1 text-sm text-red-600">{errors.confirmPassword.message}</p>}
            </div>

            {/* Role Selection */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">I am a:</label>
              <div className="grid grid-cols-2 gap-3">
                <label className={cn('flex items-center justify-center gap-2 p-3 border-2 rounded-xl cursor-pointer transition',
                  watch('role') === 'USER' ? 'border-primary-600 bg-primary-50' : 'border-slate-200 hover:border-slate-300')}>
                  <input type="radio" value="USER" className="sr-only" {...register('role')} />
                  <Check className={cn('w-5 h-5', watch('role') === 'USER' ? 'text-primary-600' : 'text-slate-300')} />
                  <span className={cn('font-medium', watch('role') === 'USER' ? 'text-primary-700' : 'text-slate-600')}>Buyer/Renter</span>
                </label>
                <label className={cn('flex items-center justify-center gap-2 p-3 border-2 rounded-xl cursor-pointer transition',
                  watch('role') === 'AGENT' ? 'border-primary-600 bg-primary-50' : 'border-slate-200 hover:border-slate-300')}>
                  <input type="radio" value="AGENT" className="sr-only" {...register('role')} />
                  <Check className={cn('w-5 h-5', watch('role') === 'AGENT' ? 'text-primary-600' : 'text-slate-300')} />
                  <span className={cn('font-medium', watch('role') === 'AGENT' ? 'text-primary-700' : 'text-slate-600')}>Agent</span>
                </label>
              </div>
              {errors.role && <p className="mt-1 text-sm text-red-600">{errors.role.message}</p>}
            </div>

            {/* Terms */}
            <label className="flex items-start gap-2 cursor-pointer">
              <input
                type="checkbox"
                className="mt-0.5 w-4 h-4 rounded border-slate-300 text-primary-600 focus:ring-primary-500"
                {...register('acceptTerms')}
              />
              <span className="text-sm text-slate-600">
                I agree to the{' '}
                <Link to="/terms-of-service" className="text-primary-600 hover:underline">Terms of Service</Link>
                {' '}and{' '}
                <Link to="/privacy-policy" className="text-primary-600 hover:underline">Privacy Policy</Link>
              </span>
            </label>
            {errors.acceptTerms && <p className="text-sm text-red-600">{errors.acceptTerms.message}</p>}

            <Button type="submit" variant="primary" size="lg" loading={loading} className="w-full">
              Create Account
            </Button>
          </form>

          <p className="mt-6 text-center text-sm text-slate-500">
            Already have an account?{' '}
            <Link to="/login" className="text-primary-600 hover:text-primary-700 font-medium">
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}