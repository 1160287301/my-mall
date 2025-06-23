package com.my.mall.security.component;

import com.my.mall.security.config.IgnoreUrlsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 动态安全过滤器，用于拦截HTTP请求并进行访问控制。
 * <p>
 * 该类继承自Spring Security的AbstractSecurityInterceptor，并实现了Servlet的Filter接口。
 * 它通过动态安全元数据源（DynamicSecurityMetadataSource）获取资源的访问控制配置，
 * 并使用动态访问决策管理器（DynamicAccessDecisionManager）来决定用户是否有权限访问某个资源。
 */
public class DynamicSecurityFilter extends AbstractSecurityInterceptor implements Filter {
    /**
     * 动态安全元数据源，用于提供资源的访问控制配置。
     */
    @Autowired
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;
    /**
     * 忽略的URL配置，用于定义不需要进行访问控制的URL。
     */
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    /**
     * 设置动态访问决策管理器。
     * <p>
     * 该方法通过调用父类的setAccessDecisionManager方法，将动态访问决策管理器设置到拦截器中。
     * </p>
     *
     * @param dynamicAccessDecisionManager 动态访问决策管理器
     */
    @Autowired
    public void setMyAccessDecisionManager(DynamicAccessDecisionManager dynamicAccessDecisionManager) {
        super.setAccessDecisionManager(dynamicAccessDecisionManager);
    }
    /**
     * 初始化过滤器。
     * <p>
     * 该方法在过滤器初始化时被调用，但在这个实现中没有具体逻辑。
     * </p>
     *
     * @param filterConfig 过滤器配置
     * @throws ServletException 如果初始化失败
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * 执行过滤操作。
     * <p>
     * 该方法的核心逻辑如下：
     * 1. 将ServletRequest和ServletResponse转换为HttpServletRequest和FilterInvocation。
     * 2. 检查请求方法是否为OPTIONS，如果是，则直接放行。
     * 3. 检查请求URL是否在忽略的URL列表中，如果是，则直接放行。
     * 4. 调用父类的beforeInvocation方法进行访问控制检查。
     * 5. 如果用户有权限访问资源，则继续执行过滤链。
     * 6. 在finally块中调用父类的afterInvocation方法进行清理工作。
     * </p>
     *
     * @param servletRequest 请求
     * @param servletResponse 响应
     * @param filterChain 过滤链
     * @throws IOException 如果发生I/O错误
     * @throws ServletException 如果发生Servlet错误
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        FilterInvocation fi = new FilterInvocation(servletRequest, servletResponse, filterChain);
        //OPTIONS请求直接放行
        if(request.getMethod().equals(HttpMethod.OPTIONS.toString())){
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
            return;
        }
        //白名单请求直接放行
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String path : ignoreUrlsConfig.getUrls()) {
            if(pathMatcher.match(path,request.getRequestURI())){
                fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
                return;
            }
        }
        //此处会调用AccessDecisionManager中的decide方法进行鉴权操作
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }
    /**
     * 销毁过滤器。
     * <p>
     * 该方法在过滤器销毁时被调用，但在这个实现中没有具体逻辑。
     * </p>
     */
    @Override
    public void destroy() {
    }
    /**
     * 获取安全对象的类。
     * <p>
     * 该方法返回安全对象的类，用于确定拦截器处理的对象类型。
     * </p>
     *
     * @return 安全对象的类
     */
    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }
    /**
     * 获取安全元数据源。
     * <p>
     * 该方法返回动态安全元数据源，用于提供资源的访问控制配置。
     * </p>
     *
     * @return 安全元数据源
     */
    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return dynamicSecurityMetadataSource;
    }

}
