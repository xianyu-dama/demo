package com.xianyu.order.context.reference.parcel.rpc;

import com.xianyu.order.context.reference.parcel.Parcel;
import com.xianyu.order.context.reference.parcel.rpc.rsp.ParcelDto;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 包裹OpenFeign服务客户端<br/>
 * Created on : 2025-11-18 20:28
 *
 * @author xian_yu_da_ma
 */
@Service
@RequiredArgsConstructor
public class ParcelClient {

    public List<ParcelDto> listForPage(long orderId, int pageIndex, int pageSize) {
        return Collections.emptyList();
    }

    public Parcel findById(long parcelId) {
        return null;
    }

    public int countByOrderId(long orderId) {
        return 0;
    }
}
