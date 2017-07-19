package net.chubarov.trevo.protocol;

/**
 * Возможные коды состояния и их числовые значения, соответствующие спецификации протокола обмена.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public enum ApiStatusCode {

    /** Успешное завершение */
    OK(0),

    /** Объект уже существует */
    ALREADY_EXISTS(1),

    /** Техническая ошибка */
    TECHNICAL_ERROR(2),

    /** Пользователь не существует */
    NOT_EXISTS(3),

    /** Неверный пароль */
    INVALID_PASSWORD(4);

    private final int code;

    ApiStatusCode(int code) {
        this.code = code;
    }

    /**
     * @return числовой код
     */
    public int getCode() {
        return code;
    }
}
