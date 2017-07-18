package net.chubarov.trial.evotor.protocol;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class ApiRequest extends ApiData {
    private ApiRequestType requestType;

    public ApiRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(ApiRequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { " + requestType + " }";
    }
}
