package cn.neud.knownact.feed;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
@EnableDiscoveryClient
// @EnableFeignClients(basePackages = "cn.neud.knownact.client")
@MapperScan("cn.neud.knownact.feed.dao")
@ComponentScan(basePackages = {"cn.neud.knownact.common", "cn.neud.knownact.feed"})
public class FeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedApplication.class, args);
    }

}
