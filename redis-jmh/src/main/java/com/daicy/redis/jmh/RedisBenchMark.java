package com.daicy.redis.jmh;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.jmh
 * @date:11/10/20
 */
@BenchmarkMode(Mode.Throughput) // 吞吐量
@OutputTimeUnit(TimeUnit.SECONDS) // 结果所使用的时间单位
@State(Scope.Thread) // 每个测试线程分配一个实例
@Fork(2) // Fork进行的数目
@Warmup(iterations = 2) // 先预热4轮
@Measurement(iterations = 3) // 进行3轮测试
public class RedisBenchMark {
    private StatefulRedisConnection<String, String> connection;

    private Jedis jedis;

    @Setup(Level.Trial) // 初始化方法，在全部Benchmark运行之前进行
    public void init() {
        RedisClient client = RedisClient.create("redis://localhost");
        connection = client.connect();
        jedis = new Jedis("localhost");
    }

    @Benchmark
    public void lettuceSetCommands() {
        connection.sync().set("c", "d");
    }

    @Benchmark
    public void jedisSetCommands() {
        jedis.set("a","b");
    }


    @TearDown(Level.Trial) // 结束方法，在全部Benchmark运行之后进行
    public void close() {
        connection.close();
        jedis.close();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(RedisBenchMark.class.getSimpleName()).build();
        new Runner(options).run();
    }
}
