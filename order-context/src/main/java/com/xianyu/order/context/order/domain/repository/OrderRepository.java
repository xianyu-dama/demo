package com.xianyu.order.context.order.domain.repository;

import com.xianyu.component.ddd.repository.CacheRepository;
import com.xianyu.component.ddd.repository.CommonRepository;
import com.xianyu.component.ddd.repository.GetWithLockRepository;
import com.xianyu.order.context.order.domain.Order;

/**
 * <br/>
 * Created on : 2023-08-30 23:07
 * @author xian_yu_da_ma
 */
public interface OrderRepository extends CommonRepository<Order, Long>, CacheRepository<Order, Long>, GetWithLockRepository<Order, Long> {

}
