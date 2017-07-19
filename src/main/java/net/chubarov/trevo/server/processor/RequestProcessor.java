package net.chubarov.trevo.server.processor;

import net.chubarov.trevo.server.ToyServer;

import java.io.IOException;
import java.net.Socket;

/**
 * Обработчик клиентского подключения отвечающий за чтение данных из клиентского сокета,
 * интерпретацию данных и отправку ответа.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public interface RequestProcessor {

    /**
     * Метод вызывается сервером при необходимости обслужить запрос клиента.
     * @param server сервер, которому поступил запрос.
     * @param socket клиентский сокет для считывания данных и отсылки ответа.
     * @throws IOException если в процессе чтения/записи произошла ошибка.
     */
    void process(ToyServer server, Socket socket) throws IOException;

}
