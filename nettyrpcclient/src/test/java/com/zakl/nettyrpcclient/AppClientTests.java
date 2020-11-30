package com.zakl.nettyrpcclient;

import com.google.common.eventbus.EventBus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppClientTests {

    @Test
    void contextLoads() {
        EventBus eventBus = new EventBus();
    }

}
