package com.xianyu.component.ddd.persistent;

import java.time.LocalDateTime;
import org.babyfish.jimmer.sql.MappedSuperclass;

/**
 * <br/>
 * Created on : 2025-11-05 16:18
 * @author xian_yu_da_ma
 */
@MappedSuperclass
public interface BasePo {

    /**
     * 新增时间
     * @return
     */
    LocalDateTime addTime();

    /**
     * 更新时间
     * @return
     */
    LocalDateTime updateTime();

}
