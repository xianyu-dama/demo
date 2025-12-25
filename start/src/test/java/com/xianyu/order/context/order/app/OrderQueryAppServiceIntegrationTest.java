package com.xianyu.order.context.order.app;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.order.context.order.app.service.OrderQueryAppService;
import com.xianyu.order.context.sdk.order.dto.req.QueryOrderDto;
import jakarta.annotation.Resource;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

/**
 * <br/>
 * Created on : 2023-09-23 08:21
 * @author xian_yu_da_ma
 */
class OrderQueryAppServiceIntegrationTest extends BaseIntegrationTest {

    @Resource
    private OrderQueryAppService orderQueryAppService;

    @Test
    @DisplayName("列表测试")
    @Sql("/sql/OrderQueryAppServiceIntegrationTest/should_list_for_page.sql")
    void should_list_for_page() {
        QueryOrderDto query = new QueryOrderDto(List.of(), "", 1, 10);
        assertJSON(orderQueryAppService.listForPage(query));
    }

    @Test
    @DisplayName("详情测试")
    @Sql("/sql/OrderQueryAppServiceIntegrationTest/test_order_item.sql")
    void test_order_item() {
        assertJSON(orderQueryAppService.detail(1L));
    }

}
