package com.jane.redissonplayground;

import com.jane.redissonplayground.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCacheReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapCacheTest extends BaseTest{

    @Test
    public void mapCacheTest(){
        TypedJsonJacksonCodec typedJsonJacksonCodec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapCacheReactive<Integer, Student> mapCache = this.client.getMapCache("users:cache", typedJsonJacksonCodec);


        Student student1 = new Student("Ritu", 30, "kathmandu", List.of(1,2,3));
        Student student2 = new Student("Jake", 10, "miami", List.of(6,7,9));

        Mono<Student> mono1 = mapCache.put(1, student1, 5, TimeUnit.SECONDS);
        Mono<Student> mono2 = mapCache.put(2, student2, 10, TimeUnit.SECONDS);

        StepVerifier.create(mono1.concatWith(mono2).then())
                .verifyComplete();

        sleep(3000);
        mapCache.get(1).doOnNext(System.out::println).subscribe();
        mapCache.get(2).doOnNext(System.out::println).subscribe();

        sleep(3000);
        mapCache.get(1).doOnNext(System.out::println).subscribe();
        mapCache.get(2).doOnNext(System.out::println).subscribe();
    }
}
