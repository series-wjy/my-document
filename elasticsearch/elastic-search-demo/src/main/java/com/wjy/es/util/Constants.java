package com.wjy.es.util;

/**
 * 
 * 此类描述的是：
 * 参数应该写入到配置文件
 * @author: Coder_Wang
 * @version: 2018年10月25日 下午4:39:38
 */
public class Constants {
	
	public interface  SEPARATOR {
		String Str_ = "_";
	}
	
	
	public interface ES{
//		String CLUSTER_NAME = "es_cluster";
		
		
//		String CLUSTER_NAME = "es";
//		String NODES = "172.16.86.101:9300";
		String NODES = "172.16.86.101:9300_172.16.86.101:9301_172.16.86.101:9302";

		String CLUSTER_NAME = "kkb-es";
//		String NODES = "192.168.10.204:9300_192.168.10.204:9301_192.168.10.204:9302";
		String HIGH_LEVEL_SCHEMA = "schema";
	}
}
