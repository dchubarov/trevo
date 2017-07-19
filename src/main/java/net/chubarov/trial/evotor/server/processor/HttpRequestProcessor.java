package net.chubarov.trial.evotor.server.processor;

import net.chubarov.trial.evotor.server.ToyServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Обрабочик запросов реализующий подмножество протокола HTTP.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public abstract class HttpRequestProcessor extends LineRequestProcessor {

    // названия заголовков HTTP
    private static final String CONTENT_LENGTH = "content-length";
    static final String CONTENT_TYPE = "Content-Type";
    private static final String CONNECTION = "Connection";
    private static final String SERVER = "Server";
    private static final String DATE = "Date";

    // значения по умолчанию для некоторых заголовков
    private static final String SERVER_HEADLINE = "Toy Server Built 2017/07";
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";
    private static final String CONNECTION_MODE_CLOSE = "close";

    /**
     * Дочерний класс должен реализовать этот метод для обработки данных HTTP-запроса.
     *
     * @param server
     * @param requestHeaders карта содержащая заголовки запроса.
     * @param requestBody строка содержащая тело запроса или {@code null}.
     * @param responseHeaders карта в которую следует поместить заголовки ответа при необходимости.
     * @param responseBody нулевой элемент массива заполняется ссылкой на строку содержащую тело ответа.
     * @return одно из значение {@link HttpStatus}, метод не должен возвращать {@code null}.
     */
    protected abstract HttpStatus processHttpRequest(ToyServer server, Map<String, String> requestHeaders,
            String requestBody, Map<String, String> responseHeaders, String[] responseBody);

    @Override
    protected final void processRequest(ToyServer server, BufferedReader requestReader,
            BufferedWriter responseWriter) throws IOException {

        HttpStatus httpStatus;
        Map<String, String> requestHeaders = new HashMap<>(), responseHeaders = null;
        String[] responseBuffer = new String[1];
        String requestBody = null;

        if (readRequestHeaders(requestReader, requestHeaders) != null) {
            // проверяем был ли передан Content-Length в запросе
            int contentLength = -1;
            try {
                contentLength = Integer.parseInt(requestHeaders.get(CONTENT_LENGTH));
            } catch (NumberFormatException ignore) {}

            // считываем тело запроса (для простоты считаем что передается строка)
            if (contentLength > 0) {
                requestBody = readRequestBody(requestReader, contentLength);
            }

            // вызываем обработчик запроса более высокого уровня (processHttpRequest)
            responseHeaders = new HashMap<>();
            httpStatus = processHttpRequest(server, requestHeaders, requestBody, responseHeaders, responseBuffer);

            // если реализация обработчика не вернула корректный статус отправляем ошибку сервера без содержимого
            if (httpStatus == null) {
                httpStatus = HttpStatus.SERVER_ERROR;
                responseBuffer[0] = null;
            }
        } else {
            // некорректный запрос (первая строка не содержит метода GET/POST)
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        // отправляем ответ клиенту
        writeResponse(responseWriter, httpStatus, responseHeaders, responseBuffer[0]);
    }

    private String readRequestHeaders(BufferedReader requestReader,
            Map<String, String> requestHeaders) throws IOException {
        // прочитать строку запроса
        String cmd = requestReader.readLine();
        if (cmd == null || cmd.isEmpty() || !(cmd.startsWith("GET") || cmd.startsWith("POST"))) {
            return null;
        }

        // прочитать заголовки
        String inputLine;
        do {
            inputLine = requestReader.readLine();
            if (inputLine != null) {
                String headerKey, headerValue = null;
                int delimiterIndex = inputLine.indexOf(":");
                if (delimiterIndex < 0) {
                    headerKey = inputLine;
                } else {
                    headerKey = inputLine.substring(0, delimiterIndex).toLowerCase().trim();
                    headerValue = inputLine.substring(delimiterIndex + 1).trim();
                }
                requestHeaders.put(headerKey, headerValue);
            }
        } while (inputLine != null && !inputLine.isEmpty());

        return cmd;
    }

    private String readRequestBody(BufferedReader requestReader, int contentLength) throws IOException {
        char[] buf = new char[contentLength];
        if (contentLength == requestReader.read(buf)) {
            return new String(buf);
        }
        return null;
    }

    private void writeResponse(BufferedWriter responseWriter, HttpStatus httpStatus,
            Map<String, String> responseHeaders, String responseBody) throws IOException {

        // отправляем строку состояния HTTP
        responseWriter.write(httpStatus.toString());
        responseWriter.newLine();

        // формируем объединенную карту заголовков
        Map<String, String> actualHeaders = getPredefinedResponseHeaders();
        if (responseHeaders != null) actualHeaders.putAll(responseHeaders);

        // при наличии тела ответа мы должны отправить его размер и тип содержимого
        if (responseBody != null) {
            actualHeaders.put(CONTENT_LENGTH, Integer.toString(responseBody.length()));
            if (!actualHeaders.containsKey(CONTENT_TYPE)) {
                actualHeaders.put(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
            }
        }

        // отправляем заголовки клиенту
        for (Map.Entry<String, String> e : actualHeaders.entrySet()) {
            responseWriter.write(e.getKey() + ":" + e.getValue());
            responseWriter.newLine();
        }

        // тело ответа должно быть отделено пустой строкой
        responseWriter.newLine();

        // отправляем тело ответа клиента
        if (responseBody != null && !responseBody.isEmpty()) {
            responseWriter.write(responseBody);
        }
    }

    private Map<String, String> getPredefinedResponseHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(DATE, ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        headers.put(CONNECTION, CONNECTION_MODE_CLOSE);
        headers.put(SERVER, SERVER_HEADLINE);
        return headers;
    }
}
