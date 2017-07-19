package net.chubarov.trevo.protocol;

/**
 * Объект данных бизнес-запроса, содержащий тип запроса и (необязательно) дополнительные свойства.
 *
 * @see ApiRequestType
 * @see ApiRequestParser
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class ApiRequest extends ApiData {
    private ApiRequestType requestType;

    /**
     * @return тип запроса.
     */
    public ApiRequestType getRequestType() {
        return requestType;
    }

    /**
     * Устанавливает тип запроса.
     * @param requestType новый тип запроса.
     */
    public void setRequestType(ApiRequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { " + requestType + " }";
    }
}
