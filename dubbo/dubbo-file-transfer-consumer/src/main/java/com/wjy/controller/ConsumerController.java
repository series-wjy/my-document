package com.wjy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wjy.service.DemoHessianService;
import com.wjy.service.DemoService;
import com.wjy.vo.FileInfoVo;
import com.wjy.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/files")
public class ConsumerController {
    private static final Logger log = LoggerFactory.getLogger(ConsumerController.class.getName());
    @Reference(version = "1.0.0", check = true)
    private DemoService demoService;
    @Reference(version = "1.0.0", check = true)
    private DemoHessianService demoHessianService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<FileInfoVo>> list() {
        List<FileInfoVo> list = demoService.findAllBook(null);
        return Result.success(list, list.size());
    }

    /**
     * 测试上传并保存文件
     *
     * @return
     */
    @RequestMapping(value = "/testSaveFile", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> testSaveFile() {
        LocalDateTime start = LocalDateTime.now();
        System.out.println(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "：>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>文件传送开始<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        try {
            File file = new File("e:\\download\\teacherreycoursesresources-master.zip");
            InputStream in = demoHessianService.testSaveFile(file.getName(),
                    new BufferedInputStream(new FileInputStream(file)));

            byte[] buffer = new byte[1024];
            int len = 0;
            FileOutputStream fos = new FileOutputStream("e:\\transFile\\new.jpg");
            while ((len = in.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();

            LocalDateTime end = LocalDateTime.now();
            System.out.println(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "：>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>文件传送结束：" + Duration.between(start, end).toMillis() + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            return Result.success("文件保存成功", "");
        } catch (IOException e) {
            log.error("查询失败：" + e.getMessage());
            e.printStackTrace();
        }

        return Result.failed();
    }
}
