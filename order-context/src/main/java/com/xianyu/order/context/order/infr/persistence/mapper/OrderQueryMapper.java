package com.xianyu.order.context.order.infr.persistence.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderQueryMapper {

    Integer ping();

    String currentSchema();

    List<Integer> select();
}