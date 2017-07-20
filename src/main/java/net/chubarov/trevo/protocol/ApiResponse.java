package net.chubarov.trevo.protocol;

/**
 * Объект данных бизнес-ответа, содержащий код ответа и (необязательно) дополнительные свойства.
 *
 * <p>Имеет вспомогательный метод {@link #toXml()} преобразующий объект в его текстовое
 * представление в XML-формате.</p>
 *
 * @see ApiStatusCode
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class ApiResponse extends ApiData {
    private ApiStatusCode errorCode = ApiStatusCode.OK;

    /**
     * @return код состояния
     */
    public ApiStatusCode getStatusCode() {
        return errorCode;
    }

    /**
     * Устанавливает код состояния для этого объекта.
     * @param code новый код состояния.
     */
    public void setStatusCode(ApiStatusCode code) {
        this.errorCode = code;
    }

    /**
     * @return XML-представление данного объекта.
     */
    public String toXml() {
        String lf = System.lineSeparator();
        StringBuilder xmlBuilder = new StringBuilder()
                .append("<response>").append(lf)
                .append("  <result-code>").append(errorCode.getCode())
                .append("</result-code>").append(lf);

        getPropertyNames().forEach((n) -> xmlBuilder
                .append("  <extra name=\"").append(n).append("\">").append(getProperty(n))
                .append("</extra>").append(lf));

        return xmlBuilder.append("</response>").append(lf).toString();
    }
}
