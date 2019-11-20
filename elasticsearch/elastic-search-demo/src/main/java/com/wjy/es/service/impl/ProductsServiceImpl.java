package com.wjy.es.service.impl;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wjy.es.entity.Doc;
import com.wjy.es.model.GoodsEs;
import com.wjy.es.model.Products;
import com.wjy.es.service.IProductsService;
import com.wjy.es.util.DateUtils;
import com.wjy.es.util.EsClientTool;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wjy.es.mapper.EsMapper;
import com.wjy.es.mapper.ProductsMapper;
import com.wjy.es.vo.SearchRequestVo;
import com.wjy.es.vo.SearchResponseVo;

@Service("productsServiceImpl")
public class ProductsServiceImpl implements IProductsService {
	private final static Logger logger = LoggerFactory.getLogger(ProductsServiceImpl.class);
	@Autowired
	private ProductsMapper productsMapper;
	@Autowired
	private EsMapper esMapper;

	public List<Products> findProducts() {
		List<Products> list = productsMapper.selectAll();
		for (int i = 0; i < list.size(); i++) {
			Products p = list.get(i);
			logger.debug(p.getName());
		}
		return list;
	}

	public void toEsAll(String index) throws IOException {
		EsClientTool.createIndex(index, 1,0);
		XContentBuilder builder = jsonBuilder().startObject().startObject("properties").startObject("pid")
				.field("type", "long").endObject().startObject("name").field("type", "text").field("boost", 2)
				.field("analyzer", "ik_max_word").field("search_analyzer", "ik_max_word").endObject()
				// .startObject("content")
				// .field("type", "text")
				// .field("analyzer", "ik_max_word")
				// .field("search_analyzer", "ik_max_word")
				// .endObject()
				.startObject("releaseTime").field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss").endObject()
				
				.startObject("description").field("type", "text").field("analyzer", "ik_max_word")
				.field("search_analyzer", "ik_max_word").field("boost", 1)
				.startObject("fields")
				.startObject("suggest")
				.field("type", "completion")
				.field("analyzer", "ik_max_word")
				.endObject()
				.endObject()
				
				.endObject()
				
				
				.endObject().endObject();
		String json = Strings.toString(builder);
		// 建立索引结构
		EsClientTool.setMapping(index, "type", json);
		List<Products> list = productsMapper.selectAll();
		List<Doc> docList = new ArrayList<>();

		// 插入索引值
		for (int i = 0; i < list.size(); i++) {
			Products pr = list.get(i);
			Doc doc = new Doc();
			doc.setIndex(index);
			doc.setType("type");
			doc.setId(String.valueOf(pr.getPid()));
			XContentBuilder builder2 = jsonBuilder().startObject()
					// .field("pid", pr.getPid())
					.field("name", pr.getName())
					// .field("content", pr.get)
					// .field("releaseTime", pr.getReleaseTime())
					.field("description", pr.getDescription()).endObject();

			doc.setBuilder(builder2);
			docList.add(doc);

		}
		EsClientTool.addDocsBukProcessor(docList);
		// boolean isOK = EsClientTool.addDocs(docList);
		// System.out.println(isOK);
	}

