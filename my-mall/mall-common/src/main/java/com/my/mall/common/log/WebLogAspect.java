package com.my.mall.common.log;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.my.mall.common.util.RequestUtil;
import io.swagger.annotations.ApiOperation;
import net.logstash.logback.marker.Markers;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.my.mall.common.domain.WebLog;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Aspect：表示这是一个切面类，用于定义切点和通知（Advice）。
 * @Component：将这个类注册为Spring容器中的一个Bean，这样Spring会自动扫描并管理这个类。
 * @Order(1)：设置该切面的优先级，数字越小优先级越高。这里设置为1，表示这个切面会在其他优先级更高的切面之后执行。
 */
@Aspect
@Component
@Order(1)
public class WebLogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogAspect.class);

    /**
     * @Pointcut：定义一个切点，指定哪些方法会被拦截。
     * execution(public * com.my.mall.controller. * . * ( ..))：匹配com.my.mall.controller包下所有类的所有公共方法。
     * execution(public * com.my.mall.*.controller.*.*(..))：匹配com.my.mall包下所有子包中的controller包的所有类的所有公共方法。
     * (..)：表示匹配任意数量和类型的参数。
     */
    @Pointcut("execution(public * com.my.mall.controller.*.*(..))||execution(public * com.my.mall.*.controller.*.*(..))")
    public void webLog() {
    }

    /**
     * @Before：前置通知，在目标方法执行之前执行。
     * JoinPoint joinPoint：提供对当前连接点的信息，如方法名、参数等。
     * 这个方法目前是空的，可以在这里添加一些在方法执行前需要执行的逻辑，比如记录方法开始执行的时间等。
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
    }

    /**
     * @AfterReturning：后置通知，在目标方法正常返回后执行。
     * returning = "ret"：表示将目标方法的返回值绑定到ret参数上。
     * 这个方法目前是空的，可以在这里添加一些在方法返回后需要执行的逻辑，比如记录返回值等。
     */
    @AfterReturning(value = "webLog()", returning = "ret")
    public void doAfterReturning(Object ret) throws Throwable {
    }

    /**
     * @Around：环绕通知，可以完全控制目标方法的执行。
     * ProceedingJoinPoint joinPoint：提供对当前连接点的信息，并允许执行目标方法。
     * joinPoint.proceed()：执行目标方法。
     * WebLog webLog：自定义的日志实体类，用于存储请求的详细信息。
     * LOGGER.info：记录日志信息。使用Markers.appendEntries可以将日志信息以结构化的方式记录，便于后续的查询和分析。
     * 7. 获取请求参数
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        //获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //记录请求信息(通过Logstash传入Elasticsearch)
        WebLog webLog = new WebLog();
        Object result = joinPoint.proceed();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(ApiOperation.class)) {
            ApiOperation log = method.getAnnotation(ApiOperation.class);
            webLog.setDescription(log.value());
        }
        long endTime = System.currentTimeMillis();
        String urlStr = request.getRequestURL().toString();
        webLog.setBasePath(StrUtil.removeSuffix(urlStr, URLUtil.url(urlStr).getPath()));
        webLog.setUsername(request.getRemoteUser());
        webLog.setIp(RequestUtil.getRequestIp(request));
        webLog.setMethod(request.getMethod());
        webLog.setParameter(getParameter(method, joinPoint.getArgs()));
        webLog.setResult(result);
        webLog.setSpendTime((int) (endTime - startTime));
        webLog.setStartTime(startTime);
        webLog.setUri(request.getRequestURI());
        webLog.setUrl(request.getRequestURL().toString());
        Map<String,Object> logMap = new HashMap<>();
        logMap.put("url",webLog.getUrl());
        logMap.put("method",webLog.getMethod());
        logMap.put("parameter",webLog.getParameter());
        logMap.put("spendTime",webLog.getSpendTime());
        logMap.put("description",webLog.getDescription());
//        LOGGER.info("{}", JSONUtil.parse(webLog));
        LOGGER.info(Markers.appendEntries(logMap), JSONUtil.parse(webLog).toString());
        return result;
    }

    /**
     * 根据方法和传入的参数获取请求参数
     * Method method：目标方法。
     * Object[] args：目标方法的参数。
     * 遍历方法的参数，检查是否有@RequestBody或@RequestParam注解。
     * 如果有@RequestBody注解，直接将参数值添加到argList中。
     * 如果有@RequestParam注解，将参数名和参数值以Map的形式添加到argList中。
     * 最后根据argList的大小，返回单个参数或参数列表。
     */
    private Object getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argList.add(args[i]);
            }
            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                Map<String, Object> map = new HashMap<>();
                String key = parameters[i].getName();
                if (!StrUtil.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                if(args[i]!=null){
                    map.put(key, args[i]);
                    argList.add(map);
                }
            }
        }
        if (argList.size() == 0) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }
}
