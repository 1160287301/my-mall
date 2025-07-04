package com.my.mall.service;

import com.my.mall.model.SmsFlashPromotion;

import java.util.List;

/**
 * 限时购活动管理Service
 * Created by my on 2018/11/16.
 */
public interface SmsFlashPromotionService {
    /**
     * 添加活动
     */
    int create(SmsFlashPromotion flashPromotion);

    /**
     * 修改指定活动
     */
    int update(Long id, SmsFlashPromotion flashPromotion);

    /**
     * 删除单个活动
     */
    int delete(Long id);

    /**
     * 修改上下线状态
     */
    int updateStatus(Long id, Integer status);

    /**
     * 获取活动详情
     */
    SmsFlashPromotion getItem(Long id);

    /**
     * 根据关键字分页查询活动
     */
    List<SmsFlashPromotion> list(String keyword, Integer pageSize, Integer pageNum);
}
