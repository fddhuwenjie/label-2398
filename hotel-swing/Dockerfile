# 宾馆客房管理系统 - Swing桌面应用（支持noVNC远程访问）
# 基于Ubuntu + Xvfb + noVNC，支持ARM和X86

FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-8 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 运行环境 - Ubuntu + 虚拟显示 + noVNC
FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive
ENV DISPLAY=:1
ENV VNC_PORT=5901
ENV NOVNC_PORT=6080
ENV LANG=zh_CN.UTF-8
ENV LC_ALL=zh_CN.UTF-8
ENV TZ=Asia/Shanghai

# 安装依赖
RUN apt-get update && apt-get install -y --no-install-recommends \
    openjdk-8-jre \
    xvfb \
    x11vnc \
    novnc \
    python3-websockify \
    supervisor \
    fonts-wqy-zenhei \
    fonts-wqy-microhei \
    locales \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /root/.vnc \
    && locale-gen zh_CN.UTF-8 \
    && ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

WORKDIR /app
COPY --from=builder /build/target/hotel-management-1.0.0.jar ./hotel-management.jar
COPY --from=builder /build/src/main/resources/db.properties ./db.properties

# 创建首页自动跳转 + 隐藏未加密提示条
RUN echo '<!DOCTYPE html><html><head><meta http-equiv="refresh" content="0;url=/vnc.html?autoconnect=true&resize=scale&reconnect=true&reconnect_delay=1000"></head></html>' > /usr/share/novnc/index.html \
    && sed -i 's|</head>|<style>#noVNC_status{display:none !important;}</style></head>|' /usr/share/novnc/vnc.html

# Supervisor配置
RUN echo '[supervisord]\n\
nodaemon=true\n\
\n\
[program:xvfb]\n\
command=/usr/bin/Xvfb :1 -screen 0 1280x800x24\n\
autorestart=true\n\
priority=100\n\
\n\
[program:x11vnc]\n\
command=/usr/bin/x11vnc -display :1 -forever -shared -rfbport 5901 -nopw\n\
autorestart=true\n\
priority=200\n\
\n\
[program:novnc]\n\
command=/usr/bin/websockify --web=/usr/share/novnc/ 6080 localhost:5901\n\
autorestart=true\n\
priority=300\n\
\n\
[program:hotel-app]\n\
command=/usr/bin/java -jar /app/hotel-management.jar\n\
environment=DISPLAY=":1"\n\
autorestart=true\n\
priority=400\n\
startsecs=5\n\
' > /etc/supervisor/conf.d/supervisord.conf

EXPOSE 6080

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
