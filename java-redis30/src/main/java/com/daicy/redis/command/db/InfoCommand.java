/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.daicy.redis.command.db;


import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.Request;
import com.daicy.redis.annotation.Command;
import com.daicy.redis.annotation.ReadOnly;
import com.daicy.redis.command.DBCommand;
import com.daicy.redis.protocal.BulkRedisMessage;
import com.daicy.redis.storage.RedisDb;
import com.daicy.redis.protocal.RedisMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

/**
 * @author daichangya
 * Load {@literal default} server information like
 * <ul>
 * <li>memory</li>
 * <li>cpu utilization</li>
 * <li>replication</li>
 * </ul>
 * <p>
 * <p>
 * <p>
 * # Server
 * redis_version:2.8.24
 * redis_git_sha1:00000000
 * redis_git_dirty:0
 * redis_build_id:43c97ba83821701d
 * redis_mode:standalone
 * os:Linux 4.15.0-121-generic x86_64
 * arch_bits:64
 * multiplexing_api:epoll
 * gcc_version:7.4.0
 * process_id:9495
 * run_id:47d440d4cbe57c95adefccc7c87b547ade8173ba
 * tcp_port:6379
 * uptime_in_seconds:596380
 * uptime_in_days:6
 * hz:10
 * lru_clock:11411062
 * config_file:/etc/redis/redis.conf
 * <p>
 * # Clients
 * connected_clients:1
 * client_longest_output_list:0
 * client_biggest_input_buf:0
 * blocked_clients:0
 * <p>
 * # Memory
 * used_memory:812216
 * used_memory_human:793.18K
 * used_memory_rss:3878912
 * used_memory_peak:9829712
 * used_memory_peak_human:9.37M
 * used_memory_lua:36864
 * mem_fragmentation_ratio:4.78
 * mem_allocator:jemalloc-3.6.0
 * <p>
 * # Persistence
 * loading:0
 * rdb_changes_since_last_save:16160640
 * rdb_bgsave_in_progress:0
 * rdb_last_save_time:1604650202
 * rdb_last_bgsave_status:err
 * rdb_last_bgsave_time_sec:0
 * rdb_current_bgsave_time_sec:-1
 * aof_enabled:0
 * aof_rewrite_in_progress:0
 * aof_rewrite_scheduled:0
 * aof_last_rewrite_time_sec:-1
 * aof_current_rewrite_time_sec:-1
 * aof_last_bgrewrite_status:ok
 * aof_last_write_status:ok
 * <p>
 * # Stats
 * total_connections_received:600
 * total_commands_processed:16666914
 * instantaneous_ops_per_sec:0
 * total_net_input_bytes:483688190
 * total_net_output_bytes:85414088
 * instantaneous_input_kbps:0.00
 * instantaneous_output_kbps:0.00
 * rejected_connections:0
 * sync_full:0
 * sync_partial_ok:0
 * sync_partial_err:0
 * expired_keys:0
 * evicted_keys:0
 * keyspace_hits:200036
 * keyspace_misses:1
 * pubsub_channels:0
 * pubsub_patterns:0
 * latest_fork_usec:307
 * <p>
 * # ReplicationManager
 * role:master
 * connected_slaves:0
 * master_repl_offset:0
 * repl_backlog_active:0
 * repl_backlog_size:1048576
 * repl_backlog_first_byte_offset:0
 * repl_backlog_histlen:0
 * <p>
 * # CPU
 * used_cpu_sys:763.07
 * used_cpu_user:236.63
 * used_cpu_sys_children:11.36
 * used_cpu_user_children:60.26
 * <p>
 * # Keyspace
 * db0:keys=34,expires=0,avg_ttl=0
 * @return {@literal null} when used in pipeline / transaction.
 * @see <a href="http://redis.io/commands/info">Redis Documentation: INFO</a>
 */
@ReadOnly
@Command("info")
public class InfoCommand implements DBCommand {

    private static final String SHARP = "#";
    private static final String SEPARATOR = ":";
    private static final String DELIMITER = "\r\n";

    private static final String SECTION_KEYSPACE = "keyspace";
    private static final String SECTION_COMMANDSTATS = "commandstats";
    private static final String SECTION_CPU = "cpu";
    private static final String SECTION_STATS = "stats";
    private static final String SECTION_PERSISTENCE = "persistence";
    private static final String SECTION_MEMORY = "memory";
    private static final String SECTION_CLIENTS = "clients";
    private static final String SECTION_REPLICATION = "replication";
    private static final String SECTION_SERVER = "server";

