package com.my.mall.service;

import com.my.mall.dto.OssCallbackResult;
import com.my.mall.dto.OssPolicyResult;

import javax.servlet.http.HttpServletRequest;

/**
 * Oss对象存储管理Service
 * Created by my on 2018/5/17.
 */
public interface OssService {
    /**
     * Oss上传策略生成
     */
    OssPolicyResult policy();
    /**
     * Oss上传成功回调
     */
    OssCallbackResult callback(HttpServletRequest request);
}
