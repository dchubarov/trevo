package net.chubarov.trial.evotor.protocol;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class ApiResponse extends ApiData {
    private ApiErrorCode errorCode = ApiErrorCode.OK;

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ApiErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String toXml() {

        String lf = System.getProperty("line.separator", "\n");
        StringBuilder xmlBuilder = new StringBuilder()
                .append("<response>").append(lf)
                .append("  <result-code>").append(errorCode.getCode()).append("</result-code>").append(lf);

        for (String n : getPropertyNames()) {
            xmlBuilder.append("  <extra name=\"").append(n).append("\">")
                    .append(getProperty(n)).append("</extra>").append(lf);
        }

        return xmlBuilder.append("</response>").append(lf).toString();
    }
}
