package com.my.mall.security.component;

import cn.hutool.core.util.URLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 动态安全元数据源，用于提供资源的访问控制配置。
 * <p>
 * 该类实现了Spring Security的FilterInvocationSecurityMetadataSource接口，
 * 通过动态安全服务（DynamicSecurityService）加载资源的访问控制配置，并根据请求的URL匹配相应的访问控制要求。
 * </p>
 */
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    /**
     * 保存资源访问控制配置的映射表，键为资源路径，值为对应的访问控制配置。
     */
    private static Map<String, ConfigAttribute> configAttributeMap = null;
    /**
     * 动态安全服务，用于加载资源的访问控制配置。
     */
    @Autowired
    private DynamicSecurityService dynamicSecurityService;

    /**
     * 在Spring容器初始化后加载资源的访问控制配置。
     * <p>
     * 该方法通过调用动态安全服务的loadDataSource方法，将资源的访问控制配置加载到configAttributeMap中。
     * </p>
     */
    @PostConstruct
    public void loadDataSource() {
        configAttributeMap = dynamicSecurityService.loadDataSource();
    }

    /**
     * 清空资源的访问控制配置。
     * <p>
     * 该方法清空configAttributeMap，并将其设置为null，用于在需要时重新加载配置。
     * </p>
     */
    public void clearDataSource() {
        configAttributeMap.clear();
        configAttributeMap = null;
    }

    /**
     * 根据请求的URL获取对应的访问控制配置。
     * <p>
     * 该方法的核心逻辑如下：
     * 1. 如果configAttributeMap为空，则调用loadDataSource方法加载配置。
     * 2. 获取当前请求的URL路径。
     * 3. 使用AntPathMatcher匹配请求路径和配置中的路径模式。
     * 4. 如果找到匹配的路径模式，则将对应的访问控制配置添加到结果列表中。
     * 5. 如果未找到匹配的路径模式，则返回空集合，表示该请求路径未设置访问控制要求。
     * </p>
     *
     * @param o FilterInvocation对象，包含请求的详细信息
     * @return 请求路径对应的访问控制配置集合
     * @throws IllegalArgumentException 如果参数o不是FilterInvocation类型
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if (configAttributeMap == null) this.loadDataSource();
        List<ConfigAttribute> configAttributes = new ArrayList<>();
        //获取当前访问的路径
        String url = ((FilterInvocation) o).getRequestUrl();
        String path = URLUtil.getPath(url);
        PathMatcher pathMatcher = new AntPathMatcher();
        Iterator<String> iterator = configAttributeMap.keySet().iterator();
        //获取访问该路径所需资源
        while (iterator.hasNext()) {
            String pattern = iterator.next();
            if (pathMatcher.match(pattern, path)) {
                configAttributes.add(configAttributeMap.get(pattern));
            }
        }
        // 未设置操作请求权限，返回空集合
        return configAttributes;
    }

    /**
     * 获取所有资源的访问控制配置。
     * <p>
     * 在这个实现中，该方法返回null，表示不支持获取所有资源的访问控制配置。
     * </p>
     *
     * @return 所有资源的访问控制配置集合，或null
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    /**
     * 判断当前安全元数据源是否支持某种类型的资源对象。
     * <p>
     * 在这个实现中，返回true，表示支持所有类型的资源对象。
     * </p>
     *
     * @param aClass 资源对象的类型
     * @return 如果支持，则返回true；否则返回false
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}
