package com.my.mall.dao;

import com.my.mall.dto.SmsCouponParam;
import org.apache.ibatis.annotations.Param;

/**
 * 优惠券管理自定义Dao
 * Created by my on 2018/8/29.
 */
public interface SmsCouponDao {
    /**
     * 获取优惠券详情包括绑定关系
     */
    SmsCouponParam getItem(@Param("id") Long id);
}
