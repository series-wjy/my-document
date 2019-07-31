package com.wjy.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WjyProperties {
	@Value("${com.wjy.title}")
	private String title;
	@Value("${com.wjy.description}")
	private String description;

	//省略getter settet方法

	}