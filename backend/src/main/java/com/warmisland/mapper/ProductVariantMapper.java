package com.warmisland.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warmisland.entity.ProductVariant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductVariantMapper extends BaseMapper<ProductVariant> {
}
