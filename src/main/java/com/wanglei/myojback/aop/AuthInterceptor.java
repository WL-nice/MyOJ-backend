package com.wanglei.myojback.aop;


import com.wanglei.myojback.annotation.AuthCheck;
import com.wanglei.myojback.commmon.ErrorCode;
import com.wanglei.myojback.constant.UserConstant;
import com.wanglei.myojback.exception.BusinessException;
import com.wanglei.myojback.model.entity.User;
import com.wanglei.myojback.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;


    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User user = userService.getLoginUser(request);

        // 必须有所有权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            Integer userRoleNum = user.getUserRole();
            String userRole = "";
            if (userRoleNum == UserConstant.ADMIN_ROLE) {
                userRole = "admin";
            }
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}