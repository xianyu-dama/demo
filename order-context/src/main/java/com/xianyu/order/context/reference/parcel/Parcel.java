package com.xianyu.order.context.reference.parcel;

import com.xianyu.component.ddd.aggregation.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 包裹<br/>
 * Created on : 2025-11-11 09:43
 * @author xian_yu_da_ma
 */
@Getter
@Builder
@AllArgsConstructor
public class Parcel extends BaseEntity<Long> {

    /**
     * 包裹号
     */
    private long parcelId;

    /**
     * 包裹状态
     */
    private ParcelStatus status;

    @Override
    public Long id() {
        return parcelId;
    }
}
