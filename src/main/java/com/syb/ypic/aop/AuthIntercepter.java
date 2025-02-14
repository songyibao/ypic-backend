package com.syb.ypic.aop;

import com.syb.ypic.annotation.AuthCheck;
import com.syb.ypic.exception.ErrorCode;
import com.syb.ypic.exception.ThrowUtils;
import com.syb.ypic.model.entity.User;
import com.syb.ypic.model.enums.UserRoleEnum;
import com.syb.ypic.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthIntercepter {
    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        if (authCheck.mustRole() == null) {
            // 不需要权限校验，放行
            return joinPoint.proceed();
        }
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);

        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        ThrowUtils.throwIf(userRoleEnum == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(!mustRole.equals(loginUser.getUserRole()), ErrorCode.NO_AUTH_ERROR);

        // 校验通过，放行
        return joinPoint.proceed();

    }
}
