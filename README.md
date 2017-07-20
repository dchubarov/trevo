
# Trevo HTTP Server

**Trevo** - лабораторный проект, представляющий собой простейший
многопоточный HTTP-сервер, принимающий и обрабатывающий XML-запросы
определенного вида. Ядро сервера (класс **NetworkServer**) написано с
использованием исключительно стандартной библиотеки Java SE (java.net,
java.io, и т.п.).

Сервер обладает следующими возможностями:

* Поддержка нескольких портов, реализующих различные протоколы.
    * Простейший протокол администрирования, завершающий работу сервера по получению команды QUIT.
    * Минимальная реализация протокола HTTP, обрабатывающая POST-запросы, содержащие XML-данные.
* Многопоточная обработка запросов клиентов с помощью пула исполнителей.
* Поддержка пула JDBC-соединений.
* Корректное завершение работы при остановке JVM, вызванной 
  сигналом (SIGKILL, SIGHUP).

## Установка

### База данных
Для создания необходимой структуры базы данных Oracle запустите 
скрипт `src/main/scripts/CreateDatabase_Oracle.sql`. 

Пользователь Oracle должен обладать привелегиями на создание 
объектов (таблиц, последовательностей, пакетов).

При наличии объектов в схеме базы данных они будут предварительно
удалены скриптом.

### Файл конфигурации
Создайте файл `server.properties` и заполните его
необходимыми параметрами. В качестве шаблона можно использовать
поставляемый `server.properties.sample`.

Каждый экземпляр сервера может поддерживать произвольное количество
слушателей бизнес-запросов. Для этого свойство `server.port.api`
может содержать несколько номеров портов через запятую.

Экземпляр сервера может поддерживать только один порт аднистративного
протокола (свойство `server.port.admin`).

Требуется указать корректные параметры БД Oracle для существующей
схемы данных, проинициализированной скриптом `CreateDatabase_Oracle.sql`.

### Сборка сервера
Требует Maven версии 3.x. Сборка приложения осуществляется командой:

```
mvn clean package
```

### Запуск сервера
Сервер запускается как Java-приложение в домашней директории
проекта.

```
java -cp lib/ojdbc6-11.2.0.4.jar:target/trevo-1.0.0-SNAPSHOT.jar \
    net.chubarov.trevo.Bootstrap [<config-path>] 
```

где *config-path* - путь к файлу конфигурации. Если параметр не
указан, сервер будет искать файл **server.properties** в директории
запуска.

При необходимости запускать несколько экземпляров сервера
необходимо создать отдельный конфигурационный файл для каждого
сервера и указывать путь к этому файлу в качестве параметра при
запуске приложения. Порты при этом не должны пересекаться.

### Отправка запросов серверу
Простейший способ отправить запрос - команда **cURL**. Например:

```
curl -X POST \
  http://localhost:8081/ \
  -H 'cache-control: no-cache' \
  -H 'content-type: text/xml' \
  -d '<?xml version="1.0" encoding="utf-8" ?>
<request>
	<request-type>CREATE-AGT</request-type>
	<extra name="login">test</extra>
	<extra name="password">test</extra>
</request>'
```

Коллекция запросов для [Postman](https://www.getpostman.com)
находится в файле `src/main/scripts/tests-postman.json`.

### Нагрузочное тестирование

Минимальный нагрузочный тест для **Apache JMeter** находится в
файле `src/main/scripts/load-test.jmx`. Тестовый сценарий требует
перенастройки на актуальные порты запущенного сервера. По умолчанию
тест ожидает открытые порты для бизнес-запросов 8081-8086.

### Остановка сервера
Корректная остановка сервера (с ожиданием завершения всех процессов)
производится отправкой команды `QUIT` в порт административного 
протокола. Например, можно подключиться командой **telnet**, набрать
**QUIT** и нажать ввод:

```
$ telnet localhost 9051
Connected to localhost.
Escape character is '^]'.
QUIT
BYE
Connection closed by foreign host.
```
