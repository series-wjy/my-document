package es;

import com.alibaba.fastjson.JSONObject;
import com.wjy.es.entity.Doc;
import com.wjy.es.util.EsClientTool;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class Test {
	
	
	
	    public static void main(String[] args) throws ParseException, IOException {
	        //判定索引是否存在 OK
//	        boolean flag=EsClientTool.isExists("test-1");
//	        System.out.println(flag);
//	        System.out.println("isExists:"+flag);
//	        //创建索引 OK
//	    	boolean flag=EsClientTool.createIndex("index1", 3, 1);
//	        System.out.println("createIndex:"+flag);
//	        //设置Mapping OK
//	    	setMapper();
//	    	
//	    	//查询索引 OK
//	    	EsClientTool.testQuery("twitter", "tweet", "3");
//	    	//删除索引 OK
//	    	EsClientTool.deleteIndex("index1");
	    	
	    	//文档创建 OK
//	    	addData();
	    	
	    	/**
	    	 * 批量操作
	    	 */
	    	//批量插入文档 OK
	    	addDocs();
	    	//批量根据查询条件删除 OK
//	    	EsTool.deletByQuery(); 
	    	//条件查询 OK
//	    	query();
	    	
	    	
	    	/**
	    	 * 查看集群信息
	    	 */
//	    	EsClientTool.printClusterHealth(EsClientTool.getClusterHealth());
	    }
	    public static void query(){
	    	
	    	 QueryBuilder qb=QueryBuilders.matchQuery(
	                 "user",
	                 "kimchy");
	    	 EsClientTool.query("twitter", qb, 1);
	    	 
	    }
	    
	    
	    //文档插入
	    public static void addData() throws ParseException{
	    	 JSONObject jsonObject = new JSONObject();
	         jsonObject.put("id", 2);
	         jsonObject.put("age", 18);
	         jsonObject.put("name", "li");
	         jsonObject.put("date", new Date());
	         String id=EsClientTool.addDoc("index1", "type", jsonObject.getString("id"), jsonObject);
	         System.out.println(id);
	    }
	    public static void addDocs() throws IOException{
	    	List<Doc> docList = new ArrayList<>();
	    	Doc doc1 = new Doc();
	    	doc1.setIndex("kkb-test1");
	    	doc1.setType("tweet");
	    	doc1.setId("1");
	    	XContentBuilder builder1 = jsonBuilder()
                    .startObject()
                    .field("user", "kimchy")
                    .field("postDate", new Date())
                    .field("message", "trying out Elasticsearch111")
                    .endObject();
	    	doc1.setBuilder(builder1);
	    	docList.add(doc1);
	    	
	    	Doc doc2 = new Doc();
	    	doc2.setIndex("kkb-test1");
	    	doc2.setType("tweet");
	    	doc2.setId("2");
	    	XContentBuilder builder2 = jsonBuilder()
                    .startObject()
                    .field("user", "kimchy2")
                    .field("postDate", new Date())
                    .field("message", "another post")
                    .endObject();
	    	doc2.setBuilder(builder2);
	    	docList.add(doc2);
	    	boolean isOK = EsClientTool.addDocs(docList);
	    	System.out.println(isOK);
	    }
	   
	   
	    public static void setMapper(){
	    	try {
	            XContentBuilder builder = jsonBuilder()
	                    .startObject()
	                    .startObject("properties")
	                    .startObject("id")
	                    .field("type", "long")
	                    .endObject()
	                    .startObject("title")
	                    .field("type", "text")
	                    .field("analyzer", "ik_max_word")
	                    .field("search_analyzer", "ik_max_word")
	                    .field("boost", 2)
	                    .endObject()
	                    .startObject("content")
	                    .field("type", "text")
	                    .field("analyzer", "ik_max_word")
	                    .field("search_analyzer", "ik_max_word")
	                    .endObject()
	                    .startObject("postdate")
	                    .field("type", "date")
	                    .field("format", "yyyy-MM-dd HH:mm:ss")
	                    .endObject()
	                    .startObject("url")
	                    .field("type", "keyword")
	                    .endObject()
	                    .endObject()
	                    .endObject();
	            String json  = Strings.toString(builder);
	            System.out.println(json);
	            //索引、类型、json串
	            EsClientTool.setMapping("index1", "blog",json);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
