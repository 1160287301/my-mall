package com.my.mall.security.annotation;

import java.lang.annotation.*;

/**
 * 自定义缓存异常注解，有该注解的缓存方法会抛出异常
 * @Documented
 * 作用：表示该注解应该被包含在JavaDoc文档中。
 * 用途：当使用@CacheException注解的类或方法被生成JavaDoc文档时，@CacheException注解也会被包含在文档中，便于开发者了解该注解的使用情况。
 *
 * @Target(ElementType.METHOD)
 * 作用：指定注解可以被应用的目标元素类型。
 * ElementType.METHOD：表示该注解只能应用于方法上。
 * 用途：限制注解的使用范围，确保@CacheException只能被用于方法上，而不是类、字段或其他类型的元素。
 *
 * @Retention(RetentionPolicy.RUNTIME)
 * 作用：指定注解的保留策略，即注解在什么阶段有效。
 * RetentionPolicy.RUNTIME：表示注解在运行时仍然可用，可以通过反射获取注解的信息。
 * 用途：允许在运行时通过反射检查方法是否被@CacheException注解标记，从而在运行时执行相关的逻辑（例如，处理异常或缓存逻辑）。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheException {
}
