package com.bjhy.core.logging;

import com.bjhy.core.config.AgentConfig;
import com.bjhy.core.config.AgentConfigInitializer;
import com.bjhy.core.config.AgentPackagePath;
import com.bjhy.core.logging.api.IWriter;
import com.bjhy.core.logging.impl.FileWriter;
import com.bjhy.core.logging.impl.SystemOutWriter;
import com.bjhy.core.util.StringUtil;

/**
 * @ClassName WriterFactory.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月04日 14:43:51
 */
public class WriterFactory {
    /**
     * 获取日志输出对象.
     *
     * @return 日志输出对象
     */
    public static IWriter getWriter() {
        if (AgentConfig.Logging.TYPE == LoggerType.STDOUT || !AgentConfigInitializer.isInited()) {
            return SystemOutWriter.INSTANCE;
        } else {
            if (StringUtil.isEmpty(AgentConfig.Logging.PATH)) {
                AgentConfig.Logging.PATH = AgentPackagePath.getAgentDir().getAbsolutePath() + "/logs/";
            }
            return FileWriter.getInstance();
        }
    }
}
