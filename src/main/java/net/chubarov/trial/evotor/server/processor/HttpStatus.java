package net.chubarov.trial.evotor.server.processor;

/**
 * <p>TODO add documentation...</p>
 * @author Dmitry Chubarov
 */
public enum HttpStatus {

    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String text;

    HttpStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "HTTP/1.1 " + code + " " + text;
    }
}
