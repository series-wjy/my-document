package com.wjy.http.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

	// �ر�����
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	// �Ƿ�ر�
	private boolean shutdown = false;

	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		server.await();
	}

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			// ����һ��socket������
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// ѭ���ȴ�http����
		while (!shutdown) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				// �����ȴ�http����
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();

				// ����һ��Request�������ڽ���http��������
				Request request = new Request(input);
				request.parse();

				// ����һ��Response �������ڷ��;�̬�ı�
				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResource();

				// �ر���
				socket.close();

				// ���URI���Ƿ��йر�����
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}