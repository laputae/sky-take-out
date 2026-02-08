package com.sky.utils;

import com.sky.constant.MessageConstant;
import com.sky.properties.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    /**
     * 文件上传
     * @param file 前端传来的文件
     * @return 文件的访问路径
     */
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        // 生成新文件名，防止重名覆盖 (例如: uuid.jpg)
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString() + extension;

        try {
            InputStream inputStream = file.getInputStream();
            // 上传到 MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 拼接返回路径：http://IP:9000/bucketName/fileName
            String requestUrl = String.format("%s/%s/%s",
                    minioProperties.getEndpoint(),
                    minioProperties.getBucketName(),
                    objectName);

            log.info("文件上传成功: {}", requestUrl);
            return requestUrl;

        } catch (Exception e) {
            log.error(MessageConstant.UPLOAD_FAILED, e);
            throw new RuntimeException(MessageConstant.UPLOAD_FAILED);
        }
    }
}