    @Override
    public RedisMessage execute(RedisDb db, Request request) {
        DefaultRedisServerContext redisServerContext = request.getServerContext();
        Map<String, Map<String, String>> sections = new LinkedHashMap<>();
        String sectionName = request.getParamStr(0);
        if (StringUtils.isEmpty(sectionName)) {
            sections.put(sectionName, section(sectionName, redisServerContext));
        } else {
            for (String section : allSections()) {
                sections.put(section, section(section, redisServerContext));
            }
        }
        return new BulkRedisMessage(makeString(sections));
    }

    private String makeString(Map<String, Map<String, String>> sections) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Map<String, String>> section : sections.entrySet()) {
            sb.append(SHARP).append(section.getKey()).append(DELIMITER);
            for (Entry<String, String> entry : section.getValue().entrySet()) {
                sb.append(entry.getKey()).append(SEPARATOR).append(entry.getValue()).append(DELIMITER);
            }
            sb.append(DELIMITER);
        }
        sb.append(DELIMITER);
        return sb.toString();
    }

    private List<String> allSections() {
        return asList(SECTION_SERVER, SECTION_REPLICATION, SECTION_CLIENTS,
                SECTION_MEMORY, SECTION_PERSISTENCE, SECTION_STATS, SECTION_CPU,
                SECTION_COMMANDSTATS, SECTION_KEYSPACE);
    }

    private Map<String, String> section(String section, DefaultRedisServerContext ctx) {
        switch (section.toLowerCase()) {
            case SECTION_SERVER:
                return server(ctx);
            case SECTION_REPLICATION:
                return replication(ctx);
            case SECTION_CLIENTS:
                return clients(ctx);
            case SECTION_MEMORY:
                return memory(ctx);
            case SECTION_PERSISTENCE:
                return persistence(ctx);
            case SECTION_STATS:
                return stats(ctx);
            case SECTION_CPU:
                return cpu(ctx);
            case SECTION_COMMANDSTATS:
                return commandstats(ctx);
            case SECTION_KEYSPACE:
                return keyspace(ctx);
            default:
                break;
        }
        return null;
    }

    private Map<String, String> server(DefaultRedisServerContext ctx) {
        return map(entry("redis_version", "2.8.24"),
                entry("tcp_port", valueOf(ctx.getServer().getPort())),
                entry("os", fullOsName()),
                entry("java_version", javaVersion()));
    }

    private String javaVersion() {
        return System.getProperty("java.version");
    }

    private String fullOsName() {
        return System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch");
    }

    private Map<String, String> replication(DefaultRedisServerContext ctx) {
        // TODO:
        return map();
//    return map(entry("role", getServerState(ctx).isMaster() ? "master" : "slave"),
//        entry("connected_slaves", slaves(ctx)));
    }

    private String slaves(DefaultRedisServerContext ctx) {
        // TODO:
        return null;
//    return valueOf(getAdminDict(ctx).getSet(String("slaves")).size());
    }

    private Map<String, String> clients(DefaultRedisServerContext ctx) {
        return map(entry("connected_clients", valueOf(ctx.getClients())));
    }

    private Map<String, String> memory(DefaultRedisServerContext ctx) {
        return map(entry("used_memory", valueOf(Runtime.getRuntime().totalMemory())));
    }

    private Map<String, String> persistence(DefaultRedisServerContext ctx) {
        // TODO:
        return map();
    }

    private Map<String, String> stats(DefaultRedisServerContext ctx) {
        // TODO:
        return map();
    }

    private Map<String, String> cpu(DefaultRedisServerContext ctx) {
        // TODO:
        return map();
    }

    private Map<String, String> commandstats(DefaultRedisServerContext ctx) {
        // TODO:
        return map();
    }

    private Map<String, String> keyspace(DefaultRedisServerContext ctx) {
        // TODO:
        return map();
    }

    @SafeVarargs
    private static Map<String, String> map(Entry<String, String>... values) {
        return Stream.of(values)
                .collect(collectingAndThen(toMap(Entry::getKey, Entry::getValue), TreeMap<String, String>::new));
    }

    public static Entry<String, String> entry(String key, String value) {
        return new SimpleEntry<>(key, value);
    }
}
