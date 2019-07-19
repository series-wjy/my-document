package me.w1992wishes.tomcatwork.simple_tomcat_01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by 万勤锋 on 2017/1/23.
 */
public class Response {

    private static final int BUFFER_SIZE = 1024;
    Request request;
    OutputStream output;

    //log
    private static Logger LOGGER = LoggerFactory.getLogger(Response.class);

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if (file.exists()) {
                output.write("HTTP/1.0 200 OK\r\n".getBytes());
                output.write("\r\n".getBytes());;// 根据 HTTP 协议, 空行将结束头信息
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    output.write(bytes, 0, ch);
                    output.flush();
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
            } else {
                //file not found
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: 23\r\n" +
                        "\r\n" +
                        "<h1>我去你妹的！</h1>";
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            LOGGER.error("write out data fail", e);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

}
