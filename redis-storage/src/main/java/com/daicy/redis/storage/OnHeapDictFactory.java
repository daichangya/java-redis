/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.daicy.redis.storage;

import com.daicy.collections.CowHashMap;

public class OnHeapDictFactory implements DictFactory {

  @Override
  public Dict create() {
    return new OnHeapDict(new CowHashMap<>());
  }

  @Override
  public void clear() {
    // nothing to clear
  }
}
