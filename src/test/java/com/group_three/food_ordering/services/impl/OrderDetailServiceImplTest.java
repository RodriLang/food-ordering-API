package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.OrderDetailRequestDto;
import com.group_three.food_ordering.dto.response.OrderDetailResponseDto;
import com.group_three.food_ordering.exceptions.InsufficientStockException;
import com.group_three.food_ordering.mappers.OrderDetailMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.models.OrderDetail;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.repositories.OrderDetailRepository;
import com.group_three.food_ordering.repositories.ProductRepository;
import com.group_three.food_ordering.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceImplTest {

    @InjectMocks
    private OrderDetailServiceImpl orderDetailService;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private OrderDetailMapper orderDetailMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    private OrderDetailRequestDto orderDetailRequestDto;
    private OrderDetailResponseDto orderDetailResponseDto;
    private OrderDetail orderDetail;
    private Product product;


    @BeforeEach
    void setUp() {

        product = Product.builder()
                .name("test product")
                .description("test description")
                .price(BigDecimal.TEN)
                .category(Category.builder().name("test category").build())
                .stock(5)
                .build();

        orderDetailRequestDto = OrderDetailRequestDto.builder()
                .productId(product.getId())
                .specialInstructions("test instructions")
                .build();

        orderDetail = OrderDetail.builder()
                .price(BigDecimal.TEN.multiply(BigDecimal.valueOf(3L)))
                .quantity(3)
                .product(product)
                .specialInstructions("test instructions")
                .build();

        orderDetailResponseDto = OrderDetailResponseDto.builder()
                .id(1L)
                .productName(product.getName())
                .quantity(3)
                .unitPrice(BigDecimal.TEN)
                .subtotal(BigDecimal.TEN.multiply(BigDecimal.valueOf(3L)))
                .specialInstructions("test instructions")
                .category("test category")
                .build();
    }

    @Test
    void create() {
        when(orderDetailMapper.toEntity(orderDetailRequestDto)).thenReturn(orderDetail);
        when(orderDetailRepository.save(orderDetail)).thenReturn(orderDetail);
        when(orderDetailMapper.toDTO(orderDetail)).thenReturn(orderDetailResponseDto);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        OrderDetailResponseDto response = orderDetailService.create(orderDetailRequestDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test product", response.getProductName());
        assertEquals(3, response.getQuantity());
        assertEquals(BigDecimal.valueOf(10), response.getUnitPrice());
        assertEquals(BigDecimal.valueOf(30), response.getSubtotal());
        assertEquals("test category", response.getCategory());
        assertEquals("test instructions", response.getSpecialInstructions());
        verify(orderDetailRepository).save(orderDetail);
    }

    @Test
    void createInternal() {
        when(orderDetailMapper.toEntity(orderDetailRequestDto)).thenReturn(orderDetail);
        when(orderDetailRepository.save(orderDetail)).thenReturn(orderDetail);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        OrderDetail response = orderDetailService.createInternal(orderDetailRequestDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(product, response.getProduct());
        assertEquals(3, response.getQuantity());
        assertEquals(BigDecimal.valueOf(30), response.getPrice());
        assertEquals("test instructions", response.getSpecialInstructions());
        assertEquals(false, response.getDeleted());
        verify(orderDetailRepository).save(orderDetail);
    }

    @Test
    void createInternal_shouldThrowException_whenStockIsInsufficient() {
        // given
        product.setStock(0); // stock insuficiente

        OrderDetailRequestDto dto = new OrderDetailRequestDto();
        dto.setProductId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when + then
        assertThrows(InsufficientStockException.class,
                () -> orderDetailService.createInternal(dto));

        // verificar que NO se guardó el orderDetail
        verify(orderDetailRepository, never()).save(any(OrderDetail.class));
    }


    @Test
    void getAll() {
        when(orderDetailRepository.findAll()).thenReturn(List.of(orderDetail));
        when(orderDetailMapper.toDTO(any(OrderDetail.class))).thenReturn(orderDetailResponseDto);

        List<OrderDetailResponseDto> responseDtoList = orderDetailService.getAll();

        assertNotNull(responseDtoList);
        assertEquals(1, responseDtoList.size());
        assertEquals(1L, responseDtoList.getFirst().getId());
    }

    @Test
    void getOrderDetailById() {

        when(orderDetailRepository.findById(1L)).thenReturn(Optional.of(orderDetail));
        when(orderDetailMapper.toDTO(orderDetail)).thenReturn(orderDetailResponseDto);

        OrderDetailResponseDto response = orderDetailService.getOrderDetailById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void softDelete() {
        when(orderDetailRepository.findById(1L)).thenReturn(Optional.of(orderDetail));

        assertFalse(orderDetail.getDeleted());

        orderDetailService.softDelete(1L);

        assertEquals(1L, orderDetail.getId());
        assertTrue(orderDetail.getDeleted());
        verify(orderDetailRepository).save(orderDetail);
    }

    @Test
    void updateQuantity_shouldUpdateQuantityAndPrice_whenStockIsSufficient() {
        // given
        when(orderDetailRepository.findById(1L))
                .thenReturn(Optional.of(orderDetail));

        doNothing().when(productService).validateStock(eq(product), anyInt());

        when(orderDetailRepository.save(any(OrderDetail.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // precondiciones
        assertEquals(3, orderDetail.getQuantity());

        // when
        orderDetailService.updateQuantity(1L, 5);

        // then
        assertEquals(5, orderDetail.getQuantity());
        verify(orderDetailRepository).save(orderDetail);
    }

    @Test
    void updateQuantity_shouldUpdateQuantityAndPrice_whenStockIsInsufficient() {
        // given
        when(orderDetailRepository.findById(1L))
                .thenReturn(Optional.of(orderDetail));

        doThrow(new InsufficientStockException())
                .when(productService).validateStock(any(Product.class), anyInt());

        assertThrows(InsufficientStockException.class, () -> orderDetailService.updateQuantity(1L, 5));
    }

    @Test
    void updateQuantity_shouldIncreaseStock_whenQuantityDecreases() {

        product.setStock(10);
        orderDetail.setQuantity(5);

        when(orderDetailRepository.findById(1L))
                .thenReturn(Optional.of(orderDetail));
        when(orderDetailRepository.save(any(OrderDetail.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        orderDetailService.updateQuantity(1L, 3);

        assertEquals(3, orderDetail.getQuantity());
        assertEquals(12, product.getStock());
        verify(orderDetailRepository).save(orderDetail);
    }


    @Test
    void updateSpecialInstructions() {

        String instructions = "new instructions";
        when(orderDetailRepository.findById(1L))
                .thenReturn(Optional.of(orderDetail));
        when(orderDetailRepository.save(orderDetail))
                .thenReturn(orderDetail);
        when(orderDetailMapper.toDTO(any(OrderDetail.class)))
                .thenAnswer(invocation -> {
                    OrderDetail od = invocation.getArgument(0);
                    OrderDetailResponseDto dto = new OrderDetailResponseDto();
                    dto.setSpecialInstructions(od.getSpecialInstructions());
                    return dto;
                });

        assertNotEquals(instructions, orderDetailResponseDto.getSpecialInstructions());

        OrderDetailResponseDto result =
                orderDetailService.updateSpecialInstructions(1L, instructions);

        assertEquals(instructions, result.getSpecialInstructions());
    }
}