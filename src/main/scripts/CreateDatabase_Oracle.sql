
--------------------------------------------------------
--  Sequence: CLIENT_SEQ
--------------------------------------------------------
BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE CLIENT_SEQ';
  EXCEPTION
  WHEN OTHERS THEN
  IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;

CREATE SEQUENCE CLIENT_SEQ MINVALUE 1 MAXVALUE 9999999999
  INCREMENT BY 1 ORDER NOCACHE NOCYCLE;

--------------------------------------------------------
--  Table: CLIENT
--------------------------------------------------------
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE CLIENT';
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE != -942 THEN
      RAISE;
    END IF;
END;

CREATE TABLE CLIENT (
  CLIENT_ID     NUMBER(10)        NOT NULL
, LOGIN         VARCHAR2(64 CHAR) NOT NULL
, PASSWORD      VARCHAR2(64 CHAR) NOT NULL
, BALANCE       NUMBER(10, 4)     DEFAULT 0 NOT NULL
, CREATE_STAMP  TIMESTAMP(3)      DEFAULT current_timestamp NOT NULL
);

COMMENT ON TABLE  CLIENT              IS 'Таблица клиентов.';
COMMENT ON COLUMN CLIENT.CLIENT_ID    IS 'Внутренний числовой идентификатор клиента.';
COMMENT ON COLUMN CLIENT.LOGIN        IS 'Регистрационное имя клиента.';
COMMENT ON COLUMN CLIENT.PASSWORD     IS 'Пароль клиента.';
COMMENT ON COLUMN CLIENT.BALANCE      IS 'Баланс личного счета.';
COMMENT ON COLUMN CLIENT.CREATE_STAMP IS 'Дата создания.';

ALTER TABLE CLIENT ADD CONSTRAINT CLIENT_PK PRIMARY KEY (CLIENT_ID)
  USING INDEX (CREATE UNIQUE INDEX IX_CLIENT_PK ON CLIENT (CLIENT_ID));

CREATE UNIQUE INDEX IX_CLIENT_LOGIN ON CLIENT (LOGIN);

--------------------------------------------------------
--  Function: CREATE_CLIENT
--------------------------------------------------------
CREATE OR REPLACE PACKAGE CLIENT_PKG IS
  PROCEDURE register (login IN VARCHAR2, pwd IN VARCHAR2, result OUT NUMBER);
END;
/

CREATE OR REPLACE PACKAGE BODY CLIENT_PKG IS
  PROCEDURE register (login IN VARCHAR2, pwd IN VARCHAR2, result OUT NUMBER) IS
  BEGIN
    SELECT CLIENT_SEQ.nextval INTO result FROM dual;
    INSERT INTO CLIENT(CLIENT_ID, LOGIN, PASSWORD) VALUES (result, login, pwd);
  EXCEPTION
    WHEN OTHERS THEN
      IF sqlcode != -1 THEN
        RAISE;
      END IF;
      result := 0;
  END;
END CLIENT_PKG;
/
