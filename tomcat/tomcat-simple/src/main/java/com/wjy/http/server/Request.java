package com.wjy.http.server;

import java.io.IOException;
import java.io.InputStream;

public class Request {
	private InputStream input;
	
	private String uri;

	public Request(InputStream input) {
		this.input = input;
	}

	public void parse() {
		// Read a set of characters from the socket
		StringBuffer request = new StringBuffer(2048);
		int i;
		byte[] buffer = new byte[2048];
		try {
			// ��ȡ��������
			i = input.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			i = -1;
		}
		for (int j = 0; j < i; j++) {
			// ��ÿ���ֽ�ת��Ϊ�ַ�
			request.append((char) buffer[j]);
		}
		// ��ӡ�ַ���
		System.out.print(request.toString());
		// ����ת���������ַ�����URI
		uri = parseUri(request.toString());
	}

	private String parseUri(String requestString) {
		int index1, index2;
		// �ҵ���һ���ո�
		index1 = requestString.indexOf(' ');
		if (index1 != -1) {
			// �ҵ��ڶ����ո�
			index2 = requestString.indexOf(' ', index1 + 1);
			if (index2 > index1)
				// ��ȡ��һ���ո񵽵ڶ����ո�֮�������
				return requestString.substring(index1 + 1, index2);
		}
		return "";
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
