import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from './hooks/useAuth'
import App from './App.tsx'
import './index.css'
import './App.css'

const supportedLanguages = ['ar', 'en'] as const;
const requestedLanguage = window.location.pathname.split('/')[1];
const language = supportedLanguages.includes(requestedLanguage as typeof supportedLanguages[number])
  ? requestedLanguage
  : 'ar';
const languagePrefix = `/${language}`;
if (window.location.pathname !== languagePrefix
  && !window.location.pathname.startsWith(`${languagePrefix}/`)) {
  window.location.replace(
    `${languagePrefix}${window.location.pathname}${window.location.search}${window.location.hash}`,
  );
}
document.documentElement.lang = language;
document.documentElement.dir = language === 'ar' ? 'rtl' : 'ltr';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter basename={languagePrefix}>
        <AuthProvider>
          <App />
        </AuthProvider>
      </BrowserRouter>
    </QueryClientProvider>
  </StrictMode>,
)