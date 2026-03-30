package com.jane.redissonplayground;

import org.junit.jupiter.api.Test;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ListQueueStackTest extends BaseTest{

    @Test
    public void listTest(){
        RListReactive<Long> list = this.client.getList("number-list", LongCodec.INSTANCE);

        List<Long> longList = LongStream.rangeClosed(1,10)
                .boxed()
                .collect(Collectors.toList());
//
//        Mono<Void> listMono = Flux.range(1,10)
//                .map(Long::valueOf)
//                .flatMap(i -> list.add(i))
//                .then();

        StepVerifier.create(list.addAll(longList).then())
                .verifyComplete();
        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();

    }

    @Test
    public void queueTest(){
        RQueueReactive<Long> queueList = this.client.getQueue("number-list", LongCodec.INSTANCE);

        Mono<Void> queuePoll = queueList.poll()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(queuePoll)
                .verifyComplete();

        StepVerifier.create(queueList.size())
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    public void stackTest(){
        RDequeReactive<Long> dequeList = this.client.getDeque("number-list", LongCodec.INSTANCE);

        Mono<Void> dequePoll = dequeList.pollLast()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(dequePoll)
                .verifyComplete();

        StepVerifier.create(dequeList.size())
                .expectNext(2)
                .verifyComplete();
    }
}
