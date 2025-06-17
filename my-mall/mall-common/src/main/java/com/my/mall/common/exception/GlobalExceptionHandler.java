package com.my.mall.common.exception;

import cn.hutool.core.util.StrUtil;
import com.my.mall.common.api.CommonResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLSyntaxErrorException;

/**
 * 全局异常处理类
 * Created by macro on 2020/2/27.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 作用：处理自定义的 ApiException 异常。
     * 关键点：
     * @ExceptionHandler(value = ApiException.class)：指定该方法处理 ApiException 类型的异常。
     * @ResponseBody：将返回值直接写入 HTTP 响应体中。
     * CommonResult.failed(e.getErrorCode())：如果异常对象中包含错误码，则返回一个包含错误码的统一响应。
     * CommonResult.failed(e.getMessage())：如果没有错误码，则返回异常消息的统一响应。
     * CommonResult 是一个自定义的响应类，用于封装统一的响应格式。
     */
    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

    /**
     * 作用：处理 MethodArgumentNotValidException 异常，通常是由 Spring 的 @Valid 或 @Validated 注解触发的参数校验异常。
     * 关键点：
     * @ExceptionHandler(value = MethodArgumentNotValidException.class)：指定该方法处理 MethodArgumentNotValidException 类型的异常。
     * BindingResult bindingResult = e.getBindingResult()：获取校验结果。
     * bindingResult.hasErrors()：判断是否有校验错误。
     * FieldError fieldError = bindingResult.getFieldError()：获取第一个校验错误。
     * fieldError.getField() + fieldError.getDefaultMessage()：拼接字段名称和校验错误信息。
     * CommonResult.validateFailed(message)：返回一个包含校验错误信息的统一响应。
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }

    /**
     * 作用：处理 BindException 异常，通常是由 Spring 的数据绑定过程中触发的异常。
     * 关键点：
     * @ExceptionHandler(value = BindException.class)：指定该方法处理 BindException 类型的异常。
     * 逻辑与 handleValidException(MethodArgumentNotValidException e) 类似，也是从 BindingResult 中提取校验错误信息。
     * CommonResult.validateFailed(message)：返回一个包含校验错误信息的统一响应。
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public CommonResult handleValidException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }

    /**
     * 作用：处理 SQLSyntaxErrorException 异常，通常是由 SQL 语法错误或数据库权限问题触发的异常。
     * 关键点：
     * @ExceptionHandler(value = SQLSyntaxErrorException.class)：指定该方法处理 SQLSyntaxErrorException 类型的异常。
     * e.getMessage()：获取异常消息。
     * StrUtil.isNotEmpty(message) && message.contains("denied")：判断异常消息是否包含特定内容（如“denied”），用于判断是否是权限问题。
     * 如果是权限问题，返回自定义的提示信息：“演示环境暂无修改权限，如需修改数据可本地搭建后台服务！”。
     * CommonResult.failed(message)：返回一个包含异常信息的统一响应。
     */
    @ResponseBody
    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public CommonResult handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        String message = e.getMessage();
        if (StrUtil.isNotEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        return CommonResult.failed(message);
    }
}
