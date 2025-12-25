package com.xianyu.order.context.order.infr.persistence.mapper;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xianyu.BaseMapperUnitTest;
import jakarta.annotation.Resource;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderQueryMapperUnitTest extends BaseMapperUnitTest {

    @Resource
    private OrderQueryMapper orderQueryMapper;

    @Test
    @DisplayName("测试集成mybatis")
    void should_integration_mybatis() {
        Integer ping = orderQueryMapper.ping();
        String currentSchema = orderQueryMapper.currentSchema();
        assertThat(ping).isEqualTo(1);
        assertThat(currentSchema).isNotBlank();

        PageHelper.startPage(1, 1);
        List<Integer> r = orderQueryMapper.select();
        PageInfo<Integer> pageInfo = new PageInfo<>(r);
        assertJSON(pageInfo);
    }
}