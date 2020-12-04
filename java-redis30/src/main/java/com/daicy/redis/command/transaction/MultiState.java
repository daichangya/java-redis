/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.command.transaction;


import com.daicy.redis.Request;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultiState implements Iterable<Request> {

  private final List<Request> requests = new LinkedList<>();

  public void enqueue(Request request) {
    requests.add(request);
  }

  public int size() {
    return requests.size();
  }

  @Override
  public Iterator<Request> iterator() {
    return requests.iterator();
  }
}
