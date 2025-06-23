package com.my.mall.security.component;

import cn.hutool.core.collection.CollUtil;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Iterator;

/**
 动态访问决策管理器，用于根据用户的身份信息和资源的访问控制配置来决定用户是否有权限访问某个资源。
 * <p>
 * 该类实现了Spring Security的AccessDecisionManager接口，通过遍历资源的访问控制配置（ConfigAttribute集合）和用户的身份信息（Authentication对象），
 * 判断用户是否拥有访问资源所需的权限。如果用户拥有所需的权限，则允许访问；否则，抛出AccessDeniedException。
 */
public class DynamicAccessDecisionManager implements AccessDecisionManager {

    /**
     * 根据用户的身份信息和资源的访问控制配置来决定用户是否有权限访问某个资源。
     * <p>
     * 该方法的核心逻辑如下：
     * 1. 检查资源是否配置了访问控制。如果未配置，则直接放行。
     * 2. 遍历资源的访问控制配置（ConfigAttribute集合），提取每个访问控制要求所需的权限。
     * 3. 遍历用户的身份信息（Authentication对象），检查用户是否拥有当前访问控制要求所需的权限。
     * 4. 如果用户拥有所需的权限，则允许访问；否则，抛出AccessDeniedException。
     * </p>
     *
     * @param authentication 用户的身份信息，包含用户的角色和权限等
     * @param object         被访问的资源对象，通常是一个FilterInvocation对象，包含请求的详细信息
     * @param configAttributes 资源的访问控制配置，每个ConfigAttribute对象代表一个访问控制要求
     * @throws AccessDeniedException 如果用户没有权限访问该资源
     * @throws InsufficientAuthenticationException 如果用户的身份信息不足
     */
    @Override
    public void decide(Authentication authentication, Object object,
                       Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        // 当接口未被配置资源时直接放行
        if (CollUtil.isEmpty(configAttributes)) {
            return;
        }
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute configAttribute = iterator.next();
            //将访问所需资源或用户拥有资源进行比对
            String needAuthority = configAttribute.getAttribute();
            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                if (needAuthority.trim().equals(grantedAuthority.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("抱歉，您没有访问权限");
    }

    /**
     * 判断当前访问决策管理器是否支持某种类型的访问控制配置。
     * <p>
     * 在这个实现中，返回true，表示支持所有类型的ConfigAttribute。
     * </p>
     *
     * @param configAttribute 访问控制配置
     * @return 如果支持，则返回true；否则返回false
     */
    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    /**
     * 判断当前访问决策管理器是否支持某种类型的资源对象。
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
