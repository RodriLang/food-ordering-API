package com.group_three.food_ordering.configs;

import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.enums.*;
import com.group_three.food_ordering.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final FoodVenueRepository foodVenueRepository;
    private final ProductRepository productRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final TableSessionRepository tableSessionRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmploymentRepository employmentRepository;


    @Value("${password-for-all-users}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        // Categories

        if (categoryRepository.count() == 0) {

            // Categorías raíz
            Category drinks = Category.builder().name("Drinks").build();
            Category food = Category.builder().name("Food").build();
            Category desserts = Category.builder().name("Desserts").build();

            categoryRepository.saveAll(List.of(drinks, food, desserts));

            // Subcategorías de Food
            Category mainCourses = Category.builder().name("Main Courses").parentCategory(food).build();
            Category appetizers = Category.builder().name("Appetizers").parentCategory(food).build();

            categoryRepository.saveAll(List.of(mainCourses, appetizers));

            // Subcategorías de Drinks
            Category alcoholic = Category.builder().name("Alcoholic").parentCategory(drinks).build();
            Category nonAlcoholic = Category.builder().name("Non-Alcoholic").parentCategory(drinks).build();

            categoryRepository.saveAll(List.of(alcoholic, nonAlcoholic));

            // Subcategorías de Desserts
            Category sweet = Category.builder().name("Sweet").parentCategory(desserts).build();
            Category savory = Category.builder().name("Savory").parentCategory(desserts).build();

            categoryRepository.saveAll(List.of(sweet, savory));

            // Subcategorías de Main Courses
            Category pizzas = Category.builder().name("Pizzas").parentCategory(mainCourses).build();
            Category burgers = Category.builder().name("Burgers").parentCategory(mainCourses).build();
            Category pasta = Category.builder().name("Pasta").parentCategory(mainCourses).build();
            Category meet = Category.builder().name("Meet").parentCategory(mainCourses).build();
            Category tacos = Category.builder().name("Tacos").parentCategory(mainCourses).build();

            categoryRepository.saveAll(List.of(pizzas, burgers, pasta, meet, tacos));

            // Subcategorías de Appetizers
            Category frenchFries = Category.builder().name("French Fries").parentCategory(appetizers).build();
            Category calamari = Category.builder().name("Calamari").parentCategory(appetizers).build();

            categoryRepository.saveAll(List.of(frenchFries, calamari));

            // Subcategorías de Alcoholic
            Category beers = Category.builder().name("Beers").parentCategory(alcoholic).build();
            Category wines = Category.builder().name("Wines").parentCategory(alcoholic).build();
            Category cocktails = Category.builder().name("Cocktails").parentCategory(alcoholic).build();

            categoryRepository.saveAll(List.of(beers, wines, cocktails));

            // Subcategorías de Non-Alcoholic
            Category sodas = Category.builder().name("Sodas").parentCategory(nonAlcoholic).build();
            Category waters = Category.builder().name("Waters").parentCategory(nonAlcoholic).build();

            categoryRepository.saveAll(List.of(sodas, waters));
        }

        // Tags
        if (tagRepository.count() == 0) {
            Tag spicy = Tag.builder().label("Spicy").build();
            Tag vegan = Tag.builder().label("Vegan").build();
            Tag glutenFree = Tag.builder().label("Gluten-Free").build();
            Tag fresh = Tag.builder().label("Fresh").build();
            tagRepository.saveAll(List.of(spicy, vegan, glutenFree, fresh));
        }

        // FoodVenues
        if (foodVenueRepository.count() == 0) {
            FoodVenue v1 = FoodVenue.builder()
                    .id(UUID.fromString("46b63071-f6fb-48bf-a2e0-4f7144e5a09b"))
                    .name("Burger House")
                    .email("contact@burgerhouse.com")
                    .phone("1234567891")
                    .imageUrl("https://example.com/burger.jpg")
                    .address(new Address("Main St", "123", "CityA", "ProvinceA", "CountryA", "1000"))
                    .build();
            FoodVenue v2 = FoodVenue.builder()
                    .id(UUID.fromString("a9ff20fc-606b-49cd-a19f-8e434eb0af44"))
                    .name("Pasta Palace")
                    .email("hello@pastapalace.com")
                    .phone("0987654321")
                    .imageUrl("https://example.com/pasta.jpg")
                    .address(new Address("Second St", "456", "CityB", "ProvinceB", "CountryB", "2000"))
                    .build();
            FoodVenue v3 = FoodVenue.builder()
                    .id(UUID.fromString("df365b85-dc66-4437-96fc-da7e8f0e5a4a"))
                    .name("Taco Town")
                    .email("info@tacotown.com")
                    .phone("5555555555")
                    .imageUrl("https://example.com/taco.jpg")
                    .address(new Address("Third St", "789", "CityC", "ProvinceC", "CountryC", "3000"))
                    .build();
            List<FoodVenue> foodVenues = List.of(v1, v2, v3);
            foodVenueRepository.saveAll(foodVenues);
            log.info("[DataInitializer] ===================== FOOD VENUES =======================");
            foodVenues.forEach((foodVenue -> log.info("[DataInitializer] food venue={} UUID={}", foodVenue.getName(), foodVenue.getId())));

            // Products
            List<Category> cats = categoryRepository.findAll();
                List<Tag> tags = tagRepository.findAll();


            Category burgers = cats.stream().filter(c -> c.getName().equals("Burgers")).findFirst().orElse(null);
            Category beers = cats.stream().filter(c -> c.getName().equals("Beers")).findFirst().orElse(null);
            Category wines = cats.stream().filter(c -> c.getName().equals("Wines")).findFirst().orElse(null);
            Category cocktails = cats.stream().filter(c -> c.getName().equals("Cocktails")).findFirst().orElse(null);
            Category sweet = cats.stream().filter(c -> c.getName().equals("Sweets")).findFirst().orElse(null);
            Category savory = cats.stream().filter(c -> c.getName().equals("Savory")).findFirst().orElse(null);
            Category pasta = cats.stream().filter(c -> c.getName().equals("Pasta")).findFirst().orElse(null);
            Category tacos = cats.stream().filter(c -> c.getName().equals("Tacos")).findFirst().orElse(null);

            Product p1 = Product.builder()
                    .foodVenue(v1)
                    .name("Classic Burger")
                    .description("A tasty beef burger")
                    .price(new BigDecimal("1500"))
                    .imageUrl("https://example.com/burger1.jpg")
                    .stock(20)
                    .category(burgers)
                    .tags(List.of(tags.getFirst())) // Ej: Spicy
                    .build();

            Product p2 = Product.builder()
                    .foodVenue(v1)
                    .name("Cheese Burger")
                    .description("Burger with cheddar cheese")
                    .price(new BigDecimal("1600"))
                    .imageUrl("https://example.com/burger2.jpg")
                    .stock(25)
                    .category(burgers)
                    .tags(List.of(tags.getFirst()))
                    .build();

            Product p3 = Product.builder()
                    .foodVenue(v1)
                    .name("IPA Patagonia")
                    .description("Craft beer with intense hops")
                    .price(new BigDecimal("1200"))
                    .imageUrl("https://example.com/ipa.jpg")
                    .stock(30)
                    .category(beers)
                    .tags(List.of())
                    .build();

            Product p4 = Product.builder()
                    .foodVenue(v1)
                    .name("Blonde Ale Andes")
                    .description("Smooth and refreshing ale")
                    .price(new BigDecimal("1100"))
                    .imageUrl("https://example.com/blonde.jpg")
                    .stock(30)
                    .category(beers)
                    .tags(List.of())
                    .build();

            Product p5 = Product.builder()
                    .foodVenue(v1)
                    .name("Don Valentín Lacrado Malbec")
                    .description("Classic Argentine red wine")
                    .price(new BigDecimal("2000"))
                    .imageUrl("https://example.com/donvalentin.jpg")
                    .stock(15)
                    .category(wines)
                    .tags(List.of())
                    .build();

            Product p6 = Product.builder()
                    .foodVenue(v1)
                    .name("Trumpeter Cabernet")
                    .description("Robust and full-bodied wine")
                    .price(new BigDecimal("2500"))
                    .imageUrl("https://example.com/trumpeter.jpg")
                    .stock(10)
                    .category(wines)
                    .tags(List.of())
                    .build();

            Product p7 = Product.builder()
                    .foodVenue(v1)
                    .name("Sex on the Beach")
                    .description("Fruity cocktail with peach and orange")
                    .price(new BigDecimal("1800"))
                    .imageUrl("https://example.com/sexonthebeach.jpg")
                    .stock(20)
                    .category(cocktails)
                    .tags(List.of())
                    .build();

            Product p8 = Product.builder()
                    .foodVenue(v1)
                    .name("Rosé Vermouth")
                    .description("Delicate herbal rosé aperitif")
                    .price(new BigDecimal("1700"))
                    .imageUrl("https://example.com/rosevermouth.jpg")
                    .stock(15)
                    .category(cocktails)
                    .tags(List.of())
                    .build();

            Product p9 = Product.builder()
                    .foodVenue(v1)
                    .name("Ice Cream Sundae")
                    .description("Vanilla and chocolate with toppings")
                    .price(new BigDecimal("900"))
                    .imageUrl("https://example.com/icecream.jpg")
                    .stock(50)
                    .category(sweet)
                    .tags(List.of())
                    .build();

            Product p10 = Product.builder()
                    .foodVenue(v1)
                    .name("Cheese and Quince")
                    .description("Classic 'Postre Vigilante'")
                    .price(new BigDecimal("950"))
                    .imageUrl("https://example.com/quince.jpg")
                    .stock(40)
                    .category(savory)
                    .tags(List.of())
                    .build();

            Product p11 = Product.builder()
                    .foodVenue(v2)
                    .name("Spaghetti Carbonara")
                    .description("Creamy pasta with bacon")
                    .price(new BigDecimal("1800"))
                    .imageUrl("https://example.com/pasta1.jpg")
                    .stock(15)
                    .category(pasta)
                    .tags(List.of(tags.get(1))) // Vegan for demo
                    .build();
            Product p12 = Product.builder()
                    .foodVenue(v3)
                    .name("Beef Taco")
                    .description("Spicy beef taco")
                    .price(new BigDecimal("800"))
                    .imageUrl("https://example.com/taco1.jpg")
                    .stock(30)
                    .category(tacos)
                    .tags(List.of(tags.get(0), tags.get(2)))
                    .build();
// Más pastas para v2
            Product p13 = Product.builder()
                    .foodVenue(v2)
                    .name("Fettuccine Alfredo")
                    .description("Fettuccine pasta in a rich and creamy Alfredo sauce")
                    .price(new BigDecimal("1900"))
                    .imageUrl("https://example.com/pasta2.jpg")
                    .stock(20)
                    .category(pasta)
                    .tags(List.of(tags.get(1))) // Ejemplo: Vegan
                    .build();

            Product p14 = Product.builder()
                    .foodVenue(v2)
                    .name("Lasagna Bolognese")
                    .description("Classic Italian lasagna with beef and cheese")
                    .price(new BigDecimal("2200"))
                    .imageUrl("https://example.com/pasta3.jpg")
                    .stock(12)
                    .category(pasta)
                    .tags(List.of(tags.get(2))) // Ejemplo: Spicy
                    .build();

            Product p15 = Product.builder()
                    .foodVenue(v2)
                    .name("Penne Arrabbiata")
                    .description("Penne pasta in a spicy tomato sauce")
                    .price(new BigDecimal("1700"))
                    .imageUrl("https://example.com/pasta4.jpg")
                    .stock(18)
                    .category(pasta)
                    .tags(List.of(tags.get(0), tags.get(2))) // Gluten-free + Spicy
                    .build();


// Más tacos para v3
            Product p16 = Product.builder()
                    .foodVenue(v3)
                    .name("Chicken Taco")
                    .description("Grilled chicken taco with fresh veggies")
                    .price(new BigDecimal("750"))
                    .imageUrl("https://example.com/taco2.jpg")
                    .stock(25)
                    .category(tacos)
                    .tags(List.of(tags.get(1))) // Vegan demo
                    .build();

            Product p17 = Product.builder()
                    .foodVenue(v3)
                    .name("Fish Taco")
                    .description("Crispy fried fish taco with tartar sauce")
                    .price(new BigDecimal("950"))
                    .imageUrl("https://example.com/taco3.jpg")
                    .stock(20)
                    .category(tacos)
                    .tags(List.of(tags.get(0))) // Gluten-free
                    .build();

            Product p18 = Product.builder()
                    .foodVenue(v3)
                    .name("Veggie Taco")
                    .description("Taco stuffed with grilled vegetables and avocado")
                    .price(new BigDecimal("700"))
                    .imageUrl("https://example.com/taco4.jpg")
                    .stock(22)
                    .category(tacos)
                    .tags(List.of(tags.get(1), tags.get(2))) // Vegan + Spicy
                    .build();

            productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15 , p16, p17, p18));


            // Root
            User rootUser = User.builder()
                    .name("Root")
                    .lastName("System")
                    .email("root@test.com")
                    .password(passwordEncoder.encode(password))
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .phone("1111111111")
                    .createdAt(LocalDateTime.now())
                    .address(new Address("Root St", "1", "Root City", "Root Province", "Root Country", "0000"))
                    .build();

            // Admin/Super Admin
            User adminUser = User.builder()
                    .name("Admin")
                    .lastName("System")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode(password))
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .phone("1111111111")
                    .createdAt(LocalDateTime.now())
                    .address(new Address("Admin St", "1", "Admin City", "Admin Province", "Admin Country", "0000"))
                    .build();

            Employment adminEmployee = Employment.builder()
                    .foodVenue(v1)
                    .user(adminUser)
                    .role(RoleType.ROLE_ADMIN)
                    .build();
            Employment adminEmployee2 = Employment.builder()
                    .foodVenue(v2)
                    .user(adminUser)
                    .role(RoleType.ROLE_ADMIN)
                    .build();
            Employment adminEmployee3 = Employment.builder()
                    .foodVenue(v3)
                    .user(adminUser)
                    .role(RoleType.ROLE_MANAGER)
                    .build();
            Employment rootV1 = Employment.builder()
                    .foodVenue(v1)
                    .user(rootUser)
                    .role(RoleType.ROLE_ROOT)
                    .build();
            Employment rootV2 = Employment.builder()
                    .foodVenue(v3)
                    .user(rootUser)
                    .role(RoleType.ROLE_ROOT)
                    .build();
            Employment rootV3 = Employment.builder()
                    .foodVenue(v2)
                    .user(rootUser)
                    .role(RoleType.ROLE_ROOT)
                    .build();

            User u1 = User.builder()
                    .email("user1@example.com")
                    .name("Leonardo")
                    .lastName("Juarez")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .createdAt(LocalDateTime.now())
                    .password(passwordEncoder.encode(password))
                    .phone("1234567890")
                    .build();
            User u2 = User.builder()
                    .email("cliente@cliente.com")
                    .name("David")
                    .lastName("Fernandez")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .createdAt(LocalDateTime.now())
                    .password(passwordEncoder.encode(password))
                    .phone("1234")
                    .build();
            User u3 = User.builder()
                    .email("user3@example.com")
                    .name("Diego")
                    .lastName("Alonso")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .createdAt(LocalDateTime.now())
                    .password(passwordEncoder.encode(password))
                    .phone("1234567890")
                    .build();


            User managerUser = User.builder()
                    .name("Manolo")
                    .lastName("Lamas")
                    .email("manager@test.com")
                    .password(passwordEncoder.encode(password))
                    .birthDate(LocalDate.of(1985, 5, 15))
                    .phone("2222222222")
                    .createdAt(LocalDateTime.now())
                    .address(new Address("Manager St", "2", "Restaurant City", "Restaurant Province", "Restaurant Country", "1111"))
                    .build();
            Employment employeeManager = Employment.builder()
                    .foodVenue(v1)
                    .user(managerUser)
                    .role(RoleType.ROLE_MANAGER)
                    .build();

            // Employee
            User employeeUser = User.builder()
                    .email("employee@test.com")
                    .password(passwordEncoder.encode(password))
                    .name("Diego")
                    .lastName("Torres")
                    .birthDate(LocalDate.of(1995, 8, 20))
                    .phone("3333333333")
                    .createdAt(LocalDateTime.now())
                    .address(new Address("Employee St", "3", "Work City", "Work Province", "Work Country", "2222"))
                    .build();

            Employment employee1 = Employment.builder()
                    .foodVenue(v1)
                    .user(employeeUser)
                    .role(RoleType.ROLE_STAFF)
                    .build();

            // Client
            User clientUser = User.builder()
                    .name("Client")
                    .lastName("Customer")
                    .email("client@test.com")
                    .password(passwordEncoder.encode(password))
                    .birthDate(LocalDate.of(2000, 12, 10))
                    .phone("4444444444")
                    .createdAt(LocalDateTime.now())
                    .address(new Address("Client St", "4", "Customer City", "Customer Province", "Customer Country", "3333"))
                    .build();
            Participant participant = Participant.builder()
                    .user(clientUser)
                    .nickname(clientUser.getName())
                    .role(RoleType.ROLE_CLIENT)
                    .build();


            // Generic user
            User genericUser = User.builder()
                    .id(UUID.fromString("00000000-0000-4437-96fc-da7e8f0e5a4a"))
                    .name("Guest")
                    .lastName("User")
                    .email("guest@guest.com")
                    .password(passwordEncoder.encode(password))
                    .birthDate(LocalDate.of(1992, 6, 15))
                    .phone("0000000000")
                    .createdAt(LocalDateTime.now())
                    .address(new Address("Guest", "0", "Guest City", "Guest Province", "Guest Country", "0000"))
                    .build();


            Participant genericParticipant = Participant.builder()
                    .id(UUID.fromString("11111111-0000-4437-96fc-da7e8f0e5a4a"))
                    .nickname("invitado")
                    .role(RoleType.ROLE_GUEST)
                    .build();

            List<User> users = List.of(rootUser, adminUser, managerUser, employeeUser, clientUser, genericUser, u1, u2, u3);
            List<Employment> employees = List.of(employee1, employeeManager, adminEmployee, adminEmployee2, adminEmployee3, rootV1, rootV2, rootV3);
            List<Participant> participants = List.of(participant, genericParticipant);

            userRepository.saveAll(users);
            employmentRepository.saveAll(employees);
            participantRepository.saveAll(participants);
            log.info("[DataInitializer] ======================== USERS ?=========================");
            users.forEach(u -> log.info("[User]  Email={} Password={}", u.getEmail(), password));
            log.info("[DataInitializer] ======================= EMPLOYEES =======================");
            employees.forEach(e -> log.info("[Employee]  Email={} con el rol={}", e.getUser().getEmail(), e.getRole()));

            // Participants
            Participant c1 = Participant.builder()
                    .nickname(u1.getName())
                    .user(u1)
                    .build();
            Participant c2 = Participant.builder()
                    .nickname("Frank")
                    .build();
            Participant c3 = Participant.builder()
                    .nickname(u2.getName())
                    .user(u2)
                    .build();
            participantRepository.saveAll(List.of(c1, c2, c3));


            // Tables
            Table t1 = Table.builder()
                    .id(UUID.fromString("141f3ffc-9f03-4242-a1c8-800bd2ea42b8"))
                    .number(1)
                    .capacity(4)
                    .status(TableStatus.AVAILABLE)
                    .foodVenue(v1).build();
            Table t2 = Table.builder()
                    .id(UUID.fromString("141f3ffc-9f03-4242-a1c8-800bd2e14098"))
                    .number(2)
                    .capacity(2)
                    .status(TableStatus.COMPLETE)
                    .foodVenue(v2)
                    .build();
            Table t3 = Table.builder()
                    .id(UUID.fromString("141f3ffc-9f03-4242-a1c8-800bd2e84556"))
                    .number(3)
                    .capacity(6)
                    .status(TableStatus.OCCUPIED)
                    .foodVenue(v3).build();
            Table t4 = Table.builder()
                    .id(UUID.fromString("141f3ffc-9f03-4242-a1c8-800bd2e84533"))
                    .number(3)
                    .capacity(6)
                    .status(TableStatus.OUT_OF_SERVICE)
                    .foodVenue(v3).build();
            List<Table> tables = List.of(t1, t2, t3, t4);
            tableRepository.saveAll(tables);

            log.info("[DataInitializer] =========================TABLES==========================");
            tables.forEach(t -> log.info("[Table]  Mesa con ID={} Estado={}", t.getId(), t.getStatus()));


            // TableSessions
            TableSession ts1 = TableSession.builder()
                    .table(t3)
                    .foodVenue(v3)
                    .sessionHost(c1)
                    .startTime(LocalDateTime.now())
                    .build();
            TableSession ts2 = TableSession.builder()
                    .table(t2)
                    .foodVenue(v2)
                    .sessionHost(c2)
                    .startTime(LocalDateTime.now())
                    .build();
            TableSession ts3 = TableSession.builder()
                    .table(t3)
                    .foodVenue(v3)
                    .sessionHost(c3)
                    .startTime(LocalDateTime.now())
                    .build();
            tableSessionRepository.saveAll(List.of(ts1, ts2, ts3));


            // Orders and OrderDetails + Payments
            Order o1 = Order.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000123000000"))
                    .participant(c1)
                    .orderNumber(1)
                    .foodVenue(v1)
                    .tableSession(ts2)
                    .specialRequirements("Estamos festejando un cumpleaños, pueden traer una velita?")
                    .status(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.ZERO)
                    .build();
            Order o2 = Order.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000124000000"))
                    .participant(c2)
                    .orderNumber(2)
                    .foodVenue(v1)
                    .tableSession(ts2)
                    .status(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.ZERO)
                    .build();
            Order o3 = Order.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000125000000"))
                    .participant(c3)
                    .orderNumber(456)
                    .foodVenue(v3)
                    .tableSession(ts3)
                    .status(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.ZERO)
                    .build();
            orderRepository.saveAll(List.of(o1, o2, o3));

            OrderDetail od1 = OrderDetail.builder()
                    .product(p1)
                    .quantity(2)
                    .price(p1.getPrice())
                    .build();
            OrderDetail od2 = OrderDetail.builder()
                    .product(p2)
                    .quantity(1)
                    .price(p2.getPrice())
                    .build();
            OrderDetail od3 = OrderDetail.builder()
                    .product(p3)
                    .quantity(3)
                    .price(p3.getPrice())
                    .build();
            orderDetailRepository.saveAll(List.of(od1, od2, od3));

            // Payment grouping all orders
            Payment pay1 = Payment.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-123123000000"))
                    .amount(BigDecimal.ZERO) // will adjust
                    .orders(List.of(o1, o2, o3))
                    .status(PaymentStatus.PENDING)
                    .paymentMethod(PaymentMethod.DEBIT_CARD)
                    .amount(BigDecimal.ZERO)
                    .build();
            BigDecimal total = od1.getPrice().multiply(BigDecimal.valueOf(od1.getQuantity()))
                    .add(od2.getPrice().multiply(BigDecimal.valueOf(od2.getQuantity())))
                    .add(od3.getPrice().multiply(BigDecimal.valueOf(od3.getQuantity())));
            pay1.setAmount(total);
            paymentRepository.save(pay1);


            log.debug("[DataInitializer] Initialized sample data.");
        }
    }
}

