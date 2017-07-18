package net.chubarov.trial.evotor.protocol;

import java.util.Objects;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class ApiResponse extends ApiData {
    private final ApiErrorCode errorCode;

    public ApiResponse(ApiErrorCode errorCode) {
        this.errorCode = Objects.requireNonNull(errorCode);
    }

    public String toXml() {
        String lf = System.getProperty("line.separator", "\n");
        StringBuilder xmlBuilder = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"utf-8\">").append(lf)
                .append("<response>").append(lf)
                .append("  <result-code>").append(errorCode.getCode()).append("</result-code>").append(lf);

        for (String n : getPropertyNames()) {
            xmlBuilder.append("  <extra name=\"").append(n).append("\">")
                    .append(getProperty(n)).append("</extra>").append(lf);
        }

        return xmlBuilder.append("</response>").append(lf).toString();
    }
}
