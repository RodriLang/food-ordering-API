package com.group_three.food_ordering.configs;

import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.enums.*;
import com.group_three.food_ordering.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
 
    private final ICategoryRepository categoryRepository;
    private final ITagRepository tagRepository;
    private final IFoodVenueRepository foodVenueRepository;
    private final IProductRepository productRepository;
    private final IClientRepository clientRepository;
    private final ITableRepository tableRepository;
    private final ITableSessionRepository tableSessionRepository;
    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final IPaymentRepository paymentRepository;

    @Override
    public void run(String... args) throws Exception {
        // Categories
        if (categoryRepository.count() == 0) {
            Category drinks = Category.builder().name("Drinks").build();
            Category food = Category.builder().name("Food").build();
            Category desserts = Category.builder().name("Desserts").build();
            categoryRepository.saveAll(List.of(drinks, food, desserts));
        }

        // Tags
        if (tagRepository.count() == 0) {
            Tag spicy = Tag.builder().label("Spicy").build();
            Tag vegan = Tag.builder().label("Vegan").build();
            Tag glutenFree = Tag.builder().label("Gluten-Free").build();
            tagRepository.saveAll(List.of(spicy, vegan, glutenFree));
        }

        // FoodVenues
        if (foodVenueRepository.count() == 0) {
            FoodVenue v1 = FoodVenue.builder()
                    .name("Burger House")
                    .email("contact@burgerhouse.com")
                    .phone("1234567890")
                    .imageUrl("https://example.com/burger.jpg")
                    .address(new Address("Main St", "123", "CityA", "ProvinceA", "CountryA", "1000"))
                    .build();
            FoodVenue v2 = FoodVenue.builder()
                    .name("Pasta Palace")
                    .email("hello@pastapalace.com")
                    .phone("0987654321")
                    .imageUrl("https://example.com/pasta.jpg")
                    .address(new Address("Second St", "456", "CityB", "ProvinceB", "CountryB", "2000"))
                    .build();
            FoodVenue v3 = FoodVenue.builder()
                    .name("Taco Town")
                    .email("info@tacotown.com")
                    .phone("5555555555")
                    .imageUrl("https://example.com/taco.jpg")
                    .address(new Address("Third St", "789", "CityC", "ProvinceC", "CountryC", "3000"))
                    .build();
            foodVenueRepository.saveAll(List.of(v1, v2, v3));

            // Products
            List<Category> cats = categoryRepository.findAll();
            List<Tag> tags = tagRepository.findAll();
            Product p1 = Product.builder()
                    .foodVenue(v1)
                    .name("Classic Burger")
                    .description("A tasty beef burger")
                    .price(new BigDecimal("1500"))
                    .imageUrl("https://example.com/burger1.jpg")
                    .stock(20)
                    .category(cats.get(1)) // Food
                    .tags(List.of(tags.get(0))) // Spicy
                    .build();
            Product p2 = Product.builder()
                    .foodVenue(v2)
                    .name("Spaghetti Carbonara")
                    .description("Creamy pasta with bacon")
                    .price(new BigDecimal("1800"))
                    .imageUrl("https://example.com/pasta1.jpg")
                    .stock(15)
                    .category(cats.get(1))
                    .tags(List.of(tags.get(1))) // Vegan for demo
                    .build();
            Product p3 = Product.builder()
                    .foodVenue(v3)
                    .name("Beef Taco")
                    .description("Spicy beef taco")
                    .price(new BigDecimal("800"))
                    .imageUrl("https://example.com/taco1.jpg")
                    .stock(30)
                    .category(cats.get(0)) // Drinks for demo
                    .tags(List.of(tags.get(0), tags.get(2)))
                    .build();
            productRepository.saveAll(List.of(p1, p2, p3));

            // Clients
            Client c1 = Client.builder()
                    .nickname("Pablo")
                    .build();
            Client c2 = Client.builder()
                    .nickname("Frank")
                    .build();
            Client c3 = Client.builder()
                    .nickname("Daniel")
                    .build();
            clientRepository.saveAll(List.of(c1, c2, c3));

            // Tables
            Table t1 = Table.builder().number(1).capacity(4).foodVenue(v1).build();
            Table t2 = Table.builder().number(2).capacity(2).foodVenue(v2).build();
            Table t3 = Table.builder().number(3).capacity(6).foodVenue(v3).build();
            tableRepository.saveAll(List.of(t1, t2, t3));

            // TableSessions
            TableSession ts1 = TableSession.builder()
                    .table(t1)
                    .foodVenue(v1)
                    .hostClient(c1)
                    .startTime(LocalDateTime.now())
                    .build();
            TableSession ts2 = TableSession.builder()
                    .table(t2)
                    .foodVenue(v2)
                    .hostClient(c2)
                    .startTime(LocalDateTime.now())
                    .build();
            TableSession ts3 = TableSession.builder()
                    .table(t3)
                    .foodVenue(v3)
                    .hostClient(c3)
                    .startTime(LocalDateTime.now())
                    .build();
            tableSessionRepository.saveAll(List.of(ts1, ts2, ts3));

            // Orders and OrderDetails + Payments
            Order o1 = Order.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000123000000"))
                    .client(c1)
                    .orderNumber(1)
                    .foodVenue(v1)
                    .tableSession(ts1)
                    .specialRequirements("Estamos festejando un cumpleaños, pueden traer una velita?")
                    .status(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.ZERO)
                    .build();
            Order o2 = Order.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000124000000"))
                    .client(c2)
                    .orderNumber(2)
                    .foodVenue(v1)
                    .tableSession(ts2)
                    .status(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.ZERO)
                    .build();
            Order o3 = Order.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000125000000"))
                    .client(c3)
                    .orderNumber(456)
                    .foodVenue(v3)
                    .tableSession(ts3)
                    .status(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.ZERO)
                    .build();
            orderRepository.saveAll(List.of(o1, o2, o3));

            OrderDetail od1 = OrderDetail.builder()
                    .order(o1)
                    .product(p1)
                    .quantity(2)
                    .price(p1.getPrice())
                    .build();
            OrderDetail od2 = OrderDetail.builder()
                    .order(o2)
                    .product(p2)
                    .quantity(1)
                    .price(p2.getPrice())
                    .build();
            OrderDetail od3 = OrderDetail.builder()
                    .order(o3)
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

            System.out.println("✅ Initialized sample data.");
        }
    }
}

