package com.jane.redissonplayground;


import com.jane.redissonplayground.config.RedissonConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RedissonReactiveClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    private final RedissonConfig radissonConfig = new RedissonConfig();
    protected RedissonReactiveClient client;

    @BeforeAll
    protected void setup() {
        this.client = this.radissonConfig.getReactiveClient();
    }
    @AfterAll
    void tearDown() {
        this.client.shutdown();
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
