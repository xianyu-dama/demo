package com.xianyu.inventory.context.inventory.infr.adapter;

import com.xianyu.inventory.context.inventory.domain.aggregation.StockInventory;
import com.xianyu.inventory.context.inventory.domain.repository.StockInventoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <br/>
 * Created on : 2024-04-12 12:31
 * @author xian_yu_da_ma
 */
@Service
@RequiredArgsConstructor
public class StockInventoryRepositoryAdapter implements StockInventoryRepository {

    @Override
    public Optional<StockInventory> get(Long id) {
        return Optional.of(StockInventory.builder().skuId(id).freeNum(100).build());
    }
} 