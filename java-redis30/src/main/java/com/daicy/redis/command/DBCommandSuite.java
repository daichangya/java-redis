/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command;

import com.daicy.redis.ServiceLoaderUtils;
import com.daicy.redis.annotation.ReadOnly;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class DBCommandSuite extends CommandSuite {

  private static final Set<String> COMMAND_BLACK_LIST = new HashSet<>(asList("ping", "echo", "quit", "time"));

  protected static final List<DBCommand> dbCommandList = ServiceLoaderUtils.loadServices(DBCommand.class);

  public DBCommandSuite() {
    super(new DBCommandWrapperFactory());
    // connection
    dbCommandList.forEach(this::processCommand);
  }

  public boolean isReadOnly(String command) {
    return COMMAND_BLACK_LIST.contains(command) || isPresent(command, ReadOnly.class);
  }
}
