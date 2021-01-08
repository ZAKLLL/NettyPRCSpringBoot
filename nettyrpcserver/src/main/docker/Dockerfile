FROM java:alpine
RUN mkdir /nettyrpcserver

# 此处指定nettyrpcserver-1.0.jar 发送到 容器中的 /nettyrpcserver 文件夹中
ADD nettyrpcserver-1.0.jar /nettyrpcserver

WORKDIR /nettyrpcserver

EXPOSE 18882 8082

