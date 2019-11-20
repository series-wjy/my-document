package com.wjy.es.service;


import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.wjy.es.model.Products;
import com.wjy.es.vo.SearchRequestVo;
import com.wjy.es.vo.SearchResponseVo;


public interface IProductsService {
    
    public List<Products> findProducts();
    public void toEsAll(String index) throws IOException;
    public void toEsAllt(String index) throws IOException, ParseException;
    
    public List<SearchResponseVo> search(SearchRequestVo vo);
}