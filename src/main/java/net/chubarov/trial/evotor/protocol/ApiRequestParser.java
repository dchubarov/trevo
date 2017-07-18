package net.chubarov.trial.evotor.protocol;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public final class ApiRequestParser {
    private static final Logger logger = Logger.getLogger(ApiRequestParser.class.getSimpleName());

    /** Предотвращает создание экземпляров класса */
    private ApiRequestParser() {}

    private static final ThreadLocal<SAXParser> threadLocalParser = new ThreadLocal<SAXParser>() {
        @Override
        protected SAXParser initialValue() {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            try {
                return parserFactory.newSAXParser();
            } catch (ParserConfigurationException | SAXException e) {
                logger.log(Level.SEVERE, "Ошибка создания анализатора XML.", e);
            }
            return super.initialValue();
        }
    };

    public static ApiRequest parse(String xml) {
        try {
            InputSource xmlSource = new InputSource(new StringReader(xml));
            RequestHandler handler = new RequestHandler();
            threadLocalParser.get().parse(xmlSource, handler);
            return handler.getRequest();
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Ошибка при разборе XML-запроса", e);
        }
        return null;
    }

    private static class RequestHandler extends DefaultHandler {
        private ApiRequest request;
        private StringBuilder text;
        private String attr;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            text = new StringBuilder();
            attr = null;

            switch (qName) {
                case "request":
                    request = new ApiRequest();
                    break;

                case "request-type":
                    text = new StringBuilder();
                    break;

                case "extra":
                    attr = attributes.getValue("name");
                    break;

            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName) {
                case "request-type":
                    if (request != null) {
                        request.setRequestType(ApiRequestType.of(text.toString()));
                    }
                    break;

                case "extra":
                    if (attr != null && request != null) {
                        request.putProperty(attr, (text != null ? text.toString() : null));
                    }
                    break;
            }
        }

        @Override
        public void endDocument() throws SAXException {
            if (request == null || request.getRequestType() == null) {
                throw new SAXException("Некорректная структура запроса.");
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (text != null) {
                text.append(ch, start, length);
            }
        }

        ApiRequest getRequest() {
            return request;
        }
    }
}
