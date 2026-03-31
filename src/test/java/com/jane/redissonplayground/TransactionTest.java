package com.jane.redissonplayground;

import org.junit.jupiter.api.*;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.esotericsoftware.minlog.Log.set;

public class TransactionTest extends BaseTest{

    private RBucketReactive<Long> user1Balance;
    private RBucketReactive<Long> user2Balance;

    @BeforeAll
    public void setup() {
        super.setup();

        this.user1Balance = this.client.getBucket("user:1:balance", LongCodec.INSTANCE);
        this.user2Balance = this.client.getBucket("user:2:balance", LongCodec.INSTANCE);

        Mono<Void> mono = this.user1Balance.set(100L)
                .then(this.user2Balance.set(0L))
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @AfterAll
    public void afterAll() {
        Mono<Void> mono = Flux.zip(this.user1Balance.get(), this.user2Balance.get())
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(mono)
                .verifyComplete();

    }
    @Test
    public void testNonTransaction(){

        this.transfer(user1Balance, user2Balance, Long.valueOf(50))
                .thenReturn(0)
                .map(i -> 5/i)
                .doOnError(System.out::println)
                .subscribe();

        sleep(1000);
    }
    @Test
    public void testTransaction(){
        RTransactionReactive transaction = this.client.createTransaction(TransactionOptions.defaults());
        RBucketReactive<Long> user1Balance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> user2Balance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);

        this.transfer(user1Balance, user2Balance, Long.valueOf(50))
                .thenReturn(0)
                .map(i -> 5/i)
                .then(transaction.commit())
                .doOnError(System.out::println)
                .onErrorResume(e -> transaction.rollback())
                .subscribe();

        sleep(1000);
    }
    private Mono<Void> transfer(RBucketReactive<Long>transferFrom, RBucketReactive<Long> transferTo, Long amount){
        return Flux.zip(transferFrom.get(), transferTo.get())
                .filter(t -> t.getT1() >= amount)
                .flatMap(t -> transferFrom.set(t.getT1() - amount).thenReturn(t))
                .flatMap(t -> transferTo.set(t.getT2() + amount).thenReturn(t))
                .then();
    }
}
