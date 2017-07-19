package net.chubarov.trevo.protocol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Тесты разбора бизнес-запросов.
 *
 * @author Dmitry Chubarov
 */
public class ApiRequestParserTest {

    @Test
    public void testFineRegistrationDocument() {
        String xml =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<request>" +
                "  <request-type>CREATE-AGT</request-type>" +
                "  <extra name=\"login\">123456</extra>" +
                "  <extra name=\"password\">pwd</extra>" +
                "</request>";

        ApiRequest request = ApiRequestParser.parse(xml);

        assertNotNull(request);
        assertEquals(ApiRequestType.CREATE, request.getRequestType());
        assertEquals("123456", request.getProperty("login"));
        assertEquals("pwd", request.getProperty("password"));
    }

    @Test
    public void testFineBalanceDocument() {
        String xml =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<request>" +
                        "  <request-type>GET-BALANCE</request-type>" +
                        "  <extra name=\"login\">123456</extra>" +
                        "  <extra name=\"password\">pwd</extra>" +
                        "</request>";

        ApiRequest request = ApiRequestParser.parse(xml);

        assertNotNull(request);
        assertEquals(ApiRequestType.QUERY_BALANCE, request.getRequestType());
        assertEquals("123456", request.getProperty("login"));
        assertEquals("pwd", request.getProperty("password"));
    }
}
