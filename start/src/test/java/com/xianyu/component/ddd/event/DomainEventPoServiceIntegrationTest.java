package com.xianyu.component.ddd.event;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.ddd.event.persistence.DomainEventPo;
import com.xianyu.component.ddd.event.persistence.service.DomainEventPoService;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <br/>
 * Created on : 2025-10-31 15:35
 * @author xian_yu_da_ma
 */
public class DomainEventPoServiceIntegrationTest extends BaseIntegrationTest {

    @Resource
    private DomainEventPoService domainEventPoService;

    @Test
    @DisplayName("测试saveBatch方法")
    void should_save_batch() {
        // 创建测试用的DomainEventPo实例
        DomainEventPo event1 = new DomainEventPo();
        event1.setTopic("test-topic");
        event1.setTag("test-tag");
        event1.setBizId("test-bizId-1");
        event1.setKey("test-key-1");
        event1.setContent("{}");
        event1.setSent(false);
        event1.setMsgId("test-msgId-1");
        
        DomainEventPo event2 = new DomainEventPo();
        event2.setTopic("test-topic");
        event2.setTag("test-tag");
        event2.setBizId("test-bizId-2");
        event2.setKey("test-key-2");
        event2.setContent("{}");
        event2.setSent(false);
        event2.setMsgId("test-msgId-2");
        
        List<DomainEventPo> domainEventPos = Arrays.asList(event1, event2);

        // 调用saveBatch方法
        boolean result = domainEventPoService.saveBatch(domainEventPos);

        // 验证保存结果
        assertTrue(result, "保存应该成功");
    }

    @Test
    @DisplayName("测试根据bizId更新消息状态")
    void should_updateSent_by_bizId() {
        final var bizId = "test-bizId-1";
        
        DomainEventPo event = new DomainEventPo();
        event.setTopic("test-topic");
        event.setTag("test-tag");
        event.setBizId(bizId);
        event.setKey("test-key-1");
        event.setContent("{}");
        event.setSent(false);
        event.setMsgId("test-msgId-1");
        
        List<DomainEventPo> domainEventPos = Arrays.asList(event);

        // 调用saveBatch方法
        boolean saveResult = domainEventPoService.saveBatch(domainEventPos);
        assertTrue(saveResult, "保存应该成功");

        // 更新发送状态
        domainEventPoService.updateSent(bizId);

        // 验证更新结果：查询数据库验证 sent 字段是否为 true
        DomainEventPo updated = domainEventPoService.lambdaQuery()
                .eq(DomainEventPo::getBizId, bizId)
                .one();
        assertTrue(updated != null && updated.getSent(), "sent应该为true");
    }

}
