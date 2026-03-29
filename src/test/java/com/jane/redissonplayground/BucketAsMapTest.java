package com.jane.redissonplayground;

import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class BucketAsMapTest extends BaseTest {

    @Test
    public void bucketAsMap(){
        Mono<Void> mono = this.client.getBuckets(StringCodec.INSTANCE)
                .get("user:1:name", "user:2:name", "user:3:name")
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }
}
