package net.glasslauncher.glassbrigadier.impl.permission;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class BasicEntry<T, V>  implements Map.Entry<T, V> {
    private T key;
    private V value;

    @Override
    public T getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        return this.value = value;
    }
}