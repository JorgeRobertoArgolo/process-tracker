package com.jorgeroberto.study.process_tracker.shared.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class Registry<K, V> {

    private final Map<K, V> entries;

    private Registry(final Map<K, V> entries) {
        this.entries = entries;
    }

    public static <K, V> Registry<K, V> of(
            final Collection<? extends V> values,
            final Function<? super V, ? extends K> keyExtractor
    ){
        HashMap<K, V> map = new HashMap<>();
        for (final V value : values){
            final K key = keyExtractor.apply(value);
            if (map.containsKey(key)){
                throw new IllegalArgumentException("Chave duplicada no registro: " + key);
            }
            map.put(key, value);
        }
        return new Registry<>(Map.copyOf(map));
    }

    public Optional<V> find(final K key){
        return Optional.ofNullable(entries.get(key));
    }

}
