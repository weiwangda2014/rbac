/**
 * Copyright &copy; 2015-2020 王玮 All rights reserved.
 */
package com.wandun.modules.sys.interceptor;

import com.wandun.common.service.BaseService;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 手机端视图拦截器
 *
 * @author 云南万盾科技有限公司
 * @version 2014-9-1
 */
public class MobileInterceptor implements HandlerInterceptor, BaseService {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
//		if (modelAndView != null){//手机端没有开发，默认打开PC端界面。如果你自己开发app端界面，请取消该注释。
//			// 如果是手机或平板访问的话，则跳转到手机视图页面。
//			if(UserAgentUtils.isMobileOrTablet(request) && !StringUtils.startsWithIgnoreCase(modelAndView.getViewName(), "redirect:")){
//				modelAndView.setViewName("mobile/" + modelAndView.getViewName());
//			}
//		}
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

    }

}
