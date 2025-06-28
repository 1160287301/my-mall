package com.my.mall.dao;

import com.my.mall.model.PmsProductVertifyRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品审核日志管理自定义Dao
 * Created by my on 2018/4/27.
 */
public interface PmsProductVertifyRecordDao {
    /**
     * 批量创建
     */
    int insertList(@Param("list") List<PmsProductVertifyRecord> list);
}
