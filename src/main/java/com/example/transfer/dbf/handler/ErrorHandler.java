package com.example.transfer.dbf.handler;

import com.example.transfer.dbf.email.impl.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.hibernate.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.net.BindException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

@Component
@RequiredArgsConstructor
public class ErrorHandler {

    public static final String INDENT = "                      ";
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    private final EmailNotificationService emailNotificationService;

    @Value("${server.port}")
    private String serverPort;


    public void handleException(Exception e) {
        String errorMessage;

        if (e instanceof CannotGetJdbcConnectionException) {
            errorMessage = handleDatabaseConnectionException((CannotGetJdbcConnectionException) e);
        } else if (e instanceof DataIntegrityViolationException) {
            errorMessage = handleDataIntegrityViolationException((DataIntegrityViolationException) e);
        } else if (e instanceof PersistenceException) {
            errorMessage = handlePersistenceException((PersistenceException) e);
        } else if (e instanceof DataAccessException) {
            errorMessage = handleDataAccessException((DataAccessException) e);
        } else if (e instanceof SQLException) {
            errorMessage = handleSqlException((SQLException) e);
        } else if (e instanceof BindException ||
                (e instanceof BeanCreationException && e.getMessage().contains("address already in use"))) {
            errorMessage = handlePortAlreadyInUseException(e);
        } else if (e instanceof TransactionSystemException ||
                e instanceof TransactionException ||
                (e instanceof IllegalStateException && e.getMessage().contains("Transaction not active"))) {
            errorMessage = handleTransactionException(e);
        } else if (e instanceof IllegalStateException && e.getMessage().contains("Session/EntityManager is closed")) {
            errorMessage = handleClosedEntityManagerException(e);
        } else if (e instanceof BeanCreationException) {
            errorMessage = handleBeanCreationException((BeanCreationException) e);
        } else if (e instanceof EntityNotFoundException) {
            errorMessage = handleEntityNotFoundException((EntityNotFoundException) e);
        } else if (e.getMessage().contains("Could not roll back JPA transaction; nested exception is org.hibernate.TransactionException: Unable to rollback against JDBC Connection")) {
            errorMessage = handlePortAlreadyInUseException(e);
        } else {
            errorMessage = formatDefaultErrorMessage(e);
        }


        logger.error(errorMessage + "\n\n");
        emailNotificationService.sendErrorNotification(errorMessage);
    }

    private String handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Throwable rootCause = getRootCause(e);

        if (rootCause.getMessage().contains("foreign key constraint fails")) {
            return formatErrorMessage(
                    "Нарушение ссылочной целостности",
                    "Попытка изменения данных, на которые есть ссылки в других таблицах",
                    "1. Проверьте связанные данные перед изменением/удалением\n" + INDENT +
                            "2. Используйте каскадное удаление или установите NULL\n" + INDENT +
                            "3. Измените бизнес-логику обработки зависимых данных"
            );
        } else if (rootCause.getMessage().contains("Duplicate entry")) {
            return formatErrorMessage(
                    "Дублирование данных",
                    "Попытка вставить дублирующиеся значения в уникальное поле",
                    "1. Проверьте уникальность вставляемых данных\n" + INDENT +
                            "2. Используте INSERT ON DUPLICATE KEY UPDATE\n" + INDENT +
                            "3. Реализуйте проверку существования перед вставкой"
            );
        } else if (rootCause.getMessage().contains("cannot be null")) {
            return formatErrorMessage(
                    "Нарушение ограничения NOT NULL",
                    "Попытка вставить NULL в обязательное поле",
                    "1. Проверьте заполнение всех обязательных полей\n" + INDENT +
                            "2. Установите значения по умолчанию\n" + INDENT +
                            "3. Измените DDL таблицы при необходимости"
            );
        }

