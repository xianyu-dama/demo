package com.xianyu.order.context.order.domain.value;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

/**
 * <br/>
 * Created on : 2024-08-08 23:17
 *
 * @author xian_yu_da_ma
 */
@Slf4j
@Getter
@Jacksonized
@SuperBuilder(toBuilder = true)
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
public class Extension implements Serializable {

    /**
     * 运费险
     */
    private BigDecimal shipInsuranceFee;

    /**
     * 首单标识
     */
    private Boolean isFirstOrder;

    private String lockId;

}
