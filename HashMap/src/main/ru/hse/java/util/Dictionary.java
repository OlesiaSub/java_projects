package ru.hse.java.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/*
 * Хеш-таблица, использующая список
 * Стандартный способ получить хеш объекта -- вызвать у него метод hashCode()
 */
public interface Dictionary<K, V> extends Map<K, V> {
    // кол-во ключей в таблице
    @Override
    int size();

    /*
     * true, если такой ключ содержится в таблице, иначе false
     */
    @Override
    boolean containsKey(Object key);

    /* Возвращает значение, хранимое по ключу key. Если такого нет, возвращает null
     */
    @Override
    V get(Object key);

    /*
     * Положить по ключу key значение value и вернуть ранее хранимое, либо null
     * Провести рехеширование по необходимости
     */
    @Override
    V put(K key, V value);

    /*
     * Забыть про пару key-value для переданного key
     * и вернуть забытое value, либо null, если такой пары не было;
     * провести рехеширование по необходимости
     */
    @Override
    V remove(Object key);

    // забыть про все пары key-value
    @Override
    void clear();

    /*
     * Все коллекции из трёх методов ниже должны поддерживать только те методы коллекции/множества,
     *   которые не меняют исходную коллекцию.
     * Также они должны поддерживать метод iterator(), который возвращает итератор, у которого работает
     *   метод remove(). Если у этого итератора вызвать remove(), то исходный Dictionary должен измениться
     */

    // вернуть множество ключей словаря
    @Override
    @NotNull
    Set<K> keySet();

    /*
     * Вернуть коллекцию из всех значений
     * Если под разными ключами лежат одинаковые значения, то все повторения
     *   должны быть в этой коллекции. Например, для словаря { "": 1, "a": 1, "b": 0 }
     *   коллекция values() выглядит, например, так: [0, 1, 1]
     * Порядок значений не определён
     */
    @Override
    @NotNull
    Collection<V> values();

    // Вернуть множество всех пар ключ-значение, которые хранятся в словаре
    @Override
    @NotNull
    Set<Entry<K, V>> entrySet();
}