        return formatErrorMessage(
                "Ошибка целостности данных",
                rootCause.getMessage(),
                "1. Проверьте соответствие данных ограничениям БД\n" + INDENT +
                        "2. Убедитесь в корректности DDL-структуры\n" + INDENT +
                        "3. Проверьте бизнес-логику работы с данными"
        );
    }

    private String handlePersistenceException(PersistenceException e) {
        Throwable rootCause = getRootCause(e);

        if (rootCause.getMessage().contains("detached entity passed to persist")) {
            return formatErrorMessage(
                    "Ошибка работы с Entity",
                    "Попытка сохранения detached-сущности",
                    "1. Используйте merge() вместо persist() для detached-сущностей\n" + INDENT +
                            "2. Проверьте жизненный цикл сущностей\n" + INDENT +
                            "3. Убедитесь в правильности каскадных операций"
            );
        }

        return formatErrorMessage(
                "Ошибка JPA/Hibernate",
                rootCause.getMessage(),
                "1. Проверьте корректность работы с EntityManager\n" + INDENT +
                        "2. Убедитесь в правильности маппинга сущностей\n" + INDENT +
                        "3. Проверьте настройки JPA"
        );
    }

    private String handleDataAccessException(DataAccessException e) {
        return formatErrorMessage(
                "Ошибка доступа к данным",
                getRootCause(e).getMessage(),
                "1. Проверьте SQL-запросы в коде\n" + INDENT +
                        "2. Убедитесь в корректности работы с JDBC\n" + INDENT +
                        "3. Проверьте права доступа к БД"
        );
    }

    private String handleSqlException(SQLException e) {
        switch (e.getSQLState()) {
            case "08000": // Connection exception
                return formatErrorMessage(
                        "Ошибка соединения с БД",
                        e.getMessage(),
                        "1. Проверьте доступность сервера БД\n" + INDENT +
                                "2. Убедитесь в правильности параметров подключения\n" + INDENT +
                                "3. Проверьте сетевые настройки"
                );
            case "22001": // String data right truncation
                return formatErrorMessage(
                        "Ошибка усечения данных",
                        "Попытка вставить слишком длинное значение в поле",
                        "1. Увеличьте размер поля в БД\n" + INDENT +
                                "2. Проверьте валидацию данных перед вставкой\n" + INDENT +
                                "3. Обрежьте данные до допустимого размера"
                );
            case "23505": // Unique constraint violation
                return handleDataIntegrityViolationException(new DataIntegrityViolationException(e.getMessage()));
            default:
                return formatErrorMessage(
                        "SQL ошибка (" + e.getSQLState() + ")",
                        e.getMessage(),
                        "1. Проверьте синтаксис SQL-запросов\n" + INDENT +
                                "2. Убедитесь в корректности параметров\n" + INDENT +
                                "3. Обратитесь к документации СУБД по коду ошибки"
                );
        }
    }

    private String handleBeanCreationException(BeanCreationException e) {
        Throwable rootCause = getRootCause(e);

        if (rootCause.getMessage().contains("address already in use")) {
            return handlePortAlreadyInUseException(e);
        }

        if (rootCause.getMessage().contains("No qualifying bean of type")) {
            return formatErrorMessage(
                    "Ошибка dependency injection",
                    "Не найден подходящий bean для внедрения",
                    "1. Проверьте аннотации @Component/@Service на нужном классе\n" + INDENT +
                            "2. Убедитесь в правильности сканирования компонентов\n" + INDENT +
                            "3. Проверьте @Qualifier и @Primary аннотации"
            );
        }

        return formatErrorMessage(
                "Ошибка создания Spring bean",
                rootCause.getMessage(),
                "1. Проверьте конфигурацию Spring\n" + INDENT +
                        "2. Убедитесь в корректности зависимостей\n" + INDENT +
                        "3. Проверьте инициализацию бинов"
        );
    }

    private String handleDatabaseConnectionException(CannotGetJdbcConnectionException e) {
        Throwable rootCause = getRootCause(e);

        if (rootCause.getMessage().contains("Access denied")) {
            return formatErrorMessage(
                    "Ошибка аутентификации в БД",
                    "Неправильный логин или пароль для подключения к базе данных",
                    "1. Проверьте параметры подключения в application.properties\n" + INDENT +
                            "2. Убедитесь в правильности username/password\n" + INDENT +
                            "3. Проверьте права пользователя в БД"
            );
        } else if (rootCause.getMessage().contains("Unknown database")) {
            return formatErrorMessage(
                    "Ошибка подключения к БД",
                    "Указанная база данных не существует",
                    "1. Проверьте название БД в настройках\n" + INDENT +
                            "2. Создайте БД, если она отсутствует\n" + INDENT +
                            "3. Проверьте регистр названия БД"
            );
        } else if (rootCause instanceof ConnectException) {
            return formatErrorMessage(
                    "Ошибка подключения к серверу БД",
                    "Не удалось установить соединение с сервером базы данных",
                    "1. Проверьте доступность сервера БД\n" + INDENT +
                            "2. Убедитесь в правильности host:port\n" + INDENT +
                            "3. Проверьте сетевые настройки и firewall\n" + INDENT +
                            "4. Попробуйте перезапустить сервер БД"
            );
        } else if (rootCause instanceof SQLTimeoutException) {
            return formatErrorMessage(
                    "Таймаут подключения к БД",
                    "Превышено время ожидания подключения",
                    "1. Увеличьте таймаут подключения в настройках\n" + INDENT +
                            "2. Проверьте нагрузку на сервер БД\n" + INDENT +
                            "3. Оптимизируйте запросы\n" + INDENT +
                            "4. Проверьте сетевую задержку"
            );
        } else {
            return formatErrorMessage(
                    "Ошибка подключения к БД",
                    rootCause.getMessage(),
                    "1. Проверьте параметры подключения\n" + INDENT +
                            "2. Убедитесь в доступности сервера БД\n" + INDENT +
                            "3. Проверьте логи сервера БД"
            );
        }
    }

    private String handleTransactionException(Exception e) {
        Throwable rootCause = getRootCause(e);

        String baseRecommendation = "Общие рекомендации:\n" + INDENT +
                "1. Проверьте доступность и стабильность соединения с БД\n" + INDENT +
                "2. Увеличьте таймауты в настройках пула соединений\n" + INDENT +
                "3. Проверьте нагрузку на сервер БД\n" + INDENT +
                "4. Убедитесь в достаточных ресурсах сервера";

        return formatErrorMessage(
                "Ошибка выполнения транзакции",
                rootCause.getMessage(),
                baseRecommendation
        );
    }

    private String handleClosedEntityManagerException(Exception e) {
        return formatErrorMessage(
                "Ошибка доступа к EntityManager",
                "Попытка использования закрытого EntityManager",
                "1. Проверьте время жизни EntityManager\n" + INDENT +
                        "2. Убедитесь, что работа с БД происходит в пределах транзакции\n" + INDENT +
                        "3. Проверьте, не закрывается ли соединение при ошибках\n" + INDENT +
                        "4. Рассмотрите использование OpenSessionInViewFilter"
        );
    }

    private String handlePortAlreadyInUseException(Exception e) {
        return formatErrorMessage(
                "Конфликт портов",
                "Порт " + serverPort + " уже используется другим процессом",
                "1. Измените порт приложения в application.properties:\n" +INDENT +
                        "   server.port=новый_порт\n" +INDENT +
                        "2. Найдите и завершите конфликтующий процесс:\n" +INDENT +
                        "   Windows: `netstat -ano | findstr :" + serverPort
        );
    }

    private String handleEntityNotFoundException(EntityNotFoundException e) {
        return formatErrorMessage(
                "Объект не найден",
                e.getMessage(),
                "1. Проверьте существование запрашиваемого объекта\n" +INDENT +
                        "2. Убедитесь в правильности идентификатора\n" +INDENT +
                        "3. Проверьте права доступа к объекту"
        );
    }

    private String formatDefaultErrorMessage(Exception e) {
        return formatErrorMessage(
                e.getClass().getSimpleName(),
                e.getMessage(),
                "1. Проверьте логи приложения для деталей\n" +INDENT +
                        "2. Убедитесь в корректности всех настроек"
        );
    }

    private Throwable getRootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    private String formatErrorMessage(String errorType, String cause, String recommendation) {
        return String.format(
                "[ОШИБКА] %s\n" + INDENT + "Причина: %s\n" + INDENT + "Рекомендация:\n" + INDENT + "%s",
                errorType,
                cause != null ? cause : "не указана",
                recommendation
        );
    }
}