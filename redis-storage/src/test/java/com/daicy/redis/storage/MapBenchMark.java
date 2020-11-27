package com.daicy.redis.storage;

import com.daicy.collections.CowHashMap;
import com.google.common.collect.Maps;
import org.h2.mvstore.MVStore;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Map;
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
public class MapBenchMark {

    @Param({"10", "40", "70", "150"}) // 定义四个参数，之后会分别对这四个参数进行测试
    private int n;

    private Map<Integer,Integer> hashMap;
    private Map<Integer,Integer> cowHashMap;
    private Map<Integer,Integer> mvMap;


    @Setup(Level.Trial) // 初始化方法，在全部Benchmark运行之前进行
    public void init() {
        hashMap = Maps.newHashMap();
        cowHashMap = new CowHashMap<>();
        MVStore mvStore = MVStore.open(null);
        mvMap = mvStore.openMap("test");
        for (Integer i = 0; i < n; i++) {
            hashMap.put(i, i);
            cowHashMap.put(i, i);
            mvMap.put(i, i);
        }
    }

    @Benchmark
    public void hashMapTraverse() {
        for (Integer i = 0; i < n; i++) {
            hashMap.get(i);
        }
    }

    @Benchmark
    public void cowHashMapTraverse() {
        for (Integer i = 0; i < n; i++) {
            cowHashMap.get(i);
        }
    }

    @Benchmark
    public void mvMapTraverse() {
        for (Integer i = 0; i < n; i++) {
            mvMap.get(i);
        }
    }


    @TearDown(Level.Trial) // 结束方法，在全部Benchmark运行之后进行
    public void arrayRemove() {
        for (Integer i = 0; i < n; i++) {
            hashMap.remove(i);
            cowHashMap.remove(i);
            mvMap.remove(i);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(MapBenchMark.class.getSimpleName()).build();
        new Runner(options).run();
    }

//    Benchmark                        (n)   Mode  Cnt      Score      Error   Units
//    MapBenchMark.cowHashMapTraverse   10  thrpt    6  25579.958 ±  104.000  ops/ms
//    MapBenchMark.cowHashMapTraverse   40  thrpt    6   2993.579 ±  115.678  ops/ms
//    MapBenchMark.cowHashMapTraverse   70  thrpt    6   1385.807 ±   14.821  ops/ms
//    MapBenchMark.cowHashMapTraverse  100  thrpt    6    945.591 ±   33.152  ops/ms
//    MapBenchMark.hashMapTraverse      10  thrpt    6  23584.195 ± 2511.278  ops/ms
//    MapBenchMark.hashMapTraverse      40  thrpt    6   6177.790 ±  211.630  ops/ms
//    MapBenchMark.hashMapTraverse      70  thrpt    6   3714.278 ±   60.004  ops/ms
//    MapBenchMark.hashMapTraverse     100  thrpt    6   2573.492 ±   18.854  ops/ms
//    MapBenchMark.mvMapTraverse        10  thrpt    6   8182.763 ±   59.939  ops/ms
//    MapBenchMark.mvMapTraverse        40  thrpt    6   1525.189 ±   26.979  ops/ms
//    MapBenchMark.mvMapTraverse        70  thrpt    6    642.801 ±   15.280  ops/ms
//    MapBenchMark.mvMapTraverse       100  thrpt    6    420.541 ±   14.572  ops/ms

}
