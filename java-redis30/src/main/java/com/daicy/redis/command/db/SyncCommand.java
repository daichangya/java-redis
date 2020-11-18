///*
// * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
// * Distributed under the terms of the MIT License
// */
//
//package com.daicy.redis.command.db;
//
//
//import com.daicy.redis.Request;
//import com.daicy.redis.annotation.Command;
//import com.daicy.redis.annotation.ReadOnly;
//import com.daicy.redis.command.DBCommand;
//import com.daicy.redis.protocal.RedisMessage;
//import com.daicy.redis.storage.RedisDb;
//
//import java.io.IOException;
//
//@ReadOnly
//@Command("sync")
//public class SyncCommand implements DBCommand {
//
//  private MasterReplication master;
//
//  @Override
//  public RedisMessage execute(RedisDb db, Request request) {
//    try {
//      DBServerContext server = getClauDB(request.getServerContext());
//
//      ByteBufferOutputStream output = new ByteBufferOutputStream();
//      server.exportRDB(output);
//
//      if (master == null) {
//        master = new MasterReplication(server);
//        master.start();
//      }
//
//      master.addSlave(request.getSession().getId());
//
//      return string(new String(output.toByteArray()));
//    } catch (IOException e) {
//      return error("ERROR replication error");
//    }
//  }
//}
