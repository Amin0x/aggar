package com.amin.aggar.config;

import com.amin.aggar.domain.entity.Amenity;
import com.amin.aggar.domain.entity.City;
import com.amin.aggar.domain.entity.Neighborhood;
import com.amin.aggar.domain.entity.Property;
import com.amin.aggar.domain.entity.PropertyImage;
import com.amin.aggar.domain.entity.State;
import com.amin.aggar.domain.entity.User;
import com.amin.aggar.domain.enums.ListingType;
import com.amin.aggar.domain.enums.PricePeriod;
import com.amin.aggar.repository.AmenityRepository;
import com.amin.aggar.repository.CityRepository;
import com.amin.aggar.repository.NeighborhoodRepository;
import com.amin.aggar.repository.PropertyRepository;
import com.amin.aggar.repository.StateRepository;
import com.amin.aggar.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DatabaseSeeder implements ApplicationRunner {

    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            StateRepository stateRepository,
            CityRepository cityRepository,
            NeighborhoodRepository neighborhoodRepository,
            UserRepository userRepository,
            AmenityRepository amenityRepository,
            PropertyRepository propertyRepository,
            PasswordEncoder passwordEncoder) {
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.userRepository = userRepository;
        this.amenityRepository = amenityRepository;
        this.propertyRepository = propertyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (hasData()) {
            return;
        }

        Map<String, State> states = seedStates();
        Map<String, City> cities = seedCities(states);
        Map<String, Neighborhood> neighborhoods = seedNeighborhoods(cities);
        Map<String, User> users = seedUsers();
        Map<String, Amenity> amenities = seedAmenities();
        seedProperties(states, cities, neighborhoods, users, amenities);
    }

    private boolean hasData() {
        return stateRepository.count() > 0
                || cityRepository.count() > 0
                || neighborhoodRepository.count() > 0
                || userRepository.count() > 0
                || amenityRepository.count() > 0
                || propertyRepository.count() > 0;
    }

    private Map<String, State> seedStates() {
        Map<String, State> states = new LinkedHashMap<>();
        addState(states, "California", "كاليفورنيا", "CA");
        addState(states, "New York", "نيويورك", "NY");
        addState(states, "Texas", "تكساس", "TX");
        addState(states, "Florida", "فلوريدا", "FL");
        addState(states, "Washington", "واشنطن", "WA");
        stateRepository.saveAll(states.values());
        return states;
    }

    private void addState(Map<String, State> states, String key, String name, String code) {
        State state = new State();
        state.setName(name);
        state.setCode(code);
        states.put(key, state);
    }

    private Map<String, City> seedCities(Map<String, State> states) {
        Map<String, City> cities = new LinkedHashMap<>();
        addCity(cities, states, "Los Angeles", "لوس أنجلوس", "California");
        addCity(cities, states, "San Francisco", "سان فرانسيسكو", "California");
        addCity(cities, states, "San Diego", "سان دييغو", "California");
        addCity(cities, states, "New York City", "مدينة نيويورك", "New York");
        addCity(cities, states, "Brooklyn", "بروكلين", "New York");
        addCity(cities, states, "Queens", "كوينز", "New York");
        addCity(cities, states, "Houston", "هيوستن", "Texas");
        addCity(cities, states, "Dallas", "دالاس", "Texas");
        addCity(cities, states, "Austin", "أوستن", "Texas");
        addCity(cities, states, "Miami", "ميامي", "Florida");
        addCity(cities, states, "Orlando", "أورلاندو", "Florida");
        addCity(cities, states, "Seattle", "سياتل", "Washington");
        addCity(cities, states, "Bellevue", "بلفيو", "Washington");
        cityRepository.saveAll(cities.values());
        return cities;
    }

    private void addCity(
            Map<String, City> cities, Map<String, State> states, String key, String name, String stateKey) {
        City city = new City();
        city.setName(name);
        city.setState(states.get(stateKey));
        cities.put(key, city);
    }

    private Map<String, Neighborhood> seedNeighborhoods(Map<String, City> cities) {
        Map<String, Neighborhood> neighborhoods = new LinkedHashMap<>();
        addNeighborhood(neighborhoods, cities, "Beverly Hills", "Los Angeles");
        addNeighborhood(neighborhoods, cities, "Hollywood", "Los Angeles");
        addNeighborhood(neighborhoods, cities, "Downtown LA", "Los Angeles");
        addNeighborhood(neighborhoods, cities, "SOMA", "San Francisco");
        addNeighborhood(neighborhoods, cities, "Mission District", "San Francisco");
        addNeighborhood(neighborhoods, cities, "Pacific Heights", "San Francisco");
        addNeighborhood(neighborhoods, cities, "Manhattan", "New York City");
        addNeighborhood(neighborhoods, cities, "Upper East Side", "New York City");
        addNeighborhood(neighborhoods, cities, "SoHo", "New York City");
        addNeighborhood(neighborhoods, cities, "Downtown Houston", "Houston");
        addNeighborhood(neighborhoods, cities, "The Heights", "Houston");
        addNeighborhood(neighborhoods, cities, "Capitol Hill", "Seattle");
        addNeighborhood(neighborhoods, cities, "Fremont", "Seattle");
        neighborhoodRepository.saveAll(neighborhoods.values());
        return neighborhoods;
    }

    private void addNeighborhood(
            Map<String, Neighborhood> neighborhoods, Map<String, City> cities, String name, String cityName) {
        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setName(name);
        neighborhood.setCity(cities.get(cityName));
        neighborhoods.put(name, neighborhood);
    }

    private Map<String, User> seedUsers() {
        Map<String, User> users = new LinkedHashMap<>();
        addUser(users, "admin", "admin123", "Admin User", "admin@aggar.com", "+1-555-0001", "admin");
        addUser(users, "john_doe", "password123", "John Doe", "john@example.com", "+1-555-0101", "owner");
        addUser(users, "jane_smith", "password123", "Jane Smith", "jane@example.com", "+1-555-0102", "owner");
        addUser(users, "mike_wilson", "password123", "Mike Wilson", "mike@example.com", "+1-555-0103", "agent");
        addUser(users, "sarah_connor", "password123", "Sarah Connor", "sarah@example.com", "+1-555-0104", "owner");
        userRepository.saveAll(users.values());
        return users;
    }

    private void addUser(
            Map<String, User> users, String username, String password, String name, String email, String phone, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        users.put(username, user);
    }

    private Map<String, Amenity> seedAmenities() {
        Map<String, Amenity> amenities = new LinkedHashMap<>();
        for (String name : List.of(
                "Swimming Pool", "Gym", "Parking", "Air Conditioning", "Heating", "Washer/Dryer",
                "Dishwasher", "Balcony", "Garden", "Security System", "Elevator", "Furnished")) {
            Amenity amenity = new Amenity();
            amenity.setName(name);
            amenities.put(name, amenity);
        }
        amenityRepository.saveAll(amenities.values());
        return amenities;
    }

    private void seedProperties(
            Map<String, State> states,
            Map<String, City> cities,
            Map<String, Neighborhood> neighborhoods,
            Map<String, User> users,
            Map<String, Amenity> amenities) {
        List<SeedProperty> seeds = List.of(
                    property(ListingType.SALE, "شقة عصرية في وسط المدينة", "modern-downtown-apartment", "شقة عصرية جميلة في قلب وسط المدينة، تتميز بإطلالات خلابة على المدينة. تم تجديدها مؤخراً بتشطيبات عالية الجودة.", "450000", null, "apartment", 2, 2, 3, 1, "1200", "New York", "New York City", "Upper East Side", "john_doe", "mike_wilson", "available", 40.7128, -74.0060,
                        List.of("Gym", "Parking", "Air Conditioning", "Washer/Dryer"),
                        "photo-1545324418-cc1a3fa10c00", "photo-1560448204-e02f11c3d0e2", "photo-1484154218962-a197022b5858"),
                    property(ListingType.RENT, "منزل عائلي واسع", "spacious-family-home", "منزل عائلي مثالي يضم فناءً خلفياً واسعاً ومطبخاً مجدداً، ويقع بالقرب من المدارس في حي هادئ.", "3500", PricePeriod.MONTHLY, "house", 4, 3, 6, 2, "2400", "California", "Los Angeles", "Beverly Hills", "john_doe", "mike_wilson", "available", 34.0522, -118.2437,
                        List.of("Swimming Pool", "Gym", "Parking", "Air Conditioning", "Heating", "Garden"),
                        "photo-1570129477492-45c003edd2be", "photo-1556020685-ae41ab02f604"),
                    property(ListingType.SALE, "فيلا فاخرة مع مسبح", "luxury-villa-with-pool", "فيلا فاخرة مذهلة تضم مسبحاً خاصاً ومنتجعاً صحياً وإطلالات بانورامية. كما تحتوي على مطبخ راقٍ بأسطح من الرخام.", "1250000", null, "villa", 5, 4, 8, 2, "4500", "California", "Los Angeles", "Beverly Hills", "sarah_connor", null, "available", 34.0736, -118.4004,
                        List.of("Swimming Pool", "Gym", "Parking", "Air Conditioning", "Washer/Dryer", "Balcony", "Garden", "Security System", "Elevator"),
                        "photo-1613490493576-7fde63acd811", "photo-1583608205776-b60c6b68b98a", "photo-1512917774080-9991f1c4c750"),
                    property(ListingType.RENT, "استوديو مريح في سوما", "cozy-studio-in-soma", "استوديو عملي ومريح في حي سوما الحيوي، وعلى مسافة قريبة سيراً على الأقدام من شركات التقنية والمطاعم.", "2200", PricePeriod.MONTHLY, "apartment", 0, 1, 1, 1, "550", "California", "San Francisco", "SOMA", "john_doe", null, "available", 37.7749, -122.4194,
                        List.of("Gym", "Parking", "Washer/Dryer", "Dishwasher"),
                        "photo-1522708323597-dfb57e8d3d59", "photo-1502672260266-1c616624a332"),
                    property(ListingType.SALE, "شقة فاخرة مطلة على الواجهة البحرية", "waterfront-condo", "شقة فاخرة على الواجهة البحرية بإطلالات رائعة على المحيط. يضم المبنى صالة رياضية ومسبحاً وأمنًا على مدار الساعة.", "750000", null, "condo", 3, 2, 4, 1, "1800", "Florida", "Miami", null, "sarah_connor", null, "available", 25.7617, -80.1918,
                        List.of("Swimming Pool", "Gym", "Parking", "Security System", "Elevator"),
                        "photo-1512917774080-9991f1c4c750", "photo-1545324418-cc1a3fa10c00"),
                    property(ListingType.RENT, "منزل تاون هاوس عصري", "modern-townhouse", "منزل تاون هاوس حديث البناء بتشطيبات عصرية، ويتميز بشرفة على السطح تطل على المدينة.", "4200", PricePeriod.MONTHLY, "townhouse", 3, 3, 5, 3, "2000", "New York", "Brooklyn", null, "john_doe", "mike_wilson", "available", 40.6782, -73.9442,
                        List.of("Gym", "Parking", "Air Conditioning", "Washer/Dryer", "Balcony"),
                        "photo-1583608205776-b60c6b68b98a", "photo-1570129477492-45c003edd2be"),
                    property(ListingType.SALE, "منزل ساحر في أوستن", "charming-house-in-austin", "منزل جميل في حي مرغوب بمدينة أوستن، على قطعة أرض واسعة تحيط بها أشجار ناضجة، مع تصميم داخلي مجدد.", "580000", null, "house", 3, 2, 5, 1, "2100", "Texas", "Austin", null, "sarah_connor", null, "available", 30.2672, -97.7431,
                        List.of("Swimming Pool", "Parking", "Air Conditioning", "Heating", "Washer/Dryer", "Garden"),
                        "photo-1564013799919-bb13f65a9418", "photo-1556020685-ae41ab02f604"),
                    property(ListingType.RENT, "شقة علوية في وسط المدينة", "downtown-loft", "شقة علوية بطابع صناعي أنيق في حي الفنون بوسط المدينة، تتميز بأسقف عالية وجدران من الطوب المكشوف ومرافق عصرية.", "2800", PricePeriod.MONTHLY, "apartment", 1, 1, 2, 1, "900", "Texas", "Houston", "Downtown Houston", "john_doe", null, "available", 29.7604, -95.3698,
                        List.of("Gym", "Parking", "Washer/Dryer", "Dishwasher", "Balcony"),
                        "photo-1502672260266-1c616624a332", "photo-1522708323597-dfb57e8d3d59"),
                    property(ListingType.SALE, "عقار بانتظار الموافقة", "pending-approval-property", "هذا العقار بانتظار موافقة الإدارة، ومن المفترض أن يظهر في لوحة التحكم.", "350000", null, "house", 3, 2, 5, 1, "1800", "California", "Los Angeles", "Beverly Hills", "sarah_connor", null, "pending", 34.0522, -118.2437,
                        List.of()));

        List<Property> properties = seeds.stream()
                .map(seed -> createProperty(seed, states, cities, neighborhoods, users, amenities))
                .toList();
        propertyRepository.saveAll(properties);
    }

    private SeedProperty property(
            ListingType listingType, String title, String slug, String description, String price, PricePeriod pricePeriod,
            String category, int bedrooms, int bathrooms, int rooms, int floors, String area,
            String state, String city, String neighborhood, String owner, String agent, String status,
            double latitude, double longitude, List<String> amenities, String... imageIds) {
        return new SeedProperty(listingType, title, slug, description, new BigDecimal(price), pricePeriod,
                category, bedrooms, bathrooms, rooms, floors, new BigDecimal(area),
                state, city, neighborhood, owner, agent, status, latitude, longitude, amenities, List.of(imageIds));
    }

    private Property createProperty(
            SeedProperty seed,
            Map<String, State> states,
            Map<String, City> cities,
            Map<String, Neighborhood> neighborhoods,
            Map<String, User> users,
            Map<String, Amenity> amenities) {
        Property property = new Property();
        property.setListingType(seed.listingType());
        property.setTitle(seed.title());
        property.setSlug(seed.slug());
        property.setDescription(seed.description());
        property.setPrice(seed.price());
        property.setCurrency("USD");
        property.setPricePeriod(seed.pricePeriod());
        property.setCategory(seed.category());
        property.setBedrooms(seed.bedrooms());
        property.setBathrooms(seed.bathrooms());
        property.setRooms(seed.rooms());
        property.setFloors(seed.floors());
        property.setArea(seed.area());
        property.setState(states.get(seed.state()));
        property.setCity(cities.get(seed.city()));
        property.setNeighborhood(seed.neighborhood() == null ? null : neighborhoods.get(seed.neighborhood()));
        property.setOwner(users.get(seed.owner()));
        property.setAgent(seed.agent() == null ? null : users.get(seed.agent()));
        property.setStatus(seed.status());
        property.setLocationLat(seed.latitude());
        property.setLocationLng(seed.longitude());
        property.setIsDeleted(false);
        property.setAmenities(seed.amenities().stream().map(amenities::get).collect(Collectors.toSet()));

        List<PropertyImage> images = new ArrayList<>();
        for (int i = 0; i < seed.imageIds().size(); i++) {
            PropertyImage image = new PropertyImage();
            image.setProperty(property);
            image.setUrl("https://images.unsplash.com/" + seed.imageIds().get(i) + "?w=800");
            image.setIsPrimary(i == 0);
            image.setSortOrder(i);
            images.add(image);
        }
        property.setImages(images);
        return property;
    }

    private record SeedProperty(
            ListingType listingType, String title, String slug, String description, BigDecimal price, PricePeriod pricePeriod,
            String category, int bedrooms, int bathrooms, int rooms, int floors, BigDecimal area,
            String state, String city, String neighborhood, String owner, String agent, String status,
            double latitude, double longitude, List<String> amenities, List<String> imageIds) {
    }
}
