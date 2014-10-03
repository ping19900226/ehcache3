/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ehcache.spi.test;

import org.ehcache.Cache;
import org.ehcache.config.StoreConfigurationImpl;
import org.ehcache.exceptions.CacheAccessException;
import org.ehcache.function.Predicates;
import org.ehcache.spi.cache.Store;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

/**
 * Test the {@link org.ehcache.spi.cache.Store#put(K key, V value)} contract of the
 * {@link org.ehcache.spi.cache.Store Store} interface.
 * <p/>
 *
 * @author Aurelien Broszniowski
 */

public class StorePutTest<K, V> extends SPIStoreTester<K, V> {

  public StorePutTest(final StoreFactory<K, V> factory) {
    super(factory);
  }

  @SPITest
  public void nullKeyThrowsException()
      throws CacheAccessException, IllegalAccessException, InstantiationException {
    final Store<K, V> kvStore = factory.newStore(
        new StoreConfigurationImpl<K, V>(factory.getKeyType(), factory.getValueType()));

    K key = null;
    V value = factory.getValueType().newInstance();

    try {
      kvStore.put(key, value);
      fail("Expected NullPointerException because the key is null");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @SPITest
  public void nullValueThrowsException()
      throws CacheAccessException, IllegalAccessException, InstantiationException {
    final Store<K, V> kvStore = factory.newStore(
        new StoreConfigurationImpl<K, V>(factory.getKeyType(), factory.getValueType()));

    K key = factory.getKeyType().newInstance();
    V value = null;

    try {
      kvStore.put(key, value);
      fail("Expected NullPointerException because the value is null");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @SPITest
  public void valueHolderCanBeRetrievedWithEqualKey()
      throws CacheAccessException, IllegalAccessException, InstantiationException {
    final Store<K, V> kvStore = factory.newStore(new StoreConfigurationImpl<K, V>(
        factory.getKeyType(), factory.getValueType(), null, Predicates.<Cache.Entry<K,V>>all(), null));

    K key = factory.getKeyType().newInstance();
    V value = factory.getValueType().newInstance();

    kvStore.put(key, value);

    assertThat(kvStore.get(key), is(instanceOf(Store.ValueHolder.class)));
  }

  @SPITest
  @SuppressWarnings("unchecked")
  public void wrongKeyTypeThrowsException()
      throws CacheAccessException, IllegalAccessException, InstantiationException {
    final Store kvStore = factory.newStore(
        new StoreConfigurationImpl<K, V>(factory.getKeyType(), factory.getValueType()));

    V value = factory.getValueType().newInstance();

    try {
      if (this.factory.getKeyType() == String.class) {
        kvStore.put(1.0f, value);
      } else {
        kvStore.put("key", value);
      }
      fail("Expected ClassCastException because the key is of the wrong type");
    } catch (ClassCastException e) {
      // expected
    }
  }

  @SPITest
  @SuppressWarnings("unchecked")
  public void wrongValueTypeThrowsException()
      throws CacheAccessException, IllegalAccessException, InstantiationException {
    final Store kvStore = factory.newStore(
        new StoreConfigurationImpl<K, V>(factory.getKeyType(), factory.getValueType()));

    K key = factory.getKeyType().newInstance();

    try {
      if (this.factory.getValueType() == String.class) {
        kvStore.put(key, 1.0f);
      } else {
        kvStore.put(key, "value");
      }
      fail("Expected ClassCastException because the value is of the wrong type");
    } catch (ClassCastException e) {
      // expected
    }
  }
}
