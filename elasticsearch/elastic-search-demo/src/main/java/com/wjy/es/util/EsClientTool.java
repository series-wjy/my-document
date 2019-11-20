package com.wjy.es.util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wjy.es.entity.Doc;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.alibaba.fastjson.JSONObject;
import com.wjy.es.util.Constants.ES;
import com.wjy.es.util.Constants.SEPARATOR;

public class EsClientTool {
	private static TransportClient client = null;

	public static TransportClient getClient() {
		if (client != null) {
			return client;
		}
		try {
			/**
			 * 在此处通过put的方式给es集群添加属性
			 */
			Settings settings = Settings.builder()
					// 设置集群名称
					.put("cluster.name", ES.CLUSTER_NAME)
					// 自动嗅探集群
					.put("client.transport.sniff", true).build();
			// 创建client
			client = new PreBuiltTransportClient(settings);
			String[] nodes = ES.NODES.split(SEPARATOR.Str_);
			for (String node : nodes) {
				if (node.length() > 0) {
					String[] hostPort = node.split(":");
					try {
						client.addTransportAddress(new TransportAddress(InetAddress.getByName(hostPort[0]),
								// 此处注意浏览器暴露的9200，客户端（client）暴露的9300
								Integer.parseInt(hostPort[1])));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return client;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取索引管理的IndicesAdminClient
	 */
	public static IndicesAdminClient getAdminClient() {
		return getClient().admin().indices();
	}

	/**
	 * 判定索引是否存在
	 * 
	 * @param indexName
	 * @return
	 */
	public static boolean isExists(String indexName) {
		IndicesExistsResponse response = getAdminClient().prepareExists(indexName).get();
		return response.isExists() ? true : false;
	}

	/**
	 * 创建索引
	 * 
	 * @param indexName索引名
	 * @return
	 */
	public static boolean createIndex(String indexName) {
		CreateIndexResponse createIndexResponse = getAdminClient().prepareCreate(indexName.toLowerCase()).get();
		return createIndexResponse.isAcknowledged() ? true : false;
	}

	/**
	 * 创建索引
	 * 
	 * @param indexName
	 *            索引名
	 * @param shards
	 *            分片数
	 * @param replicas
	 *            副本数
	 * @return
	 */
	public static boolean createIndex(String indexName, int shards, int replicas) {
		Settings settings = Settings.builder().put("index.number_of_shards", shards)
				.put("index.number_of_replicas", replicas).build();
		CreateIndexResponse createIndexResponse = getAdminClient().prepareCreate(indexName.toLowerCase())
				.setSettings(settings).execute().actionGet();
		return createIndexResponse.isAcknowledged() ? true : false;
	}

	/**
	 * 位索引indexName设置mapping
	 * 
	 * @param indexName
	 * @param typeName
	 * @param mapping
	 */
	public static void setMapping(String indexName, String typeName, String mapping) {
		getAdminClient().preparePutMapping(indexName).setType(typeName).setSource(mapping, XContentType.JSON).get();
	}

	/**
	 * 删除索引
	 * 
	 * @param indexName
	 * @return
	 */
	public static boolean deleteIndex(String indexName) {
		ActionResponse deleteResponse = getAdminClient().prepareDelete(indexName.toLowerCase()).execute()
				.actionGet();
		return deleteResponse.isAcknowledged() ? true : false;
	}

	/**
	 * 插入文档 详细数据指定type id
	 * 
	 * @param index
	 * @param type
	 * @param object
	 * @return
	 */
	public static String addDoc(String index, String type, String id, JSONObject object) {
		try {
			IndexResponse response = getClient().prepareIndex(index, type, id)
					.setSource(object.toJSONString(), XContentType.JSON).get();
			return response.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 此方法描述的是： 批量添加文档
	 */
	public static boolean addDocs(List<Doc> docList) {
		TransportClient client = getClient();
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		for (int i = 0; i < docList.size(); i++) {
			Doc doc = docList.get(i);
			bulkRequest.add(client.prepareIndex(doc.getIndex(), doc.getType(), doc.getId()).setSource(doc.getBuilder()));
		}
		BulkResponse bulkResponse = bulkRequest.get();
		// 这里返回的是否有错误，所以返回的是非值
		return !bulkResponse.hasFailures();
	}

	private static BulkProcessor bulkProcessor = null;

	/**
	 * 批处理组件
	 * Using Bulk Processor
	 */
	public static BulkProcessor getBulkProcessor() {
		if (bulkProcessor == null) {

			bulkProcessor = BulkProcessor.builder(getClient(), new BulkProcessor.Listener() {
				@Override
				public void beforeBulk(long executionId, BulkRequest request) {
					System.out.println("请求数:" + request.numberOfActions());
				}

				@Override
				public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
					if (!response.hasFailures()) {
						System.out.println("执行成功！");
					} else {
						System.out.println("执行失败！");
						System.out.println(response.buildFailureMessage());
					}
				}

				@Override
				public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
					// 设置bulk批处理的异常处理工作
					System.out.println(failure);
				}
			}).setBulkActions(1000)// 设置提交批处理操作的请求阀值数
					.setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))// 设置提交批处理操作的请求大小阀值
					.setFlushInterval(TimeValue.timeValueSeconds(5))// 设置刷新索引时间间隔
					.setConcurrentRequests(1)// 设置并发处理线程个数
					// 设置回滚策略，等待时间100ms,retry次数为3次
					.setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)).build();
		}
		return bulkProcessor;
	}
	/**
	 * 批处理组件插入
	 */
	public static void addDocsBukProcessor(List<Doc> docList) {
		BulkProcessor bulkProcessor = getBulkProcessor();
		for (int i = 0; i < docList.size(); i++) {
			Doc doc = docList.get(i);
			bulkProcessor.add(new IndexRequest(doc.getIndex(), doc.getType(), doc.getId()).source(doc.getBuilder()));
		}
		// 刷新所有请求
		bulkProcessor.flush();
		// 关闭bulkProcessor
		bulkProcessor.close();
		// 刷新索引
		getClient().admin().indices().prepareRefresh().get();
		// Now you can start searching!
		getClient().prepareSearch().get();
	}

	/**
	 * 批量根据条件删除
	 */
	public static long deletByQuery() {
		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(getClient())
				// filter可以添加多个，类型QueryBuilders下的都可以
				.filter(QueryBuilders.matchQuery("message", "Elasticsearch")).source("twitter")// 设置索引名称
				.get();
		// 被删除文档数目
		long deleted = response.getDeleted();
		return deleted;
	}

	public static String testQuery(String index, String type, String id) {
		String returnStr = null;
		try {
			GetResponse response = getClient().prepareGet(index, type, id).execute().actionGet();
			returnStr = response.getSourceAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	/**
	 * 此方法描述的是： 更加query查询条件 分页查询index
	 * 
	 * @param index
	 *            索引
	 * @param query
	 *            查询条件
	 * @param size
	 *            条数
	 */
	public static List<Map<String, Object>> query(String index, QueryBuilder query, Integer size) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		// 搜索结果存入SearchResponse
		SearchResponse response = getClient().prepareSearch(index).setQuery(query) // 设置查询器
				.setSize(size) // 一次查询文档数
				.get();
		returnList = responseToList(response);
		return returnList;
	}

	/**
	 * 根据文档名、字段名、字段值查询某一条记录的详细信息；query查询
	 * 
	 * @param type
	 *            文档名，相当于oracle中的表名，例如：ql_xz；
	 * @param key
	 *            字段名，例如：bdcqzh
	 * @param value
	 *            字段值，如：“”
	 * @return List
	 */
	public static List<Map<String, Object>> getQueryDataBySingleField(String index, String type, String key,
			String value) {

		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		TransportClient client = null;
		try {
			client = getClient();
			QueryBuilder qb = QueryBuilders.termQuery(key, value);
			SearchResponse response = client.prepareSearch(index).setTypes(type).setQuery(qb).setFrom(0).setSize(10000)
					.setExplain(true).execute().actionGet();
			returnList = responseToList(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return returnList;
	}

	/**
	 * 多条件 文档名、字段名、字段值，查询某一条记录的详细信息
	 * 
	 * @param type
	 *            文档名，相当于oracle中的表名，例如：ql_xz
	 * @param map
	 *            字段名：字段值 的map
	 * @return List
	 * @author Lixin
	 */
	public static List<Map<String, Object>> getBoolDataByMuchField(String index, String type, Map<String, String> map) {

		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		TransportClient client = null;
		try {
			client = getClient();
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			for (String in : map.keySet()) {
				// map.keySet()返回的是所有key的值
				String str = map.get(in);// 得到每个key多对用value的值
				boolQueryBuilder.must(QueryBuilders.termQuery(in, str));
			}
			SearchResponse response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder).setFrom(0)
					.setSize(10000).setExplain(true).execute().actionGet();
			returnList = responseToList(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return returnList;
	}

	/**
	 * 单条件 模糊查询
	 * 
	 * @param type
	 *            文档名，相当于oracle中的表名，例如：ql_xz
	 * @param key
	 *            字段名，例如：bdcqzh
	 * @param value
	 *            字段名模糊值：如 *123* ;?123*;?123?;*123?;
	 * @return List
	 * @author Lixin
	 */
	public static List<Map<String, Object>> getDataByillegible(String index, String type, String key, String value) {

		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		TransportClient client = null;
		try {
			client = getClient();
			WildcardQueryBuilder wBuilder = QueryBuilders.wildcardQuery(key, value);
			SearchResponse response = client.prepareSearch(index).setTypes(type).setQuery(wBuilder).setFrom(0)
					.setSize(10000).setExplain(true).execute().actionGet();
			returnList = responseToList(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return returnList;
	}

	/**
	 * 多条件 模糊查询
	 * 
	 * @param type
	 *            type 文档名，相当于oracle中的表名，例如：ql_xz
	 * @param map
	 *            包含key:value 模糊值键值对
	 * @return List
	 * @author Lixin
	 */
	public static List<Map<String, Object>> getDataByMuchillegible(String index, String type, Map<String, String> map) {

		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		TransportClient client = null;
		try {
			client = getClient();
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			for (String in : map.keySet()) {
				// map.keySet()返回的是所有key的值
				String str = map.get(in);// 得到每个key多对用value的值
				boolQueryBuilder.must(QueryBuilders.wildcardQuery(in, str));
			}
			SearchResponse response = client.prepareSearch(index).setTypes(type).setQuery(boolQueryBuilder).setFrom(0)
					.setSize(10000).setExplain(true).execute().actionGet();
			returnList = responseToList(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return returnList;
	}

	/**
	 * 将查询后获得的response转成list
	 * 
	 * @param client
	 * @param response
	 * @return
	 */
	public static List<Map<String, Object>> responseToList(SearchResponse response) {
		SearchHits hits = response.getHits();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < hits.getHits().length; i++) {
			Map<String, Object> map = hits.getAt(i).getSourceAsMap();
			list.add(map);
		}
		return list;
	}

	/** --------------集群状态查询-------------- **/

	/**
	 * 获取集群管理的ClusterAdminClient对象
	 */
	public static ClusterAdminClient getClusterAdminClient() {
		return getClient().admin().cluster();
	}

	/**
	 * 查看集群的健康信息
	 */
	public static ClusterHealthResponse getClusterHealth() {
		return getClusterAdminClient().prepareHealth().get();
	}

	/**
	 * 查看索引所在节点的健康信息 集群状态有个枚举类 ClusterHealthStatus
	 */
	public static ClusterHealthStatus getNodeStateByIndex(String index) {
		ClusterHealthResponse response = getClusterAdminClient().prepareHealth(index).setWaitForGreenStatus().get();
		ClusterHealthStatus status = response.getIndices().get(index).getStatus();
		return status;
	}

	/**
	 * 打印集群健康信息
	 */
	public static void printClusterHealth(ClusterHealthResponse healths) {
		String clusterName = healths.getClusterName();
		System.out.println("clusterName=" + clusterName);
		int numberOfDataNodes = healths.getNumberOfDataNodes();
		System.out.println("numberOfDataNodes=" + numberOfDataNodes);
		int numberOfNodes = healths.getNumberOfNodes();
		System.out.println("numberOfNodes=" + numberOfNodes);

		for (ClusterIndexHealth health : healths.getIndices().values()) {
			String index = health.getIndex();
			int numberOfShards = health.getNumberOfShards();
			int numberOfReplicas = health.getNumberOfReplicas();
			System.out.printf("index=%s,numberOfShards=%d,numberOfReplicas=%d\n", index, numberOfShards,
					numberOfReplicas);
			ClusterHealthStatus status = health.getStatus();
			System.out.println(status.toString());
		}
	}

}
