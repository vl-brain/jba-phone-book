package phonebook;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class HashTable<K, V> {
    private static final int SCALING_FACTOR = 2;
    private int size;
    private TableEntry<K, V>[] table;

    @SuppressWarnings("unchecked")
    public HashTable(int size) {
        this.size = size;
        table = new TableEntry[size];
    }

    public V get(K key) {
        final int idx = findIndex(key);
        if (idx < 0 || table[idx] == null) {
            return null;
        }
        return table[idx].getValue();
    }

    public boolean put(K key, V value) {
        int idx = findIndex(key);
        if (idx < 0) {
            resize();
            idx = findIndex(key);
        }
        table[idx] = new TableEntry<>(key, value);
        return true;
    }

    public Set<TableEntry<K, V>> entrySet() {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private int findIndex(K key) {
        final int startIdx = Integer.remainderUnsigned(key.hashCode(), size);
        int idx = startIdx;
        while (!(table[idx] == null || table[idx].getKey().equals(key))) {
            idx = (idx + 1) % size;
            if (idx == startIdx) {
                return -1;
            }
        }
        return idx;
    }

    private void resize() {
        final HashTable<K, V> hashTable = new HashTable<>(size * SCALING_FACTOR);
        for (TableEntry<K, V> entry : table) {
            hashTable.put(entry.getKey(), entry.getValue());
        }
        size = hashTable.size;
        table = hashTable.table;
    }

    public static class TableEntry<K, V> {
        private final K key;
        private final V value;

        public TableEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}

