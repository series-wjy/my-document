package com.wjy.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hessian测试-提供者
 *
 * @author admin
 */
@Service(interfaceClass = DemoHessianService.class, protocol = "hessian", version = "1.0.0", timeout = 120000)
@Component
public class DemoHessianServiceImpl implements DemoHessianService {
    private static AtomicInteger count = new AtomicInteger();

    @Override
    public InputStream testSaveFile(String fileName, InputStream nis) {
        OutputStream fos = null;
        byte[] buffer = new byte[4096];
        int len = 0;
        try {
            fileName = count.getAndAdd(1) + "_transfile.zip";
            fos = new FileOutputStream("e:\\transFile\\" + fileName);
            MessageDigest md = MessageDigest.getInstance("MD5");
            while ((len = nis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                md.update(buffer, 0, len);
            }
            BigInteger bi = new BigInteger(1, md.digest());

            return new FileInputStream("d:\\new.jpg");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("保存文件失败");
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
