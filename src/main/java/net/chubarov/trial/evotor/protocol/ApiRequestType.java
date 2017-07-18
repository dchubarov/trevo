package net.chubarov.trial.evotor.protocol;

import java.util.stream.Stream;

/**
 * <p>TODO add documentation...</p>
 * @author Dmitry Chubarov
 */
public enum ApiRequestType {

    /** Создание нового клиента */
    CREATE("CREATE-AGT"),

    /** Запрос текущего баланса */
    QUERY_BALANCE("GET-BALANCE");

    private final String text;

    ApiRequestType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ApiRequestType of(String text) {
        return Stream.of(values()).filter(v -> v.text.equals(text)).findFirst().orElse(null);
    }
}
