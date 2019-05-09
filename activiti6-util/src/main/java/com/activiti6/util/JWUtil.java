package com.activiti6.util;

public final class JWUtil {
	/**
	 * 获取行数
	 * @return
	 */
    public static int getLineNumber() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        int line = e.getLineNumber();
        return line;
    }
}
