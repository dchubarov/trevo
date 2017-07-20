package net.chubarov.trevo.protocol;

import java.util.stream.Stream;

/**
 * Известные типы бизнес-запросов.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
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

    /**
     * Возвращает константу по имени запроса из спецификации протокола.
     * @return константа, соответствующая данному имени или {@code null} если соответствие не найдено.
     */
    public static ApiRequestType of(String text) {
        return Stream.of(values()).filter(v -> v.text.equals(text)).findFirst().orElse(null);
    }
}
