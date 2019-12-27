package com.wjy.es.mapper;

import java.util.List;

import com.wjy.es.model.Products;
import com.wjy.es.model.ProductsExample;
import org.apache.ibatis.annotations.Param;

public interface ProductsMapper {
    int countByExample(ProductsExample example);

    int deleteByExample(ProductsExample example);

    int deleteByPrimaryKey(Integer pid);

    int insert(Products record);

    int insertSelective(Products record);

    List<Products> selectByExampleWithBLOBs(ProductsExample example);

    List<Products> selectByExample(ProductsExample example);
    
    List<Products> selectAll();
    

    Products selectByPrimaryKey(Integer pid);

    int updateByExampleSelective(@Param("record") Products record, @Param("example") ProductsExample example);

    int updateByExampleWithBLOBs(@Param("record") Products record, @Param("example") ProductsExample example);

    int updateByExample(@Param("record") Products record, @Param("example") ProductsExample example);

    int updateByPrimaryKeySelective(Products record);

    int updateByPrimaryKeyWithBLOBs(Products record);

    int updateByPrimaryKey(Products record);
}