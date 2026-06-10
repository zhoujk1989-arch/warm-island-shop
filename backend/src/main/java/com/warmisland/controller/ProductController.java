package com.warmisland.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.warmisland.dto.Result;
import com.warmisland.entity.Product;
import com.warmisland.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
        Product product = productService.getProductById(id);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(product);
    }

    /**
     * POST /api/products - Create product (admin)
     */
    @PostMapping
    public Result<Product> create(@Valid @RequestBody Product product) {
        return Result.success(productService.createProduct(product));
    }

    /**
     * PUT /api/products/{id} - Update product (admin)
     */
    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @Valid @RequestBody Product product) {
        Product updated = productService.updateProduct(id, product);
        if (updated == null) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(updated);
    }

    /**
     * DELETE /api/products/{id} - Delete product (admin)
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!productService.deleteProduct(id)) {
            return Result.error(404, "商品不存在");
        }
        return Result.success();
    }

    /**
     * POST /api/products/upload - Upload product image (admin)
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error(400, "上传文件为空");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error(400, "文件大小不能超过 10MB");
        }

        String originalFilename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');

        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
        }

        List<String> allowedExtensions = java.util.List.of(".jpg", ".jpeg", ".png", ".webp", ".gif");
        if (!allowedExtensions.contains(extension)) {
            return Result.error(400, "仅支持 jpg、png、webp、gif 图片");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(400, "文件内容类型不合法");
        }

        Path uploadDir = Path.of(System.getProperty("user.dir"), "uploads", "products");
        Files.createDirectories(uploadDir);

        String filename = UUID.randomUUID() + extension;
        Path target = uploadDir.resolve(filename);
        file.transferTo(target);

        return Result.success("/uploads/products/" + filename);
    }
}
