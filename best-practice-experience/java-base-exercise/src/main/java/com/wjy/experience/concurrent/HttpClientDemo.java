package com.wjy.experience.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HttpClient 连接池的使用示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年10月23日 17:29:00
 */
@Slf4j
@RequestMapping("http-client")
@RestController
public class HttpClientDemo {


    private static CloseableHttpClient httpClient = null;
    static {
        //当然，也可以把CloseableHttpClient定义为Bean，然后在@PreDestroy标记的方法内close这个HttpClient
        httpClient = HttpClients.custom().setMaxConnPerRoute(1).setMaxConnTotal(1).evictIdleConnections(60, TimeUnit.SECONDS).build();
        // 注册关闭钩子或者通过 @PreDestroy 注解关闭 CloseableHttpClient
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                httpClient.close();
            } catch (IOException ignored) {
            }
        }));
    }

    /**
     * 复用 CloseableHttpClient
     * @return
     */
    @GetMapping("perfect")
    public String right() {
        try (CloseableHttpResponse response = httpClient.execute(new HttpGet("http://localhost:8080/http-client/test"))) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * 每次请求都创建 CloseableHttpClient 的方式是不正确的，CloseableHttpClient 内部维护了一个线程池
     * @return
     */
    @GetMapping("defective")
    public String wrong1() {
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .evictIdleConnections(60, TimeUnit.SECONDS).build();
        try (CloseableHttpResponse response = client.execute(new HttpGet("http://localhost:8081/test"))) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @GetMapping("test")
    public String test() {
        return "test";
    }
}
