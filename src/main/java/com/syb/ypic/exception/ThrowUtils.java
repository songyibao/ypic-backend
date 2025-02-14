package com.syb.ypic.exception;

/**
 * 异常处理工具类
 */
public class ThrowUtils {
    /**
     * 抛出业务异常
     * @param expression 表达式
     * @param runtimeException 异常
     */
    public static void throwIfTrue(boolean expression, RuntimeException runtimeException) {
        if (expression) {
            throw runtimeException;
        }
    }

    public static void throwIf(boolean expression, ErrorCode errorCode) {
        if (expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void throwIf(boolean expression, ErrorCode errorCode, String message) {
        if (expression) {
            throw new BusinessException(errorCode, message);
        }
    }
}
