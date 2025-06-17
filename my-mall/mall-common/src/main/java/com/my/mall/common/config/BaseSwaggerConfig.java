package com.my.mall.common.config;

import com.my.mall.common.domain.SwaggerProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseSwaggerConfig {
    /**
     * 作用：创建并配置 Swagger 的 Docket 对象，它是 Swagger 的核心配置类，用于定义 API 文档的生成规则。
     * 关键点：
     * SwaggerProperties swaggerProperties = swaggerProperties();：调用抽象方法 swaggerProperties() 获取自定义的 Swagger 配置属性。
     * new Docket(DocumentationType.SWAGGER_2)：创建一个 Docket 对象，指定使用 Swagger 2 规范。
     * .apiInfo(apiInfo(swaggerProperties))：调用 apiInfo 方法，设置 API 文档的基本信息（如标题、描述、联系人等）。
     * .select()：开始选择需要生成文档的接口。
     * .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getApiBasePackage()))：指定扫描的包路径，只扫描指定包下的接口。
     * .paths(PathSelectors.any())：指定路径过滤规则，这里设置为匹配所有路径。
     * .build()：构建 Docket 对象。
     * if (swaggerProperties.isEnableSecurity())：如果配置中启用了安全机制（如认证），则调用 securitySchemes 和 securityContexts 方法，配置安全相关的设置。
     */
    @Bean
    public Docket createRestApi() {
        SwaggerProperties swaggerProperties = swaggerProperties();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo(swaggerProperties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getApiBasePackage()))
                .paths(PathSelectors.any())
                .build();
        if (swaggerProperties.isEnableSecurity()) {
            docket.securitySchemes(securitySchemes()).securityContexts(securityContexts());
        }
        return docket;
    }

    /**
     * 作用：创建并配置 ApiInfo 对象，用于定义 API 文档的基本信息。
     * 关键点：
     * ApiInfoBuilder：Swagger 提供的构建器，用于逐步设置 API 文档的元数据。
     * .title(swaggerProperties.getTitle())：设置文档标题。
     * .description(swaggerProperties.getDescription())：设置文档描述。
     * .contact(new Contact(...))：设置文档的联系人信息，包括姓名、URL 和邮箱。
     * .version(swaggerProperties.getVersion())：设置文档版本。
     * .build()：构建 ApiInfo 对象。
     */
    private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .contact(new Contact(swaggerProperties.getContactName(), swaggerProperties.getContactUrl(), swaggerProperties.getContactEmail()))
                .version(swaggerProperties.getVersion())
                .build();
    }

    /**
     * 作用：定义安全机制（如认证方式），这里配置了一个基于请求头的 Authorization 认证。
     * 关键点：
     * ApiKey：表示一个基于请求头的认证方式。
     * "Authorization"：认证的名称和请求头的键。
     * "header"：指定认证信息存储在请求头中。
     * 返回一个包含 ApiKey 的列表，表示支持的认证方式。
     */
    private List<SecurityScheme> securitySchemes() {
        //设置请求头信息
        List<SecurityScheme> result = new ArrayList<>();
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        result.add(apiKey);
        return result;
    }

    /**
     * 作用：定义需要应用安全机制的路径。
     * 关键点：
     * getContextByPath()：调用 getContextByPath 方法，传入一个正则表达式，指定需要认证的路径模式。
     * 返回一个包含 SecurityContext 的列表，表示哪些路径需要进行安全认证。
     */
    private List<SecurityContext> securityContexts() {
        //设置需要登录认证的路径
        List<SecurityContext> result = new ArrayList<>();
        result.add(getContextByPath("/*/.*"));
        return result;
    }

    /**
     * 作用：根据路径正则表达式创建 SecurityContext 对象。
     * 关键点：
     * SecurityContext.builder()：创建 SecurityContext 的构建器。
     * .securityReferences(defaultAuth())：指定使用 defaultAuth 方法定义的认证引用。
     * .operationSelector(oc -> oc.requestMappingPattern().matches(pathRegex))：通过正则表达式匹配路径，决定哪些操作需要应用安全机制。
     * .build()：构建 SecurityContext 对象。
     */
    private SecurityContext getContextByPath(String pathRegex) {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .operationSelector(oc -> oc.requestMappingPattern().matches(pathRegex))
                .build();
    }

    /**
     * 作用：定义默认的认证引用。
     * 关键点：
     * AuthorizationScope：定义授权范围，这里定义了一个全局范围，允许访问所有内容。
     * SecurityReference：表示一个安全引用，关联认证方式和授权范围。
     * 返回一个包含 SecurityReference 的列表，表示默认的认证引用。
     */
    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        result.add(new SecurityReference("Authorization", authorizationScopes));
        return result;
    }

    /**
     * 作用：生成一个 BeanPostProcessor，用于处理 Springfox 的请求映射问题。
     * 关键点：
     * BeanPostProcessor：Spring 提供的接口，用于在 Bean 初始化前后进行处理。
     * postProcessAfterInitialization：在 Bean 初始化后执行的方法。
     * customizeSpringfoxHandlerMappings：过滤并清理请求映射，避免某些问题（如重复映射）。
     * getHandlerMappings：通过反射获取 Springfox 的请求映射集合。
     */
    public BeanPostProcessor generateBeanPostProcessor(){
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }

    /**
     * 自定义Swagger配置
     */
    public abstract SwaggerProperties swaggerProperties();
}
