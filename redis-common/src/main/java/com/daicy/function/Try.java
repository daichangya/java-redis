package com.daicy.function;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.function
 * @date:11/10/20
 */
public interface Try<T> {

    static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    static <T> Try<T> failure(String message) {
        return failure(new Exception(message));
    }

    static <T> Try<T> failure() {
        return failure(new Exception());
    }

    static <T> Try<T> failure(Throwable error) {
        return new Failure<>(error);
    }

    static <T> Try<T> of(Callable<T> callable) {
        try {
            return success(callable.call());
        } catch (Throwable error) {
            return failure(error);
        }
    }


    default Try<T> recover(Function<Throwable, T> mapper) {
        if (isFailure()) {
            return Try.of(() -> mapper.apply(getCause()));
        }
        return this;
    }


    Throwable getCause();

    boolean isSuccess();

    boolean isFailure();

    /**
     * Returns the value if available. If not, it throws {@code NoSuchElementException}
     *
     * @return the wrapped value
     * @throws NoSuchElementException if value is not available
     */
    T get();

    final class Success<T> implements Try<T>, Serializable {

        private static final long serialVersionUID = -3934628369477099278L;

        private final T value;

        private Success(T value) {
            this.value = Preconditions.checkNotNull(value);
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Throwable getCause() {
            throw new NoSuchElementException("success doesn't have any cause");
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Success(" + value + ")";
        }
    }

    final class Failure<T> implements Try<T>, Serializable {

        private static final long serialVersionUID = -8155444386075553318L;

        private final Throwable cause;

        private Failure(Throwable cause) {
            this.cause = Preconditions.checkNotNull(cause);
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T get() {
            throw new NoSuchElementException("failure doesn't have any value");
        }

        @Override
        public Throwable getCause() {
            return cause;
        }

        private String getMessage() {
            return cause.getMessage();
        }

        private StackTraceElement[] getStackTrace() {
            return cause.getStackTrace();
        }

        @Override
        public int hashCode() {
            return Objects.hash(cause.getMessage(), cause.getStackTrace());
        }

        @Override
        public String toString() {
            return "Failure(" + cause + ")";
        }
    }
}