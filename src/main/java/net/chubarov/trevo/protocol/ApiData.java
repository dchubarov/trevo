package net.chubarov.trevo.protocol;

import java.util.*;

/**
 * Абстрактный объект данных -- контейнер дополнительных свойств.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public abstract class ApiData {
    private Map<String, String> properties;

    /**
     * @return коллекцию имен всех добавленных свойств
     */
    public Collection<String> getPropertyNames() {
        return (properties != null ? Collections.unmodifiableSet(properties.keySet()) : Collections.emptyList());
    }

    /**
     * Возвращает значение свойства по его имени.
     * @param name имя свойства.
     * @return значение свойства или {@code null} если свойство не существует.
     */
    public String getProperty(String name) {
        return (properties != null ? properties.get(name) : null);
    }

    /**
     * Помещает свойство и его значение в контейнер.
     * @param name имя свойства, не может быть {@code null}.
     * @param value значение свойства (допускается {@code null}).
     * @return предыдущее значение свойства или {@code null} если свойство ранее отсутствовало.
     */
    public String putProperty(String name, String value) {
        Objects.requireNonNull(name);
        String oldValue = null;
        if (properties != null) {
            oldValue = properties.get(name);
        } else {
            properties = new HashMap<>();
        }
        properties.put(name, value);
        return oldValue;
    }
}
