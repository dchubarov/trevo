package net.chubarov.trevo.util;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Вспомогательные методы для безопасной обработки строк и коллекций.
 *
 * @author Dmitry Chubarov
 * @since 1.0.1
 */
public final class NullSafe {

    /**
     * Проверяет, что переданная строка пуста.
     * @param s строка для проверки.
     * @return {@code true} если строка пуста или {@code null}, в противном случае {@code false}.
     */
    public static boolean isEmpty(String s) {
        return (s == null || s.isEmpty());
    }

    /**
     * Проверяет, что переданная строка не пуста.
     * @param s строка для проверки.
     * @return {@code true} если строка не {@code null} и не пуста, в противном случае {@code false}.
     */
    public static boolean nonEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * Проверяет, что переданная строка начинается с любого из перечисленных значений.
     * @param s строка для проверки.
     * @param patterns префиксы для проверки.
     * @return {@code true} если строка пуста или {@code null}, в противном случае {@code false}.
     */
    public static boolean startsWithAny(String s, String... patterns) {
        return (nonEmpty(s) && Stream.of(patterns).anyMatch(s::startsWith));
    }

    /**
     * Проверяет, что переданная карта пуста.
     * @param map карта для проверки.
     * @return {@code true} если карта пуста или {@code null}, в противном случае {@code false}.
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * Проверяет, что переданная карта не пуста.
     * @param map карта для проверки.
     * @return {@code true} если карта не {@code null} и содержит хотя бы один ключ, в противном случае {@code false}.
     */
    public static boolean nonEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /* Предотвращает создание экземпляров класса */
    private NullSafe() {}
}
