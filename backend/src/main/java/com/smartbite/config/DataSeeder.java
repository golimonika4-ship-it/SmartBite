package com.smartbite.config;

import com.smartbite.model.MenuItem;
import com.smartbite.service.MenuItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.smartbite.model.Role;
import com.smartbite.model.User;
import com.smartbite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final MenuItemService menuItemService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${smartbite.admin.name:Campus Canteen Administrator}")
    private String adminName;

    @Value("${smartbite.admin.email:admin@smartbite.campus}")
    private String adminEmail;

    @Value("${smartbite.admin.roll:ADMIN-01}")
    private String adminRoll;

    @Value("${smartbite.admin.password:}")
    private String adminPassword;

    public DataSeeder(
            MenuItemService menuItemService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.menuItemService = menuItemService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedInitialAdminUser();
        seedMenuItemsIfEmpty();
    }

    private void seedInitialAdminUser() {
        if (adminPassword == null || adminPassword.isBlank()) {
            if (userRepository.existsByRole(Role.ADMIN)) {
                logger.info("Admin account already exists in database. Skipping admin password sync (SMARTBITE_ADMIN_PASSWORD is empty).");
            } else {
                logger.warn("Initial admin password not configured (SMARTBITE_ADMIN_PASSWORD is empty). Skipping automated admin creation.");
            }
            return;
        }

        String cleanEmail = adminEmail != null && !adminEmail.isBlank() ? adminEmail.trim().toLowerCase() : "admin@smartbite.campus";
        String cleanRoll = adminRoll != null && !adminRoll.isBlank() ? adminRoll.trim().toUpperCase() : "ADMIN-01";
        String cleanName = adminName != null && !adminName.isBlank() ? adminName.trim() : "Campus Canteen Administrator";

        // Find existing admin by email, roll number, or role
        User adminUser = userRepository.findByEmailIgnoreCase(cleanEmail)
                .or(() -> userRepository.findByRollNumberIgnoreCase(cleanRoll))
                .or(() -> userRepository.findAll().stream().filter(u -> u.getRole() == Role.ADMIN).findFirst())
                .orElse(null);

        if (adminUser != null) {
            boolean updated = false;
            if (adminUser.getRole() != Role.ADMIN) {
                adminUser.setRole(Role.ADMIN);
                updated = true;
            }
            if (!cleanName.equals(adminUser.getName())) {
                adminUser.setName(cleanName);
                updated = true;
            }
            if (!cleanEmail.equalsIgnoreCase(adminUser.getEmail())) {
                adminUser.setEmail(cleanEmail);
                updated = true;
            }
            if (!cleanRoll.equalsIgnoreCase(adminUser.getRollNumber())) {
                adminUser.setRollNumber(cleanRoll);
                updated = true;
            }
            if (!passwordEncoder.matches(adminPassword, adminUser.getPassword())) {
                adminUser.setPassword(passwordEncoder.encode(adminPassword));
                updated = true;
                logger.info("Admin password updated with new secure BCrypt hash.");
            }
            if (updated) {
                userRepository.save(adminUser);
                logger.info("Admin user credentials synchronized successfully for '{}'.", cleanEmail);
            } else {
                logger.info("Admin account already in sync for '{}'.", cleanEmail);
            }
        } else {
            logger.info("No admin account found. Creating initial admin account for '{}'...", cleanEmail);
            User newAdmin = new User(
                    cleanName,
                    cleanRoll,
                    cleanEmail,
                    passwordEncoder.encode(adminPassword),
                    Role.ADMIN
            );
            userRepository.save(newAdmin);
            logger.info("Initial admin account created successfully with secure BCrypt hash.");
        }
    }

    private void seedMenuItemsIfEmpty() {
        long count = menuItemService.count();
        if (count > 0) {
            logger.info("Database already contains {} menu items. Skipping seeder to preserve existing records.", count);
            return;
        }

        logger.info("Menu table is empty. Seeding initial 16 campus canteen menu items...");

        List<MenuItem> initialItems = List.of(
            // --- BREAKFAST ---
            new MenuItem(
                "sb-b1",
                "Crispy Masala Dosa",
                "breakfast",
                "Breakfast",
                50.0,
                true,
                "Available",
                4.8,
                "8-10 mins",
                "Crispy golden fermented crepe filled with savory spiced potato masala, served with freshly ground coconut chutney and hot sambar.",
                "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-b2",
                "Steamed Idli & Medu Vada Combo",
                "breakfast",
                "Breakfast",
                40.0,
                true,
                "Available",
                4.7,
                "5-7 mins",
                "Two melt-in-mouth steamed rice idlis and one crispy golden medu vada served with piping hot dal sambar and spicy tomato chutney.",
                "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-b3",
                "Fluffy Poori Bhaji (3 pcs)",
                "breakfast",
                "Breakfast",
                45.0,
                true,
                "Available",
                4.6,
                "8-10 mins",
                "Three puffed golden whole-wheat pooris served with traditional spiced potato bhaji curry and tangy lemon pickle.",
                "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-b4",
                "Indori Poha with Crispy Sev",
                "breakfast",
                "Breakfast",
                30.0,
                true,
                "Available",
                4.9,
                "4-5 mins",
                "Steamed flattened rice tempered with mustard seeds, crunchy peanuts, and green chillies, topped with ratlami sev and fresh coriander.",
                "https://images.unsplash.com/photo-1630383249896-424e482df921?auto=format&fit=crop&w=600&q=80"
            ),

            // --- MEALS ---
            new MenuItem(
                "sb-m1",
                "Paneer Butter Masala Thali",
                "meals",
                "Meals",
                110.0,
                true,
                "Available",
                4.9,
                "12-15 mins",
                "Tender paneer cubes in velvety tomato cashew gravy. Served with 2 butter rotis, fragrant jeera rice, yellow dal tadka, and salad.",
                "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-m2",
                "Hyderabadi Chicken Dum Biryani",
                "meals",
                "Meals",
                130.0,
                false,
                "Available",
                4.9,
                "10-12 mins",
                "Long-grain dum basmati rice slow-cooked with marinated tender chicken pieces, aromatic whole spices, cooling raita, and mirchi ka salan.",
                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-m3",
                "Special Deluxe Veg Mini Thali",
                "meals",
                "Meals",
                85.0,
                true,
                "Available",
                4.7,
                "10-12 mins",
                "Balanced campus lunch thali with seasonal paneer sabzi, dal fry, 2 soft phulkas, steamed basmati rice, papad, and mixed pickle.",
                "https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-m4",
                "Homestyle Rajma Chawal Bowl",
                "meals",
                "Meals",
                70.0,
                true,
                "Available",
                4.8,
                "8-10 mins",
                "Hearty North-Indian red kidney beans slow-simmered in rich spiced onion-tomato gravy, served over steaming hot basmati rice with sliced onions.",
                "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=600&q=80"
            ),

            // --- SNACKS ---
            new MenuItem(
                "sb-s1",
                "Veg Cheese Grilled Sandwich",
                "snacks",
                "Snacks",
                60.0,
                true,
                "Available",
                4.6,
                "6-8 mins",
                "Toasted 3-layer sandwich stuffed with sweet corn, bell peppers, fresh mint chutney, mozzarella cheese, and secret sandwich herbs.",
                "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-s2",
                "Crispy Herb Veg Burger",
                "snacks",
                "Snacks",
                75.0,
                true,
                "Available",
                4.5,
                "8-10 mins",
                "Golden crumbed vegetable patty loaded with peri-peri mayo, crunchy lettuce, sliced tomato, and cheddar cheese inside a sesame bun.",
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-s3",
                "Delhi Samosa Chaat (2 pcs)",
                "snacks",
                "Snacks",
                40.0,
                true,
                "Available",
                4.8,
                "5 mins",
                "Crispy spiced potato samosas crushed and smothered with warm Punjabi chole, sweetened yogurt, tangy tamarind chutney, and nylon sev.",
                "https://images.unsplash.com/photo-1541544741938-0af808871cc0?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-s4",
                "Peri-Peri Crispy French Fries",
                "snacks",
                "Snacks",
                50.0,
                true,
                "Available",
                4.6,
                "6 mins",
                "Golden crunchy potato fries tossed in zesty peri-peri spice dust, served hot with creamy garlic dip and tomato ketchup.",
                "https://images.unsplash.com/photo-1576107232684-1279f3908594?auto=format&fit=crop&w=600&q=80"
            ),

            // --- BEVERAGES ---
            new MenuItem(
                "sb-v1",
                "Thick Cold Coffee with Ice Cream",
                "beverages",
                "Beverages",
                55.0,
                true,
                "Available",
                4.9,
                "4 mins",
                "Creamy blended cold brew coffee topped with a velvety scoop of vanilla ice cream and rich chocolate syrup drizzle.",
                "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-v2",
                "Special Masala Cutting Chai",
                "beverages",
                "Beverages",
                20.0,
                true,
                "Available",
                4.9,
                "3 mins",
                "Campus favorite freshly boiled Indian tea infused with crushed ginger, cardamom, and clove, served piping hot in an authentic clay kulhad.",
                "https://images.unsplash.com/photo-1576092768241-dec231879fc3?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-v3",
                "Fresh Lemon Mint Mojito Cooler",
                "beverages",
                "Beverages",
                35.0,
                true,
                "Available",
                4.5,
                "3 mins",
                "Sparkling soda cooler infused with hand-crushed fresh mint sprigs, tangy lime juice, and a pinch of black salt for instant hydration.",
                "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=600&q=80"
            ),
            new MenuItem(
                "sb-v4",
                "Traditional Mango Lassi",
                "beverages",
                "Beverages",
                45.0,
                true,
                "Available",
                4.8,
                "4 mins",
                "Thick sweet yogurt smoothie churned with pure Alphonso mango pulp, fragrant cardamom powder, and garnished with slivered almonds.",
                "https://images.unsplash.com/photo-1546173159-315724a31696?auto=format&fit=crop&w=600&q=80"
            )
        );

        menuItemService.saveAll(initialItems);
        logger.info("Successfully seeded {} campus canteen menu items into MySQL.", initialItems.size());
    }
}
