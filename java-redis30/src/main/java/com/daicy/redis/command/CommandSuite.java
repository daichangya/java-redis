/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;

import com.daicy.redis.ServiceLoaderUtils;
import com.daicy.redis.annotation.Command;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class CommandSuite {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandSuite.class);

    protected final Map<String, Class<?>> metadata = new HashMap<>();
    protected final Map<String, RedisCommand> commands = new HashMap<>();

    protected static final List<RedisCommand> redisCommandList = ServiceLoaderUtils.loadServices(RedisCommand.class);

    private final NullCommand nullCommand = new NullCommand();

    private final CommandWrapperFactory factory;

    public CommandSuite() {
        this(new DefaultCommandWrapperFactory());
    }

    public CommandSuite(CommandWrapperFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
        redisCommandList.forEach(this::processCommand);
    }

    public RedisCommand getCommand(String name) {
        return commands.getOrDefault(name.toLowerCase(), nullCommand);
    }

    public boolean isPresent(String name, Class<? extends Annotation> annotationClass) {
        return getMetadata(name).isAnnotationPresent(annotationClass);
    }

    public boolean contains(String name) {
        return commands.get(name) != null;
    }


    protected void addCommand(String name, RedisCommand command) {
        commands.put(name.toLowerCase(), factory.wrap(command));
    }

    protected void processCommand(Object command) {
        Class<?> clazz = command.getClass();
        Command annotation = clazz.getAnnotation(Command.class);
        if (annotation != null) {
            commands.put(annotation.value(), factory.wrap(command));
            metadata.put(annotation.value(), clazz);
        } else {
            LOGGER.warn("annotation not present at {}", clazz.getName());
        }
    }

    private Class<?> getMetadata(String name) {
        return metadata.getOrDefault(name.toLowerCase(), Void.class);
    }
}
