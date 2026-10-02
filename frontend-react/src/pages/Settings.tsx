import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { User, Bell, Shield, Key, ChevronRight } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';

interface TabProps {
  id: string;
  label: string;
  icon: React.ElementType;
}

const tabs: TabProps[] = [
  { id: 'account', label: 'Account', icon: User },
  { id: 'notifications', label: 'Notifications', icon: Bell },
  { id: 'security', label: 'Security', icon: Shield },
];

export function Settings() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('account');
  const [saved, setSaved] = useState(false);

  const handleSave = () => {
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  };

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-slate-900 mb-8">Settings</h1>

        <div className="flex flex-col md:flex-row gap-6">
          {/* Sidebar */}
          <div className="md:w-64 flex-shrink-0">
            <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
              {tabs.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`w-full flex items-center gap-3 px-5 py-3.5 text-sm transition-colors ${
                    activeTab === tab.id
                      ? 'bg-primary-50 text-primary-700 font-medium'
                      : 'text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  <tab.icon className="w-4 h-4" />
                  {tab.label}
                  <ChevronRight className="w-4 h-4 ml-auto" />
                </button>
              ))}
            </div>
          </div>

          {/* Content */}
          <div className="flex-1">
            <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 md:p-8">
              {activeTab === 'account' && (
                <div>
                  <h2 className="text-xl font-semibold text-slate-900 mb-6">Account Information</h2>
                  <div className="space-y-5 max-w-lg">
                    <Input label="Full Name" defaultValue={user?.name} />
                    <Input label="Email" type="email" defaultValue={user?.email} />
                    <Input label="Phone" type="tel" defaultValue={user?.phone || ''} />
                    <Input label="Username" defaultValue={user?.username} disabled />
                    <div className="pt-4 border-t border-slate-100">
                      <Button variant="primary" onClick={handleSave}>
                        Save Changes
                      </Button>
                      {saved && (
                        <span className="ml-3 text-sm text-green-600">Changes saved!</span>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {activeTab === 'notifications' && (
                <div>
                  <h2 className="text-xl font-semibold text-slate-900 mb-6">Notification Preferences</h2>
                  <div className="space-y-4">
                    {[
                      'New property alerts',
                      'Price drop notifications',
                      'Message notifications',
                      'Marketing emails',
                    ].map((item) => (
                      <div key={item} className="flex items-center justify-between py-3 border-b border-slate-100">
                        <span className="text-sm text-slate-700">{item}</span>
                        <label className="relative inline-flex items-center cursor-pointer">
                          <input type="checkbox" className="sr-only peer" defaultChecked />
                          <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
                        </label>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {activeTab === 'security' && (
                <div>
                  <h2 className="text-xl font-semibold text-slate-900 mb-6">Security</h2>
                  <div className="space-y-6 max-w-lg">
                    <div>
                      <h3 className="font-medium text-slate-900 mb-3">Change Password</h3>
                      <div className="space-y-3">
                        <Input label="Current Password" type="password" />
                        <Input label="New Password" type="password" />
                        <Input label="Confirm New Password" type="password" />
                        <Button variant="primary" size="sm">Update Password</Button>
                      </div>
                    </div>
                    <div className="pt-6 border-t border-slate-100">
                      <h3 className="font-medium text-slate-900 mb-2">Two-Factor Authentication</h3>
                      <p className="text-sm text-slate-500 mb-3">Add an extra layer of security to your account</p>
                      <Button variant="outline" size="sm">Enable 2FA</Button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
