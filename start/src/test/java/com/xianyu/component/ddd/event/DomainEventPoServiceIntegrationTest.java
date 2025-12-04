package com.xianyu.component.ddd.event;

import com.xianyu.BaseIntegrationTest;
import com.xianyu.component.ddd.event.persistence.DomainEventPo;
import com.xianyu.component.ddd.event.persistence.DomainEventPoDraft;
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
        List<DomainEventPo> domainEventPos = Arrays.asList(
                DomainEventPoDraft.$.produce(draft -> {
                    draft.setTopic("test-topic");
                    draft.setTag("test-tag");
                    draft.setBizId("test-bizId-1");
                    draft.setKey("test-key-1");
                    draft.setContent("{}");
                    draft.setSent(false);
                    draft.setMsgId("test-msgId-1");
                }),
                DomainEventPoDraft.$.produce(draft -> {
                    draft.setTopic("test-topic");
                    draft.setTag("test-tag");
                    draft.setBizId("test-bizId-2");
                    draft.setKey("test-key-2");
                    draft.setContent("{}");
                    draft.setSent(false);
                    draft.setMsgId("test-msgId-2");
                })
        );

        // 调用saveBatch方法
        boolean result = domainEventPoService.saveBatch(domainEventPos);

        // 验证保存结果
        assertTrue(result, "保存应该成功");
    }

    @Test
    @DisplayName("测试根据bizId更新消息状态")
    void should_updateSent_by_bizId() {
        final var bizId = "test-bizId-1";
        List<DomainEventPo> domainEventPos = Arrays.asList(
                DomainEventPoDraft.$.produce(draft -> {
                    draft.setId(1L);
                    draft.setTopic("test-topic");
                    draft.setTag("test-tag");
                    draft.setBizId(bizId);
                    draft.setKey("test-key-1");
                    draft.setContent("{}");
                    draft.setSent(false);
                    draft.setMsgId("test-msgId-1");
                })
        );

        // 调用saveBatch方法
        domainEventPoService.saveBatch(domainEventPos);

        domainEventPoService.updateSent(bizId);

        assertTrue(domainEventPoService.findByBizId(bizId)
                .<Boolean>map(DomainEventPo::sent)
                .orElse(false));
    }

}
