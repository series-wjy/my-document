package com.example.demo.util;

import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;

public class ElasticSearchUtil {
    private static final RequestOptions COMMON_OPTIONS;
    private static final int DEFAULT_BUFFER_LIMIT = 100 * 1024 * 1024;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        //builder.addHeader("Authorization", "Bearer " + TOKEN);
        builder.setHttpAsyncResponseConsumerFactory(
                new HttpAsyncResponseConsumerFactory
                        .HeapBufferedResponseConsumerFactory(DEFAULT_BUFFER_LIMIT));
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 取得基础选项配置
     * @return
     */
    public static RequestOptions getCommonOptions() {
        return COMMON_OPTIONS;
    }
}
