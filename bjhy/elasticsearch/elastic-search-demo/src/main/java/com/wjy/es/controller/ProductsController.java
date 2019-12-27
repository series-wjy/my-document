package com.wjy.es.controller;


import java.util.List;

import com.wjy.es.model.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wjy.es.service.IProductsService;
import com.wjy.es.vo.SearchRequestVo;
import com.wjy.es.vo.SearchResponseVo;


@RestController/**自动返回的是json格式数据***/
public class ProductsController {
    
//	@Qualifier("productsServiceImpl")
	@Autowired
    private IProductsService productsServiceImpl;

    @RequestMapping("list")
    public List<Products> list(){
        List<Products> list = productsServiceImpl.findProducts();
        return list;
    }
    @RequestMapping("addAll")
    public void addAll(){
        try {
			productsServiceImpl.toEsAllt("goods");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    @RequestMapping("earchEs")
    public List<SearchResponseVo> earchEs(){
    	List<SearchResponseVo> list = null;
    	
    	SearchRequestVo vo = new SearchRequestVo();
        try {
			productsServiceImpl.search(vo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return list;
    }
}