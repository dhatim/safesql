package org.safesql.connection;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public interface Result<T> {

    T toObject();
    Optional<T> toOptional();
    Stream<T> stream();
    List<T> toList();
    Set<T> toSet();
    void forEach(Consumer<T> consumer);
    <R, A> R collect(Collector<? super T, A, R> collector);

    <U> Result<U> map(Function<T, U> mapper);
    Result<T> peek(Consumer<T> consumer);

}
