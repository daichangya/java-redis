/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.event;


import com.daicy.redis.DefaultRedisServerContext;
import com.daicy.redis.command.pubsub.BaseSubscriptionSupport;
import com.daicy.redis.command.pubsub.PublishCommand;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

public class NotificationManager implements BaseSubscriptionSupport {

    private static final String PMESSAGE = "pmessage";

    private final DefaultRedisServerContext redisServerContext;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public NotificationManager(DefaultRedisServerContext redisServerContext) {
        this.redisServerContext = requireNonNull(redisServerContext);
    }

    public void start() {
        // nothing to do
    }

    public void stop() {
        executor.shutdown();
    }

    public void enqueue(Event event) {
        executor.execute(() -> PublishCommand.pubsubPublishMessage(event.getChannel(), event.getValue()));
    }

    @Override
    public String getTitle() {
        return PMESSAGE;
    }
}
