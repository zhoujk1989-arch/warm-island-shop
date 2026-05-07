package com.warmisland.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.warmisland.dto.Result;
import com.warmisland.entity.Product;
import com.warmisland.entity.ProductCategory;
import com.warmisland.mapper.ProductCategoryMapper;
import com.warmisland.service.ProductService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ProductCategoryController {

    private final ProductCategoryMapper categoryMapper;
    private final ProductService productService;

    public ProductCategoryController(ProductCategoryMapper categoryMapper, ProductService productService) {
        this.categoryMapper = categoryMapper;
        this.productService = productService;
    }

    @GetMapping
    public Result<List<ProductCategory>> list(
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(status)) {
            wrapper.eq(ProductCategory::getStatus, status);
        }

        wrapper.orderByAsc(ProductCategory::getSortOrder).orderByAsc(ProductCategory::getId);
        return Result.success(categoryMapper.selectList(wrapper));
    }

    @PostMapping
    public Result<ProductCategory> create(@RequestBody ProductCategory category) {
        String name = normalizeName(category.getName());
        if (!StringUtils.hasText(name)) {
            return Result.error(400, "分类名称不能为空");
        }

        if (existsByName(name, null)) {
            return Result.error(400, "分类名称已存在");
        }

        category.setName(name);
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (!StringUtils.hasText(category.getStatus())) {
            category.setStatus("启用");
        }

        categoryMapper.insert(category);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<ProductCategory> update(@PathVariable Long id, @RequestBody ProductCategory category) {
        ProductCategory oldCategory = categoryMapper.selectById(id);
        if (oldCategory == null) {
            return Result.error(404, "分类不存在");
        }

        String name = normalizeName(category.getName());
        if (!StringUtils.hasText(name)) {
            return Result.error(400, "分类名称不能为空");
        }

        if (existsByName(name, id)) {
            return Result.error(400, "分类名称已存在");
        }

        category.setId(id);
        category.setName(name);
        if (category.getSortOrder() == null) {
            category.setSortOrder(oldCategory.getSortOrder());
        }
        if (!StringUtils.hasText(category.getStatus())) {
            category.setStatus(oldCategory.getStatus());
        }

        categoryMapper.updateById(category);

        if (!oldCategory.getName().equals(name)) {
            productService.update(new LambdaUpdateWrapper<Product>()
                    .eq(Product::getCategory, oldCategory.getName())
                    .set(Product::getCategory, name));
        }

        return Result.success(categoryMapper.selectById(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ProductCategory category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }

        long productCount = productService.count(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategory, category.getName()));
        if (productCount > 0) {
            return Result.error(400, "该分类下仍有商品，不能删除");
        }

        categoryMapper.deleteById(id);
        return Result.success();
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getName, name);

        if (excludeId != null) {
            wrapper.ne(ProductCategory::getId, excludeId);
        }

        return categoryMapper.selectCount(wrapper) > 0;
    }
}
