package com.bjhy.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ClassName ExceptionUtil.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月05日 11:43:18
 */
public class ExceptionUtil {
    /**
     * 获取异常的堆栈信息.
     *
     * @param throwable 异常对象
     * @return
     */
    public static String format(Throwable throwable) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        throwable.printStackTrace(new PrintWriter(buf, true));
        String expMeesage = buf.toString();
        try {
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Constants.LINE_SEPARATOR + expMeesage;
    }
}
