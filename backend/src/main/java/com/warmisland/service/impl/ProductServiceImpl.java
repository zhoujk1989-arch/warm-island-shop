package com.warmisland.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warmisland.entity.ProductImage;
import com.warmisland.entity.Product;
import com.warmisland.entity.ProductDetailEntry;
import com.warmisland.mapper.ProductDetailEntryMapper;
import com.warmisland.mapper.ProductImageMapper;
import com.warmisland.mapper.ProductMapper;
import com.warmisland.entity.ProductVariant;
import com.warmisland.mapper.ProductVariantMapper;
import com.warmisland.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductImageMapper productImageMapper;
    private final ProductDetailEntryMapper productDetailEntryMapper;
    private final ProductVariantMapper productVariantMapper;

    public ProductServiceImpl(
            ProductImageMapper productImageMapper,
            ProductDetailEntryMapper productDetailEntryMapper,
            ProductVariantMapper productVariantMapper) {
        this.productImageMapper = productImageMapper;
        this.productDetailEntryMapper = productDetailEntryMapper;
        this.productVariantMapper = productVariantMapper;
    }

    @Override
    public IPage<Product> pageProducts(int pageNum, int pageSize, String category, String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(category) && !category.equals("全部")) {
            wrapper.eq(Product::getCategory, category);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or()
                    .like(Product::getNameEn, keyword)
                    .or()
                    .like(Product::getDescription, keyword));
        }

        wrapper.orderByDesc(Product::getCreateTime);

        IPage<Product> result = page(new Page<>(pageNum, pageSize), wrapper);
        fillProductImages(result.getRecords());
        fillProductDetailEntries(result.getRecords(), true);
        fillProductVariants(result.getRecords(), true);
        return result;
    }

    @Override
    public Product getProductById(Long id) {
        Product product = getById(id);

        if (product != null) {
            fillProductImages(List.of(product));
            fillProductDetailEntries(List.of(product), false);
            fillProductVariants(List.of(product), false);
        }

        return product;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        applyPrimaryImage(product);
        save(product);
        replaceProductImages(product.getId(), collectImages(product));
        replaceProductDetailEntries(product.getId(), product.getDetailEntries());
        replaceProductVariants(product.getId(), product.getVariants());
        return getProductById(product.getId());
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        product.setId(id);
        applyPrimaryImage(product);

        if (!updateById(product)) {
            return null;
        }

        replaceProductImages(id, collectImages(product));
        replaceProductDetailEntries(id, product.getDetailEntries());
        replaceProductVariants(id, product.getVariants());
        return getProductById(id);
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        productImageMapper.delete(new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, id));
        productDetailEntryMapper.delete(new LambdaQueryWrapper<ProductDetailEntry>().eq(ProductDetailEntry::getProductId, id));
        productVariantMapper.delete(new LambdaQueryWrapper<ProductVariant>().eq(ProductVariant::getProductId, id));
        return removeById(id);
    }

    @Override
    public List<Product> getHotProducts(int limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Product::getSoldCount);
        wrapper.last("LIMIT " + limit);
        List<Product> products = list(wrapper);
        fillProductImages(products);
        fillProductDetailEntries(products, true);
        fillProductVariants(products, true);
        return products;
    }

    @Override
    public List<String> getCategories() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Product::getCategory);
        List<Product> products = list(wrapper);

        return products.stream()
                .map(Product::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private void fillProductImages(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        List<Long> productIds = products.stream()
                .map(Product::getId)
                .filter(Objects::nonNull)
                .toList();

        if (productIds.isEmpty()) {
            return;
        }

        List<ProductImage> images = productImageMapper.selectList(
                new LambdaQueryWrapper<ProductImage>()
                        .in(ProductImage::getProductId, productIds)
                        .orderByAsc(ProductImage::getProductId)
                        .orderByAsc(ProductImage::getSortOrder)
                        .orderByAsc(ProductImage::getId));

        Map<Long, List<String>> imageMap = images.stream()
                .collect(Collectors.groupingBy(
                        ProductImage::getProductId,
                        Collectors.mapping(ProductImage::getImageUrl, Collectors.toList())));

        for (Product product : products) {
            List<String> productImages = new ArrayList<>(imageMap.getOrDefault(product.getId(), Collections.emptyList()));

            if (productImages.isEmpty() && StringUtils.hasText(product.getImage())) {
                productImages.add(product.getImage());
            }

            product.setImages(productImages);

            if (!productImages.isEmpty()) {
                product.setImage(productImages.get(0));
            }
        }
    }

    private void fillProductDetailEntries(List<Product> products, boolean enabledOnly) {
        if (products == null || products.isEmpty()) {
            return;
        }

        List<Long> productIds = products.stream()
                .map(Product::getId)
                .filter(Objects::nonNull)
                .toList();

        if (productIds.isEmpty()) {
            return;
        }

        LambdaQueryWrapper<ProductDetailEntry> wrapper = new LambdaQueryWrapper<ProductDetailEntry>()
                .in(ProductDetailEntry::getProductId, productIds)
                .orderByAsc(ProductDetailEntry::getProductId)
                .orderByAsc(ProductDetailEntry::getSortOrder)
                .orderByAsc(ProductDetailEntry::getId);

        if (enabledOnly) {
            wrapper.eq(ProductDetailEntry::getStatus, "启用");
        }

        Map<Long, List<ProductDetailEntry>> entryMap = productDetailEntryMapper.selectList(wrapper)
                .stream()
                .collect(Collectors.groupingBy(ProductDetailEntry::getProductId));

        for (Product product : products) {
            product.setDetailEntries(new ArrayList<>(entryMap.getOrDefault(product.getId(), Collections.emptyList())));
        }
    }

    private void fillProductVariants(List<Product> products, boolean enabledOnly) {
        if (products == null || products.isEmpty()) {
            return;
        }

        List<Long> productIds = products.stream()
                .map(Product::getId)
                .filter(Objects::nonNull)
                .toList();

        if (productIds.isEmpty()) {
            return;
        }

        LambdaQueryWrapper<ProductVariant> wrapper = new LambdaQueryWrapper<ProductVariant>()
                .in(ProductVariant::getProductId, productIds)
                .orderByAsc(ProductVariant::getProductId)
                .orderByAsc(ProductVariant::getSortOrder)
                .orderByAsc(ProductVariant::getId);

        if (enabledOnly) {
            wrapper.eq(ProductVariant::getStatus, "启用");
        }

        Map<Long, List<ProductVariant>> variantMap = productVariantMapper.selectList(wrapper)
                .stream()
                .collect(Collectors.groupingBy(ProductVariant::getProductId));

        for (Product product : products) {
            product.setVariants(new ArrayList<>(variantMap.getOrDefault(product.getId(), Collections.emptyList())));
        }
    }

    private void applyPrimaryImage(Product product) {
        List<String> images = collectImages(product);

        if (!images.isEmpty()) {
            product.setImage(images.get(0));
        }
    }

    private List<String> collectImages(Product product) {
        List<String> images = new ArrayList<>();

        if (product.getImages() != null) {
            images.addAll(product.getImages());
        }

        if (product.getVariants() != null) {
            product.getVariants().stream()
                    .map(ProductVariant::getImageUrl)
                    .filter(StringUtils::hasText)
                    .forEach(images::add);
        }

        if (StringUtils.hasText(product.getImage())) {
            images.add(product.getImage());
        }

        return images.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();
    }

    private void replaceProductImages(Long productId, List<String> imageUrls) {
        productImageMapper.delete(new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, productId));

        for (int index = 0; index < imageUrls.size(); index++) {
            ProductImage image = new ProductImage();
            image.setProductId(productId);
            image.setImageUrl(imageUrls.get(index));
            image.setSortOrder(index);
            productImageMapper.insert(image);
        }
    }

    private void replaceProductDetailEntries(Long productId, List<ProductDetailEntry> entries) {
        productDetailEntryMapper.delete(new LambdaQueryWrapper<ProductDetailEntry>().eq(ProductDetailEntry::getProductId, productId));

        if (entries == null) {
            return;
        }

        int index = 0;
        for (ProductDetailEntry entry : entries) {
            if (!StringUtils.hasText(entry.getTitle()) && !StringUtils.hasText(entry.getContent()) && !StringUtils.hasText(entry.getImageUrl())) {
                continue;
            }

            entry.setId(null);
            entry.setProductId(productId);
            entry.setSectionType(StringUtils.hasText(entry.getSectionType()) ? entry.getSectionType() : "detail");
            entry.setLayoutType(StringUtils.hasText(entry.getLayoutType()) ? entry.getLayoutType() : "text");
            entry.setSortOrder(entry.getSortOrder() == null ? index : entry.getSortOrder());
            entry.setStatus(StringUtils.hasText(entry.getStatus()) ? entry.getStatus() : "启用");
            productDetailEntryMapper.insert(entry);
            index++;
        }
    }

    private void replaceProductVariants(Long productId, List<ProductVariant> variants) {
        productVariantMapper.delete(new LambdaQueryWrapper<ProductVariant>().eq(ProductVariant::getProductId, productId));

        if (variants == null) {
            return;
        }

        int index = 0;
        for (ProductVariant variant : variants) {
            if (!StringUtils.hasText(variant.getName())) {
                continue;
            }

            variant.setId(null);
            variant.setProductId(productId);
            variant.setSortOrder(variant.getSortOrder() == null ? index : variant.getSortOrder());
            variant.setStatus(StringUtils.hasText(variant.getStatus()) ? variant.getStatus() : "启用");
            productVariantMapper.insert(variant);
            index++;
        }
    }
}
