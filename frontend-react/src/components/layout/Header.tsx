import { useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { Button } from '../ui/button';
import {
  Search,
  Menu,
  X,
  User,
  LogOut,
  PlusCircle,
  Heart,
  Settings,
  ChevronDown,
  Building2,
  Home,
  Crown,
  Building,
  Hotel,
  Store,
  Map,
} from 'lucide-react';
import { cn } from '../../lib/utils';

const categories = [
  { id: 'apartment', label: 'Apartments', icon: Building2 },
  { id: 'house', label: 'Houses', icon: Home },
  { id: 'villa', label: 'Villas', icon: Crown },
  { id: 'condo', label: 'Condos', icon: Building },
  { id: 'townhouse', label: 'Townhouses', icon: Hotel },
  { id: 'office', label: 'Office', icon: Building2 },
  { id: 'retail', label: 'Retail', icon: Store },
  { id: 'land', label: 'Land', icon: Map },
];

export function Header() {
  const { user, isAuthenticated, logout } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [categoriesOpen, setCategoriesOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      window.location.href = `/properties?q=${encodeURIComponent(searchQuery.trim())}`;
    }
  };

  return (
    <header className="sticky top-0 z-50 bg-white border-b border-slate-200 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
              <Home className="w-5 h-5 text-white" />
            </div>
            <span className="text-xl font-bold text-slate-900">Aggar</span>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center gap-1">
            <NavLink
              to="/properties"
              className={({ isActive }) =>
                cn('px-3 py-2 rounded-lg text-sm font-medium transition',
                  isActive ? 'bg-primary-50 text-primary-700' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50')
              }
            >
              All Properties
            </NavLink>

            {/* Categories Dropdown */}
            <div className="relative">
              <button
                onClick={() => setCategoriesOpen(!categoriesOpen)}
                className="px-3 py-2 rounded-lg text-sm font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-50 flex items-center gap-1"
              >
                Categories <ChevronDown className="w-4 h-4" />
              </button>
              {categoriesOpen && (
                <>
                  <div className="fixed inset-0 z-10" onClick={() => setCategoriesOpen(false)} />
                  <div className="absolute top-full left-0 mt-1 w-64 bg-white rounded-xl shadow-lg border border-slate-200 py-2 z-20">
                    {categories.map((cat) => (
                      <Link
                        key={cat.id}
                        to={`/properties?category=${cat.id}`}
                        className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition"
                        onClick={() => setCategoriesOpen(false)}
                      >
                        <cat.icon className="w-4 h-4 text-primary-600" />
                        {cat.label}
                      </Link>
                    ))}
                  </div>
                </>
              )}
            </div>
          </nav>

          {/* Search Bar */}
          <form onSubmit={handleSearch} className="hidden md:flex flex-1 max-w-md mx-8">
            <div className="relative w-full">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
              <input
                type="search"
                placeholder="Search properties, locations..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none"
              />
            </div>
          </form>

          {/* Desktop Auth */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <>
                <Link to="/properties/add">
                  <Button variant="primary" size="sm">
                    <PlusCircle className="w-4 h-4 mr-1.5" />
                    Add Property
                  </Button>
                </Link>

                {/* Profile Dropdown */}
                <div className="relative">
                  <button
                    onClick={() => setProfileOpen(!profileOpen)}
                    className="flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-50"
                  >
                    <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
                      <User className="w-4 h-4 text-primary-700" />
                    </div>
                    <span>{user?.name?.split(' ')[0]}</span>
                    <ChevronDown className="w-3 h-3" />
                  </button>
                  {profileOpen && (
                    <>
                      <div className="fixed inset-0 z-10" onClick={() => setProfileOpen(false)} />
                      <div className="absolute top-full right-0 mt-1 w-56 bg-white rounded-xl shadow-lg border border-slate-200 py-2 z-20">
                        <Link
                          to="/profile"
                          className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50"
                          onClick={() => setProfileOpen(false)}
                        >
                          <User className="w-4 h-4" /> Profile
                        </Link>
                        <Link
                          to="/profile/saved"
                          className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50"
                          onClick={() => setProfileOpen(false)}
                        >
                          <Heart className="w-4 h-4" /> Saved Properties
                        </Link>
                        <Link
                          to="/profile/settings"
                          className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50"
                          onClick={() => setProfileOpen(false)}
                        >
                          <Settings className="w-4 h-4" /> Settings
                        </Link>
                        <hr className="my-1 border-slate-100" />
                        <button
                          onClick={() => { setProfileOpen(false); logout(); }}
                          className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-red-600 hover:bg-red-50"
                        >
                          <LogOut className="w-4 h-4" /> Sign Out
                        </button>
                      </div>
                    </>
                  )}
                </div>
              </>
            ) : (
              <>
                <Link to="/login">
                  <Button variant="ghost" size="sm">Sign In</Button>
                </Link>
                <Link to="/register">
                  <Button variant="primary" size="sm">Get Started</Button>
                </Link>
              </>
            )}
          </div>

          {/* Mobile Menu Button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden p-2 rounded-lg text-slate-600 hover:bg-slate-100"
          >
            {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden py-4 border-t border-slate-200">
            <form onSubmit={handleSearch} className="mb-4">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                <input
                  type="search"
                  placeholder="Search properties..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 outline-none"
                />
              </div>
            </form>

            <nav className="space-y-1">
              <NavLink
                to="/properties"
                className={({ isActive }) =>
                  cn('block px-3 py-2 rounded-lg text-sm font-medium',
                    isActive ? 'bg-primary-50 text-primary-700' : 'text-slate-600')
                }
                onClick={() => setMobileMenuOpen(false)}
              >
                All Properties
              </NavLink>

              <div className="px-3 py-2 text-sm font-semibold text-slate-400 uppercase">Categories</div>
              {categories.map((cat) => (
                <Link
                  key={cat.id}
                  to={`/properties?category=${cat.id}`}
                  className="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-slate-700"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  <cat.icon className="w-4 h-4 text-primary-600" />
                  {cat.label}
                </Link>
              ))}
            </nav>

            <div className="mt-4 pt-4 border-t border-slate-200">
              {isAuthenticated ? (
                <div className="space-y-2">
                  <div className="px-3 py-2 text-sm font-medium text-slate-900">{user?.name}</div>
                  <Link to="/profile" className="block px-3 py-2 text-sm text-slate-600" onClick={() => setMobileMenuOpen(false)}>
                    Profile
                  </Link>
                  <Link to="/profile/saved" className="block px-3 py-2 text-sm text-slate-600" onClick={() => setMobileMenuOpen(false)}>
                    Saved Properties
                  </Link>
                  <Link to="/properties/add" className="block px-3 py-2 text-sm text-slate-600" onClick={() => setMobileMenuOpen(false)}>
                    Add Property
                  </Link>
                  <button onClick={logout} className="block w-full text-left px-3 py-2 text-sm text-red-600">
                    Sign Out
                  </button>
                </div>
              ) : (
                <div className="flex gap-2">
                  <Link to="/login" className="flex-1" onClick={() => setMobileMenuOpen(false)}>
                    <Button variant="ghost" className="w-full">Sign In</Button>
                  </Link>
                  <Link to="/register" className="flex-1" onClick={() => setMobileMenuOpen(false)}>
                    <Button variant="primary" className="w-full">Get Started</Button>
                  </Link>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </header>
  );
}