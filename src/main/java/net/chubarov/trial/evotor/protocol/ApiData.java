package net.chubarov.trial.evotor.protocol;

import java.util.*;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public abstract class ApiData {
    private Map<String, String> properties;

    public Collection<String> getPropertyNames() {
        return (properties != null ? Collections.unmodifiableSet(properties.keySet()) : Collections.emptyList());
    }

    public String getProperty(String name) {
        return (properties != null ? properties.get(name) : null);
    }

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
