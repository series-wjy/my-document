package com.wjy.es.util;
 
 
import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import com.wjy.es.util.Constants.ES;
import com.wjy.es.util.Constants.SEPARATOR;
 
public class EsHighLevelFactory {
    private static int connectTimeOut = 1000;
    private static int socketTimeOut = 30000;
    private static int connectionRequestTimeOut = 500;
    private static int maxConnectNum = 100;
    private static int maxConnectPerRoute = 100;
    private static boolean uniqueConnectTimeConfig = true;
    private static boolean uniqueConnectNumConfig = true;
    private static RestClientBuilder builder;
    private static RestHighLevelClient client = null;
 
    public static RestHighLevelClient client() {
        if(client==null){
    	String[] nodes = ES.NODES.split(SEPARATOR.Str_);
    	HttpHost[] httpHosts = new HttpHost[nodes.length];
		for (int i = 0 ; i<nodes.length;i++) {
			String node =nodes[i];
			if (node.length() > 0) {
				String[] hostPort = node.split(":");
				try {
					httpHosts[i] = new HttpHost(hostPort[0], Integer.parseInt(hostPort[1]), ES.HIGH_LEVEL_SCHEMA);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		builder = RestClient.builder(httpHosts);
        
        if (uniqueConnectTimeConfig) {
            setConnectTimeOutConfig();
        }
        if (uniqueConnectNumConfig) {
            setMutiConnectConfig();
        }
        client = new RestHighLevelClient(builder);
        }
        return client;
    }
 
    /**
     * 异步httpclient的连接延时配置
     */
    public static void setConnectTimeOutConfig() {
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public Builder customizeRequestConfig(Builder requestConfigBuilder) {
                requestConfigBuilder.setConnectTimeout(connectTimeOut);
                requestConfigBuilder.setSocketTimeout(socketTimeOut);
                requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
                return requestConfigBuilder;
            }
        });
    }
 
 
    /**
     * 异步httpclient的连接数配置
     */
    public static void setMutiConnectConfig() {
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                httpClientBuilder.setMaxConnTotal(maxConnectNum);
                httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
                return httpClientBuilder;
            }
        });
    }
 
    /**
     * 关闭连接
     */
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
}
