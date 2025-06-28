package com.my.mall.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis相关配置
 * Created by my on 2019/4/8.
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.my.mall.mapper","com.my.mall.dao"})
public class MyBatisConfig {
}
