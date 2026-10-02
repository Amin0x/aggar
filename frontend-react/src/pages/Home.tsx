import { HeroSection } from '../components/home/HeroSection';
import { StatsSection } from '../components/home/StatsSection';
import { CategoryGrid } from '../components/home/CategoryGrid';
import { FeaturedProperties } from '../components/home/FeaturedProperties';
import { HowItWorks } from '../components/home/HowItWorks';
import { Testimonials } from '../components/home/Testimonials';
import { CTASection } from '../components/home/CTASection';

export function Home() {
  return (
    <div className="page-transition">
      <HeroSection />
      <StatsSection />
      <CategoryGrid />
      <FeaturedProperties />
      <HowItWorks />
      <Testimonials />
      <CTASection />
    </div>
  );
}