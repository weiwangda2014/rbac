/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.common.web;

import com.wandun.BigDecimalEditor;
import com.wandun.DateEditor;
import com.wandun.StringEditor;
import com.wandun.common.beanvalidator.BeanValidators;
import com.wandun.common.mapper.JsonMapper;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 控制器支持类
 *
 * @author 云南万盾科技有限公司
 * @version 2013-3-23
 */
public abstract class BaseController {

    /**
     * 错误消息
     */
    protected static final Message ERROR_MESSAGE = Message.error("失败");

    /**
     * 成功消息
     */
    protected static final Message SUCCESS_MESSAGE = Message.success("成功");

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 管理基础路径
     */
    @Value("${adminPath}")
    protected String adminPath;

    /**
     * 前端基础路径
     */
    @Value("${frontPath}")
    protected String frontPath;

    /**
     * 前端URL后缀
     */
    @Value("${urlSuffix}")
    protected String urlSuffix;

    /**
     * 验证Bean实例对象
     */
    @Autowired
    protected Validator validator;

    /**
     * 服务端参数有效性验证
     *
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
     */
    protected boolean beanValidator(Model model, Object object, Class<?>... groups) {
        try {
            BeanValidators.validateWithException(validator, object, groups);
        } catch (ConstraintViolationException ex) {
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            addMessage(model, list.toArray(new String[]{}));
            return false;
        }
        return true;
    }

    protected Message BeanValidators(Object object, BindingResult bindingResult) {
        try {
            BeanValidators.validateWithException(validator, object);
        } catch (ConstraintViolationException ex) {
            if (bindingResult.hasErrors()) {
                List<ObjectError> list = bindingResult.getAllErrors();
                StringBuilder errorMessage = new StringBuilder();
                for (ObjectError error : list) {
                    errorMessage.append(error.getDefaultMessage()).append("\n");
                }
                return Message.error(errorMessage.toString());
            }
        }
        return null;
    }

    /**
     * 添加Model消息
     *
     * @param model
     * @param messages
     */
    protected void addMessage(Model model, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        model.addAttribute("message", sb.toString());
    }

    /**
     * 添加Flash消息
     *
     * @param redirectAttributes
     * @param messages
     */
    protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        redirectAttributes.addFlashAttribute("message", sb.toString());
    }

    /**
     * 客户端返回JSON字符串
     *
     * @param response
     * @param object
     * @return
     */
    protected String renderString(HttpServletResponse response, Object object) {
        return renderString(response, JsonMapper.toJsonString(object));
    }

    /**
     * 客户端返回字符串
     *
     * @param response
     * @param string
     * @return
     */
    protected String renderString(HttpServletResponse response, String string) {
        try {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @ResponseBody
    @ExceptionHandler({Exception.class})
    public Message MethodArgumentNotValidHandler(Exception exception) {
        StringBuilder errorMessage = new StringBuilder();
        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) exception;
            BindingResult result = ex.getBindingResult();
            FieldError error = result.getFieldError();
            //String field = error.getField();
            String message = error.getDefaultMessage();
            errorMessage.append("<p><span class=\"text-danger\">" + message + "</span></p>");
        } else if (exception instanceof BindException) {
            BindException ex = (BindException) exception;
            List<FieldError> errors = ex.getFieldErrors();
            for (FieldError error : errors) {
                //String field = error.getField();
                String message = error.getDefaultMessage();
                errorMessage.append("<p><span class=\"text-danger\">" + message + "</span></p>");
            }
        } else if (exception instanceof ValidationException) {
            ValidationException ex = (ValidationException) exception;
            String message = ex.getMessage();
            errorMessage.append("<p><span class=\"text-danger\">" + message + "</span></p>");
        } else if (exception instanceof AuthenticationException) {
            AuthenticationException ex = (AuthenticationException) exception;
            String message = ex.getMessage();
            errorMessage.append("<p><span class=\"text-danger\">" + message + "</span></p>");
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) exception;
            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            for (ConstraintViolation v : violations) {
                String message = v.getMessageTemplate();
                errorMessage.append("<p><span class=\"text-danger\">" + message + "</span></p>");
            }

        } else {
            String message = exception.getMessage();
            errorMessage.append("<p><span class=\"text-danger\">" + message + "</span></p>");
        }

        logger.error(exception.getMessage(), exception);
        return Message.error(errorMessage.toString());
    }


    /**
     * 初始化数据绑定
     * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
     * 2. 将字段中Date类型转换为String类型
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, new DateEditor(true));
        binder.registerCustomEditor(String.class, "password", new StringEditor(true));
        binder.registerCustomEditor(BigDecimal.class, new BigDecimalEditor());
    }
}
