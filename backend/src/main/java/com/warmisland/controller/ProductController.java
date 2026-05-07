package com.warmisland.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.warmisland.dto.Result;
import com.warmisland.entity.Product;
import com.warmisland.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products - List all products (with optional category filter)
     */
    @GetMapping
    public Result<IPage<Product>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        IPage<Product> page = productService.pageProducts(pageNum, pageSize, category, keyword);
        return Result.success(page);
    }

    /**
     * GET /api/products/hot - Get hot products
     */
    @GetMapping("/hot")
    public Result<List<Product>> hot(
            @RequestParam(defaultValue = "4") int limit) {
        return Result.success(productService.getHotProducts(limit));
    }

    /**
     * GET /api/products/categories - Get all categories
     */
    @GetMapping("/categories")
    public Result<List<String>> categories() {
        return Result.success(productService.getCategories());
    }

    /**
     * GET /api/products/{id} - Get single product by ID
     */
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(product);
    }

    /**
     * POST /api/products - Create product (admin)
     */
    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        productService.save(product);
        return Result.success(product);
    }

    /**
     * PUT /api/products/{id} - Update product (admin)
     */
    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        if (!productService.updateById(product)) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(product);
    }

    /**
     * DELETE /api/products/{id} - Delete product (admin)
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!productService.removeById(id)) {
            return Result.error(404, "商品不存在");
        }
        return Result.success();
    }
}
