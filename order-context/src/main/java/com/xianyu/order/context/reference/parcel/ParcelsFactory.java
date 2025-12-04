package com.xianyu.order.context.reference.parcel;

import com.xianyu.component.ddd.composition.Entities;
import com.xianyu.order.context.reference.parcel.rpc.ParcelClient;
import com.xianyu.order.context.reference.parcel.rpc.rsp.ParcelDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParcelsFactory {

    private final ParcelClient parcelClient;

    public Parcels create(@NonNull Long orderId) {
        return new ParcelsImpl(orderId, parcelClient);
    }

    @AllArgsConstructor
    private static class ParcelsImpl extends Entities<Long, Parcel> implements Parcels {

        @NonNull
        private Long orderId;

        @NonNull
        private ParcelClient parcelClient;

        /**
         * 按需加载
         * @param pageIndex
         * @param pageSize
         * @return
         */
        @Override
        protected List<Parcel> findByPage(int pageIndex, int pageSize) {
            return parcelClient.listForPage(orderId, pageIndex, pageSize).stream().map(ParcelDto::toParcel).toList();
        }

        @Override
        protected Parcel findById(Long aLong) {
            return parcelClient.findById(aLong);
        }

        @Override
        public int size() {
            return parcelClient.countByOrderId(orderId);
        }
    }
}