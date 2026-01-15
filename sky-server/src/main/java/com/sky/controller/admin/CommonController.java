package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.utils.MinioUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用工具接口")
public class CommonController {
    @Autowired
    private MinioUtil minioUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传: {}", file.getOriginalFilename());

        try {
            // 加上 try-catch 兜底，防止 MinIO 挂了前端不知道
            String url = minioUtil.upload(file);
            log.info("上传成功: {}", url);
            return Result.success(url);
        } catch (Exception e) {
            log.error("上传失败", e);
            return Result.error("文件上传失败，请稍后重试");
        }
    }
}
