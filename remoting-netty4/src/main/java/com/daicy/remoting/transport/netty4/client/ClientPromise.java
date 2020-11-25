/**
 * Copyright (c) 2013-2020 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.daicy.remoting.transport.netty4.client;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

/**
 * 
 * @author Nikita Koksharov
 *
 * @param <T> type of object
 */
public class ClientPromise<T> implements Promise<T> {

    private final Promise<T> promise = ImmediateEventExecutor.INSTANCE.newPromise();

    public ClientPromise() {
    }
    
    public static <V> ClientPromise<V> newFailedFuture(Throwable cause) {
        ClientPromise<V> future = new ClientPromise<V>();
        future.tryFailure(cause);
        return future;
    }

    public static <V> ClientPromise<V> newSucceededFuture(V result) {
        ClientPromise<V> future = new ClientPromise<V>();
        future.trySuccess(result);
        return future;
    }

    @Override
    public boolean isSuccess() {
        return promise.isSuccess();
    }

    @Override
    public boolean isCancellable() {
        return promise.isCancellable();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return promise.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(timeout,unit);
    }

    @Override
    public boolean isCancelled() {
        return promise.isCancelled();
    }

    @Override
    public Promise<T> setSuccess(T result) {
        return promise.setSuccess(result);
    }

    @Override
    public boolean trySuccess(T result) {
        return promise.trySuccess(result);
    }

    @Override
    public Promise<T> setFailure(Throwable cause) {
        return promise.setFailure(cause);
    }

    @Override
    public Throwable cause() {
        return promise.cause();
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        return promise.tryFailure(cause);
    }

    @Override
    public boolean setUncancellable() {
        return promise.setUncancellable();
    }

    @Override
    public Promise<T> addListener(GenericFutureListener<? extends Future<? super T>> listener) {
        return promise.addListener(listener);
    }

    @Override
    public Promise<T> addListeners(GenericFutureListener<? extends Future<? super T>>... listeners) {
        return promise.addListeners(listeners);
    }

    @Override
    public Promise<T> removeListener(GenericFutureListener<? extends Future<? super T>> listener) {
        return promise.removeListener(listener);
    }

    @Override
    public Promise<T> removeListeners(GenericFutureListener<? extends Future<? super T>>... listeners) {
        return promise.removeListeners(listeners);
    }

    @Override
    public Promise<T> await() throws InterruptedException {
        promise.await();
        return this;
    }

    @Override
    public Promise<T> awaitUninterruptibly() {
        promise.awaitUninterruptibly();
        return this;
    }

    @Override
    public Promise<T> sync() throws InterruptedException {
        promise.sync();
        return this;
    }

    @Override
    public Promise<T> syncUninterruptibly() {
        promise.syncUninterruptibly();
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return promise.await(timeout, unit);
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return promise.await(timeoutMillis);
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return promise.awaitUninterruptibly(timeout, unit);
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        return promise.awaitUninterruptibly(timeoutMillis);
    }

    @Override
    public T getNow() {
        return promise.getNow();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(mayInterruptIfRunning);
    }

    public void onComplete(BiConsumer<? super T, ? super Throwable> action) {
        promise.addListener(f -> {
            if (!f.isSuccess()) {
                action.accept(null, f.cause());
                return;
            }

            action.accept((T) f.getNow(), null);
        });
    }


    @Override
    public String toString() {
        return "ClientPromise [promise=" + promise + "]";
    }


}
