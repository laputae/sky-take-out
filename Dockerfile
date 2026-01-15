# 1. 基础镜像
FROM eclipse-temurin:8-jdk-alpine

# 2. 维护者信息
LABEL maintainer="sky-takeout"

# 3. 设置时区
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 4. 复制 Jar 包
COPY sky-server/target/*.jar app.jar

# 5. 暴露端口
EXPOSE 8080

# 6. 启动命令 【这里是关键修改】
# 修改点1：内存由 512m 提升至 1024m，解决 CPU 99% 导致的卡顿
# 修改点2：保留随机数参数，作为双重保险
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]