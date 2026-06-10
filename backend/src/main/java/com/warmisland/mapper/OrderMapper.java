package com.warmisland.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warmisland.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
