package com.warmisland.service;

import com.warmisland.entity.Product;
import com.warmisland.mapper.ProductImageMapper;
import com.warmisland.mapper.ProductMapper;
import com.warmisland.mapper.ProductDetailEntryMapper;
import com.warmisland.mapper.ProductVariantMapper;
import com.warmisland.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled("Mockito cannot spy ProductServiceImpl due to MyBatis-Plus base class limitation. Use @SpringBootTest integration tests instead.")
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductImageMapper productImageMapper;

    @Mock
    private ProductDetailEntryMapper productDetailEntryMapper;

    @Mock
    private ProductVariantMapper productVariantMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @Disabled("See class-level @Disabled")
    void testGetProductById_Found() {
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("暖屿咖啡");
        mockProduct.setPrice(new BigDecimal("38.00"));

        when(productMapper.selectById(1L)).thenReturn(mockProduct);

        Product result = productService.getProductById(1L);
        assertNotNull(result);
        assertEquals("暖屿咖啡", result.getName());
    }

    @Test
    @Disabled("See class-level @Disabled")
    void testGetProductById_NotFound() {
        when(productMapper.selectById(999L)).thenReturn(null);
        Product result = productService.getProductById(999L);
        assertNull(result);
    }
}
