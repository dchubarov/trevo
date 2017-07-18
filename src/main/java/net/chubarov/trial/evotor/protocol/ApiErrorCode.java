package net.chubarov.trial.evotor.protocol;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public enum ApiErrorCode {

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

    ApiErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
