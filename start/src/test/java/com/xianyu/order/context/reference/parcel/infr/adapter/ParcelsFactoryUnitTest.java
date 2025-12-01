package com.xianyu.order.context.reference.parcel.infr.adapter;

import com.xianyu.base.BaseUnitTest;
import com.xianyu.order.context.reference.parcel.Parcel;
import com.xianyu.order.context.reference.parcel.ParcelStatus;
import com.xianyu.order.context.reference.parcel.ParcelsFactory;
import com.xianyu.order.context.reference.parcel.rpc.ParcelClient;
import com.xianyu.order.context.reference.parcel.rpc.rsp.ParcelDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ParcelsFactoryUnitTest extends BaseUnitTest {

    @Mock
    private ParcelClient parcelClient;

    @InjectMocks
    private ParcelsFactory parcelsFactory;

    @Test
    @DisplayName("stub ParcelClient 并验证 OrderParcels 迭代和查找")
    void should_iterate_and_find_parcels() {
        long orderId = 1L;

        Parcel parcel1 = Parcel.builder().parcelId(10L).status(ParcelStatus.PENDING).build();
        Parcel parcel2 = Parcel.builder().parcelId(11L).status(ParcelStatus.SHIPPED).build();

        ParcelDto dto1 = mock(ParcelDto.class);
        ParcelDto dto2 = mock(ParcelDto.class);
        when(dto1.toParcel()).thenReturn(parcel1);
        when(dto2.toParcel()).thenReturn(parcel2);

        when(parcelClient.countByOrderId(orderId)).thenReturn(2);
        when(parcelClient.listForPage(orderId, 0, 2)).thenReturn(java.util.List.of(dto1, dto2));
        when(parcelClient.findById(10L)).thenReturn(parcel1);
        when(parcelClient.findById(11L)).thenReturn(parcel2);

        var orderParcels = parcelsFactory.create(orderId);

        var size = orderParcels.findAll().size();
        assertThat(size).isEqualTo(2);

        var list = orderParcels.findAll().stream().toList();
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getParcelId()).isEqualTo(10L);
        assertThat(list.get(0).getStatus()).isEqualTo(ParcelStatus.PENDING);
        assertThat(list.get(1).getParcelId()).isEqualTo(11L);
        assertThat(list.get(1).getStatus()).isEqualTo(ParcelStatus.SHIPPED);

        assertThat(orderParcels.find(10L)).isPresent();
        assertThat(orderParcels.find(11L)).isPresent();
    }

}