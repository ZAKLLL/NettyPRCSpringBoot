package com.zakl.nettyrpcserver;

import com.zakl.nettyrpcserver.config.NettyServerConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;


@SpringBootApplication
@EnableCaching
@ComponentScan(value = {"com.zakl"})
public class AppServer implements CommandLineRunner {

    @Resource(name = NettyServerConfig.REGISTRY_BEAN_NAME)
    private NettyServerConfig nettyServerConfig;

    public static void main(String[] args) {

        SpringApplication.run(AppServer.class, args);
//        SpringApplication app = new SpringApplication(AppServer.class);
//        app.setApplicationStartup(new BufferingApplicationStartup(2048));
//        app.run(args);
    }

    //启动Netty 服务
    @Override
    public void run(String... args) {
        nettyServerConfig.startNettyServer();
    }
}
