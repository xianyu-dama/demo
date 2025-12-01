package com.xianyu.component.ddd.event.persistence.service;

import com.xianyu.component.ddd.event.persistence.DomainEventPo;
import com.xianyu.component.ddd.event.persistence.Tables;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;

/**
 * <br/>
 * Created on : 2025-10-07 16:09
 * @author xian_yu_da_ma
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DomainEventPoService {

    private final JSqlClient jSqlClient;

    public boolean saveBatch(List<DomainEventPo> domainEventPos) {
        return jSqlClient.saveEntitiesCommand(domainEventPos)
                .setMode(SaveMode.INSERT_ONLY)
                .execute()
                .getTotalAffectedRowCount() > 0;
    }


    public void updateSent(String bizId) {
        jSqlClient.createUpdate(Tables.DOMAIN_EVENT_PO_TABLE)
                .set(Tables.DOMAIN_EVENT_PO_TABLE.sent(), true)
                .where(Tables.DOMAIN_EVENT_PO_TABLE.bizId().eq(bizId))
                .execute();
    }

    public Optional<DomainEventPo> findByBizId(String bizId) {
        return jSqlClient.createQuery(Tables.DOMAIN_EVENT_PO_TABLE)
                .where(Tables.DOMAIN_EVENT_PO_TABLE.bizId().eq(bizId))
                .select(Tables.DOMAIN_EVENT_PO_TABLE)
                .execute()
                .stream()
                .findFirst();
    }

    public List<DomainEventPo> list() {
        return jSqlClient.createQuery(Tables.DOMAIN_EVENT_PO_TABLE)
                .select(Tables.DOMAIN_EVENT_PO_TABLE)
                .execute();
    }
}
