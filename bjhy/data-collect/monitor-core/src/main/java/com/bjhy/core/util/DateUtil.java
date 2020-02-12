package com.bjhy.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName DateUtil.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月02日 17:33:15
 */
public class DateUtil {

    /**
     * 日期格式:年-月-日 时:分:秒:毫秒.
     */
    private static final String DATEFORMAT_FULL = "yyyy-MM-dd HH:mm:ss:SSS";

    /**
     * 日期格式:年.月.日.时.分.秒.
     */
    private static final String DATEFORMAT_FILE = "yyyy.MM.dd.HH.mm.ss";

    /**
     * 当前的毫秒.
     *
     * @return
     */
    public static long getNowMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 日期转换为格式:年-月-日 时:分:秒:毫秒.
     *
     * @param date 被转换的日期
     * @return 格式化的日期字符串
     */
    public static String getFullFormat(Date date) {
        return new SimpleDateFormat(DATEFORMAT_FULL).format(date);
    }

    /**
     * 日期转换为格式:年.月.日.时.分.秒.
     *
     * @param date 被转换的日期
     * @return 格式化的日期字符串
     */
    public static String getFormatForFile(Date date) {
        return new SimpleDateFormat(DATEFORMAT_FILE).format(date);
    }
}
