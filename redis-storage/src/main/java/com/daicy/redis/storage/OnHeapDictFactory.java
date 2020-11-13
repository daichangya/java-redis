/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;

import java.util.HashMap;

public class OnHeapDictFactory implements DictFactory {

  @Override
  public Dict create() {
    return new OnHeapDict(new HashMap<>());
  }

  @Override
  public void clear() {
    // nothing to clear
  }
}
