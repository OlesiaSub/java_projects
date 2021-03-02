package ru.hse.java.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DictionaryImpl<K, V> extends AbstractMap<K, V> implements Dictionary<K, V> {

    public static class Pair<T, U> implements Entry<T, U> {
        private final T key;
        private U val;
        private boolean deleted;

        Pair(T k, U v) {
            key = k;
            val = v;
            deleted = false;
        }

        @Override
        public T getKey() {
            return key;
        }

        @Override
        public U getValue() {
            return val;
        }

        @Override
        public U setValue(U newVal) {
            U prevVal = val;
            val = newVal;
            return prevVal;
        }

        public void setDeleted(boolean del) {
            deleted = del;
        }

        public boolean isDeleted() {
            return deleted;
        }
    }

    private int size;
    private int capacity;
    public final int initialCapacity;
    private final double lowerRehashBound;
    private final double upperRehashBound;
    private ArrayList<Pair<K, V>> hashTable;

    public DictionaryImpl(int capacity, double lowerRehashBound, double upperRehashBound) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Impossible capacity");
        }
        if (lowerRehashBound <= 0 || upperRehashBound <= 0 || lowerRehashBound > upperRehashBound) {
            throw new IllegalArgumentException("Impossible rehash bounds.");
        }
        this.capacity = capacity;
        initialCapacity = this.capacity;
        this.lowerRehashBound = lowerRehashBound;
        this.upperRehashBound = upperRehashBound;
        hashTable = new ArrayList<>(this.capacity);
        for (int i = 0; i < this.capacity; i++) {
            hashTable.add(null);
        }
    }

    public DictionaryImpl() {
        this(4, 0.65, 3);
    }

    private int getHashCode(Object elem) {
        return (elem.hashCode() % capacity + capacity) % capacity;
    }

    private boolean checkUpperRehash() {
        return size > capacity * lowerRehashBound;
    }

    private boolean checkLowerRehash() {
        return size < upperRehashBound * capacity;
    }

    private void increaseCapacity() {
        capacity *= 2;
    }

    private void decreaseCapacity() {
        capacity /= 2;
    }

    private void rehash(boolean upper) {
        ArrayList<Pair<K, V>> prevHashTable = hashTable;
        if (upper) {
            increaseCapacity();
        } else {
            decreaseCapacity();
        }
        size = 0;
        hashTable = new ArrayList<>();

        for (int i = 0; i < capacity; i++) {
            hashTable.add(null);
        }
        for (final Pair<K, V> pair : prevHashTable) {
            if (pair != null && !pair.isDeleted()) {
                put(pair.getKey(), pair.getValue());
            }
        }
    }

    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    @Override
    public boolean containsKey(Object key) {
        for (int i = getHashCode(key); ; i++) {
            if (hashTable.get(i) == null || hashTable.get(i).isDeleted()) {
                return false;
            } else if (hashTable.get(i).getKey().equals(key) && !hashTable.get(i).isDeleted()) {
                return true;
            }
        }
    }

    @Override
    public V get(Object key) {
        for (int i = getHashCode(key); ; i++) {
            if (hashTable.get(i) == null || hashTable.get(i).isDeleted()) {
                return null;
            } else if (hashTable.get(i).getKey().equals(key) && !hashTable.get(i).isDeleted()) {
                return hashTable.get(i).getValue();
            }
        }
    }

    @Override
    public V put(K key, V value) {
        int code = getHashCode(key);
        for (int i = code; ; i++) {
            if (i >= capacity) {
                return null;
            }
            if (hashTable.get(i) == null || hashTable.get(i).isDeleted()) {
                hashTable.set(i, new Pair<>(key, value));
                hashTable.get(i).setDeleted(false);
                size++;
                if (checkUpperRehash()) {
                    rehash(true);
                }
                return null;
            } else if (hashTable.get(i).getKey().equals(key) && !hashTable.get(i).isDeleted()) {
                V prevValue = hashTable.get(i).getValue();
                hashTable.set(i, new Pair<>(key, value));
                return prevValue;
            }
        }
    }

    @Override
    public V remove(Object key) {
        for (int i = getHashCode(key); ; i++) {
            if (i >= capacity || hashTable.get(i) == null || hashTable.get(i).isDeleted()) {
                return null;
            } else if (hashTable.get(i).getKey().equals(key) && !hashTable.get(i).isDeleted()) {
                V tmp = hashTable.get(i).getValue();
                hashTable.get(i).setDeleted(true);
                size--;
                if (checkLowerRehash()) {
                    rehash(false);
                }
                return tmp;
            }
        }
    }

    @Override
    public void clear() {
        hashTable = new ArrayList<>();
        size = 0;
        capacity = initialCapacity;
    }

    private abstract class MyIterator {
        private final Iterator<Pair<K, V>> iter;

        MyIterator() {
            iter = hashTable.iterator();
        }

        public boolean hasNext() {
            if (iter != null) {
                while (iter.hasNext()) {
                    if (iter.next() != null) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Entry<K, V> getNext() {
            if (hasNext()) {
                return iter.next();
            } else {
                throw new NoSuchElementException("Next element does not exist.");
            }
        }

        public void remove() {
            iter.remove();
            DictionaryImpl.this.size--;
        }
    }

    private class MyIteratorEntrySet extends MyIterator implements Iterator<Entry<K, V>> {
        @Override
        public Entry<K, V> next() {
            return getNext();
        }
    }

    private class MyIteratorKeySet extends MyIterator implements Iterator<K> {
        @Override
        public K next() {
            return getNext().getKey();
        }
    }

    private class MyIteratorValues extends MyIterator implements Iterator<V> {
        @Override
        public V next() {
            return getNext().getValue();
        }
    }

    private class MyKeySet extends AbstractSet<K> {

        @Override
        public @NotNull Iterator<K> iterator() {
            return new MyIteratorKeySet();
        }

        @Override
        public int size() {
            return DictionaryImpl.this.size();
        }
    }

    private class MyValues extends AbstractCollection<V> {

        @Override
        public @NotNull Iterator<V> iterator() {
            return new MyIteratorValues();
        }

        @Override
        public int size() {
            return DictionaryImpl.this.size();
        }
    }

    private class MyPairSet extends AbstractSet<Entry<K, V>> {

        @Override
        public @NotNull Iterator<Entry<K, V>> iterator() {
            return new MyIteratorEntrySet();
        }

        @Override
        public int size() {
            return DictionaryImpl.this.size();
        }
    }

    @Override
    public @NotNull Set<K> keySet() {
        return new MyKeySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return new MyValues();
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return new MyPairSet();
//        return null;
    }
}
