package com.my.mall.service;

import com.my.mall.dto.OmsOrderReturnApplyResult;
import com.my.mall.dto.OmsReturnApplyQueryParam;
import com.my.mall.dto.OmsUpdateStatusParam;
import com.my.mall.model.OmsOrderReturnApply;

import java.util.List;

/**
 * 退货申请管理Service
 * Created by my on 2018/10/18.
 */
public interface OmsOrderReturnApplyService {
    /**
     * 分页查询申请
     */
    List<OmsOrderReturnApply> list(OmsReturnApplyQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 批量删除申请
     */
    int delete(List<Long> ids);

    /**
     * 修改指定申请状态
     */
    int updateStatus(Long id, OmsUpdateStatusParam statusParam);

    /**
     * 获取指定申请详情
     */
    OmsOrderReturnApplyResult getItem(Long id);
}
