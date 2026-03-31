package com.jane.redissonplayground;


import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

public class SortedSetTest extends BaseTest{

    @Test
    public void testSortedSet(){
        RScoredSortedSetReactive<String> sortedSet = this.client.getScoredSortedSet("student:score", StringCodec.INSTANCE);

        Mono<Void> mono = sortedSet.addScore("sam", 12.35)
                .then(sortedSet.add(23.7, "mike"))
                .then(sortedSet.addScore("jake", 7))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();

        sortedSet.entryRange(0,1)
                .flatMapIterable(Function.identity())
                .doOnNext(System.out::println)
                .subscribe();
    }
}
