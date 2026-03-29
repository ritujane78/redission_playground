package com.jane.redissonplayground;

import com.jane.redissonplayground.config.RedissonConfig;
import com.jane.redissonplayground.dto.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LocalCachedMapTest extends BaseTest{
    RLocalCachedMap<Integer,Student> studentsMap;
    @BeforeAll
    public void beforeAll(){
        RedissonConfig config = new RedissonConfig();
        RedissonClient redissonClient = config.getClient();

        LocalCachedMapOptions<Integer, Student> mapOptions = LocalCachedMapOptions.<Integer, Student>name("students")
                .codec(new TypedJsonJacksonCodec(Integer.class, Student.class))
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.NONE   )
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);

        this.studentsMap = redissonClient.getLocalCachedMap(mapOptions);
    }

    @Test
    public void appServer1(){
        Student student1 = new Student("Ritu", 30, "kathmandu", List.of(1,2,3));
        Student student2 = new Student("Jake", 10, "miami", List.of(6,7,9));

        this.studentsMap.put(1, student1);
        this.studentsMap.put(2, student2);

        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(integer -> System.out.println(integer + "==" + this.studentsMap.get(1)))
                .subscribe();

        sleep(600000);
    }

    @Test
    public void appServer2(){
        Student student1 = new Student("Ritu-updated", 30, "kathmandu", List.of(1,2,3));
        this.studentsMap.put(1, student1);
    }
}
