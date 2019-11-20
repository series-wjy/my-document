package com.wjy.service;

import com.wjy.vo.FileInfoVo;
import java.util.List;

public interface DemoService {

	List<FileInfoVo> findAllBook(FileInfoVo vo);
}
