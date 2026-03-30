package com.jane.redissonplayground;

import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class HyperLogLogTest extends BaseTest{
    @Test
    public void hyperLogLogTest(){
        RHyperLogLogReactive<Long> counter = this.client.getHyperLogLog("user:visits", LongCodec.INSTANCE);
        List<Long> list1 = LongStream.rangeClosed(1,25000)
                .boxed()
                .collect(Collectors.toList());
        List<Long> list2 = LongStream.rangeClosed(25001,50000)
                .boxed()
                .collect(Collectors.toList());
        List<Long> list3 = LongStream.rangeClosed(50001,75000)
                .boxed()
                .collect(Collectors.toList());
        List<Long> list4 = LongStream.rangeClosed(75001,100_000)
                .boxed()
                .collect(Collectors.toList());

        Mono<Void> mono = Flux.just(list1,list2,list3,list4)
                .flatMap(l -> counter.addAll(l))
                .then();

        StepVerifier.create(mono)
                .verifyComplete();
        counter.count()
                .doOnNext(System.out::println)
                .subscribe();


    }
}
