package com.daicy.redis.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.jmh
 * @date:11/10/20
 */
@BenchmarkMode(Mode.Throughput) // 吞吐量
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 结果所使用的时间单位
@State(Scope.Thread) // 每个测试线程分配一个实例
@Fork(2) // Fork进行的数目
@Warmup(iterations = 2) // 先预热4轮
@Measurement(iterations = 3) // 进行3轮测试
public class BenchMark {

    @Param({"10", "40", "70", "100"}) // 定义四个参数，之后会分别对这四个参数进行测试
    private int n;

    private List<Integer> array;
    private List<Integer> list;

    @Setup(Level.Trial) // 初始化方法，在全部Benchmark运行之前进行
    public void init() {
        array = new ArrayList<>(0);
        list = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            array.add(i);
            list.add(i);
        }
    }

    @Benchmark
    public void arrayTraverse() {
        for (int i = 0; i < n; i++) {
            array.get(i);
        }
    }

    @Benchmark
    public void listTraverse() {
        for (int i = 0; i < n; i++) {
            list.get(i);
        }
    }

    @TearDown(Level.Trial) // 结束方法，在全部Benchmark运行之后进行
    public void arrayRemove() {
        for (int i = 0; i < n; i++) {
            array.remove(0);
            list.remove(0);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(BenchMark.class.getSimpleName()).build();
        new Runner(options).run();
    }
}
