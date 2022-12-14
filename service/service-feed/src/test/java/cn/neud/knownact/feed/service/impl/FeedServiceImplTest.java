package cn.neud.knownact.feed.service.impl;

import cn.neud.knownact.feed.service.FeedService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class FeedServiceImplTest {

    @Resource
    FeedService feedService;

    @Test
    void train() throws Exception {
        feedService.train();
        Thread.sleep(10000);
    }
}