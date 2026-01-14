package com.sky.controller.admin;


import com.aliyuncs.exceptions.ClientException;
import com.sky.result.Result;
import com.sky.utils.MinioUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags="通用工具接口")
public class CommonController {
    @Autowired
    private MinioUtil minioUtil;

    @PostMapping("/upload")
    public Result upload(String username, Integer age, @RequestParam("file") List<MultipartFile> files) throws IOException, ClientException {
        log.info("文件上传: {},{},{}", username, age, files.size());
        if (files == null || files.isEmpty()) {
            return Result.error("文件为空");
        }
        String url=minioUtil.upload(files.get(0));
        log.info("文件上传成功: 文件访问的url: {}", url);
        return Result.success(url);
    }

}
