# 1. 基础镜像：使用专门针对 Java 8 的轻量级 Alpine 镜像
FROM eclipse-temurin:8-jdk-alpine

# 2. 维护者信息
LABEL maintainer="sky-takeout"

# 3. 设置时区为上海 (避免日志时间对不上)
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 4. 复制 Jar 包
# 关键点：因为是多模块项目，GitHub Actions 会在根目录打包
# 这里的路径必须指向子模块的 target 目录
COPY sky-server/target/*.jar app.jar

# 5. 暴露端口
EXPOSE 8080

# 6. 启动命令
# 关键点：针对 2GB 服务器，强制限制堆内存为 512M
# -Djava.security.egd... 是为了加快随机数生成，避免启动卡顿
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]