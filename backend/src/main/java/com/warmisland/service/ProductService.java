package com.warmisland.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warmisland.entity.Product;

public interface ProductService extends IService<Product> {

    /**
     * Paginated product list with optional category filter and search
     */
    IPage<Product> pageProducts(int pageNum, int pageSize, String category, String keyword);

    Product getProductById(Long id);

    Product createProduct(Product product);

    Product updateProduct(Long id, Product product);

    boolean deleteProduct(Long id);

    /**
     * Get hot products by sold count
     */
    java.util.List<Product> getHotProducts(int limit);

    /**
     * Get all categories
     */
    java.util.List<String> getCategories();
}