	@Override
	public void toEsAllt(String index) throws IOException, ParseException {
		 EsClientTool.createIndex(index, 3, 1);
		XContentBuilder builder = jsonBuilder()
				// select
				// g.goods_id,g.goods_name,g.product_id,g.shop_id,g.shop_id,g.cost_price,
				// g.sell_price,g.inventory,g.is_specification,g.spec_info,g.create_time
				// ,p.brand,classify1,classify2,classify3
				// from tb_goods g
				// left JOIN tb_product p on g.product_id = p.product_id
				// ;

				.startObject().startObject("properties")
				
				.startObject("goods_id").field("type", "long")
				// .field("index","not_analyzed")
				.endObject()

				.startObject("goods_name").field("type", "text")
				.field("boost", 2).field("analyzer", "ik_max_word")
				.field("search_analyzer", "ik_max_word")
				.endObject()
//				.startObject("fields")
//				
//				.endObject()
				
				.startObject("suggest")
				.field("type", "completion")
				.field("analyzer", "ik_max_word")
				.endObject()
				
				.startObject("product_id").field("type", "long")
				// .field("index","not_analyzed")
				.endObject()

				.startObject("shop_id").field("type", "long")
				// .field("index","not_analyzed")
				.endObject()

				// cost_price,
				.startObject("cost_price").field("type", "double")
				// .field("index","not_analyzed")
				.endObject()

				// g.sell_price,
				.startObject("sell_price").field("type", "double")
				// .field("index","not_analyzed")
				.endObject()

				// g.inventory,
				.startObject("inventory").field("type", "long")
				// .field("index","not_analyzed")
				.endObject()

				// g.is_specification,
				// g.spec_info,
				.startObject("spec_info").field("type", "text")
				.field("boost", 10).field("analyzer", "ik_max_word")
				.field("search_analyzer", "ik_max_word").endObject()

				// g.create_time date_hour_minute_second_fraction

				.startObject("create_time")

				.field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss")
				// .field("format", "strict_date_optional_time||epoch_millis")
				.endObject()

				// ,p.brand,
				.startObject("brand").field("type", "long")
				// .field("index","not_analyzed")
				.endObject()

				// classify1,
				.startObject("classify1").field("type", "long")
				// .field("index","not_analyzed")
				.endObject()

				// classify2,
				.startObject("classify2").field("type", "long")
				// .field("index","not_analyzed")
				.endObject()

				// classify3
				.startObject("classify3").field("type", "long")
				// .field("index","not_analyzed")
				.endObject()

				// subtitle,p.product_name
				.startObject("subtitle").field("type", "text").field("analyzer", "ik_max_word")
				.field("search_analyzer", "ik_max_word").field("boost", 1).endObject()

				// product_name
				.startObject("product_name").field("type", "text").field("analyzer", "ik_max_word")
				.field("search_analyzer", "ik_max_word").field("boost", 1).endObject()
				
				.startObject("img_path").field("type", "text")
//				 .field("index","not_analyzed")
				.endObject()

				
				// g.template,
				.startObject("template").field("type", "long")
				// .field("index","not_analyzed")
				.endObject()
				
				.endObject().endObject();
		String json = Strings.toString(builder);
		// 建立索引结构
		EsClientTool.setMapping(index, "type", json);
		List<GoodsEs> list = esMapper.selectAllGoods();
		List<Doc> docList = new ArrayList<>();

		// 插入索引值
		for (int i = 0; i < list.size(); i++) {
			GoodsEs g = list.get(i);
			Doc doc = new Doc();
			doc.setIndex(index);
			doc.setType("type");
			doc.setId(String.valueOf(g.getGoodsId()));
			XContentBuilder builder2 = jsonBuilder().startObject()
					// .field("pid", pr.getPid())
					.field("goods_id", g.getGoodsId())
					
					.field("goods_name", g.getGoodsName())
					.field("suggest", g.getGoodsName())
					
					.field("product_id", g.getProductId()).field("shop_id", g.getShopId())
					.field("cost_price", g.getCostPrice()).field("sell_price", g.getSellPrice())
					.field("inventory", g.getInventory()).field("spec_info", g.getSpecInfo())
					.field("create_time",
							DateUtils.convertDate2String(DateUtils.DEFAILT_DATE_TIME_PATTERN, g.getCreateTime()))
					.field("brand", g.getBrand()).field("classify1", g.getClassify1())
					.field("classify2", g.getClassify2()).field("classify3", g.getClassify3())
					.field("subtitle", g.getSubtitle())
					.field("product_name", g.getProductName())
					.field("img_path", g.getImgPath())
					.field("template", g.getTemplate())
					.endObject();

			doc.setBuilder(builder2);
			docList.add(doc);

		}
		EsClientTool.addDocsBukProcessor(docList);
		// boolean isOK = EsClientTool.addDocs(docList);
		// System.out.println(isOK);
	}

	@Override
	public List<SearchResponseVo> search(SearchRequestVo vo) {

		String index = "goods";
		

		
		MultiMatchQueryBuilder qb = QueryBuilders.multiMatchQuery("亮瓷黑", "goods_name", "spec_info");
		TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("grades_count").field("product_id");
		SearchResponse sr = EsClientTool.getClient().prepareSearch(index)
				.setQuery(qb)
				.addAggregation(termsAggregationBuilder)
				.get()
				;
		System.out.println(qb.toString());
		System.out.println(termsAggregationBuilder.toString());
		Map<String, Aggregation> aggMap = sr.getAggregations().asMap();

		List<Map<String, Object>> valueList = EsClientTool.responseToList(sr);
		
		

		

		return null;
	}

}