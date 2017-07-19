package net.chubarov.trevo.server.processor;

/**
 * Используемые коды состояния HTTP с числовыми кодами и сообщениями,
 * соответствующими спецификации W3C.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public enum HttpStatus {

    /** Запрос обработан успешно. */
    OK(200, "OK"),

    /** Некорректный или поврежденный запрос. */
    BAD_REQUEST(400, "Bad Request"),

    /** Внутренняя ошибка сервера. */
    SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String text;

    HttpStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @Override
    public String toString() {
        return "HTTP/1.1 " + code + " " + text;
    }
}
