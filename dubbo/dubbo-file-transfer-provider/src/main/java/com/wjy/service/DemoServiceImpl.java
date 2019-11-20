package com.wjy.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.wjy.vo.FileInfoVo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = DemoService.class, protocol = "dubbo", version = "1.0.0", timeout = 120000)
@Component
public class DemoServiceImpl implements DemoService{

	@Override
	public List<FileInfoVo> findAllBook(FileInfoVo vo) {
		List<FileInfoVo> books = new ArrayList<FileInfoVo>();
		for(int k=0;k<50;k++) {
			books.add(new FileInfoVo(1000+k,"The book of day"+k));
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>findAllBook<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		return books;
	}
}
