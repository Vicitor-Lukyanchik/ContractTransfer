package com.example.transfer;

import com.example.transfer.s03000.entity.Rate;
import com.example.transfer.s03000.entity.RateId;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        String url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=bsznt1)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=BSZO.BSZ)))";
        String user = "ts02015";
        String password = "ts02015";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database!");
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from pack_type t");

            while (resultSet.next()) {
                Long id = resultSet.getLong("tpack_id");
                String name = resultSet.getString("tpack_name");

                System.out.println("ID: " + id + ", Name: " + name);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//}
//    public static void main(String[] args) {
//        String url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=bsznt1)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=BSZO.BSZ)))";
//        String user1 = "s03000";
//        String password1 = "s03000";
//        String user2 = "ts02015";
//        String password2 = "ts02015";
//        // Пример использования
//        try (
//                Connection conn1 = DriverManager.getConnection(url, user1, password1);
//                Connection conn2 = DriverManager.getConnection(url, user2, password2)
//        ) {
//            List<Rate> ratesFromDb1 = fetchRates(conn1); // Получаем данные из первой базы
//            List<Rate> ratesFromDb2 = fetchRates(conn2); // Получаем данные из второй базы
//
//            compareRateLists(ratesFromDb1, ratesFromDb2);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * Метод для сравнения двух списков объектов Rate.
//     */
//    public static void compareRateLists(List<Rate> rates1, List<Rate> rates2) {
//        // Создаем карту для быстрого поиска записей по ключу (CUR_KOD, RAT_DATA)
//        Map<RateId, Rate> rateMap1 = rates1.stream()
//                .collect(Collectors.toMap(Rate::getId, rate -> rate));
//        Map<RateId, Rate> rateMap2 = rates2.stream()
//                .collect(Collectors.toMap(Rate::getId, rate -> rate));
//
//        // Сравниваем записи из первой базы со второй
//        for (Rate rate1 : rates1) {
//            RateId key = rate1.getId();
//            Rate rate2 = rateMap2.get(key);
//
//            if (rate2 == null) {
//                System.out.println("Запись отсутствует во второй базе: " + key);
//            } else {
//                compareRates(rate1, rate2);
//            }
//        }
//
//        // Проверяем записи, которые есть только во второй базе
//        for (Rate rate2 : rates2) {
//            RateId key = rate2.getId();
//            if (!rateMap1.containsKey(key)) {
//                System.out.println("Запись отсутствует в первой базе: " + key);
//            }
//        }
//    }
//
//    /**
//     * Метод для сравнения двух объектов Rate.
//     */
//    public static void compareRates(Rate rate1, Rate rate2) {
//        RateId key = rate1.getId();
//
//        if (!Objects.equals(rate1.getKurs(), rate2.getKurs())) {
//            System.out.println("Различие в поле 'RAT_KURS' для записи " + key + ":");
//            System.out.println("  База 1: " + rate1.getKurs());
//            System.out.println("  База 2: " + rate2.getKurs());
//        }
//
//        if (!Objects.equals(rate1.getShkala(), rate2.getShkala())) {
//            System.out.println("Различие в поле 'RAT_SHKALA' для записи " + key + ":");
//            System.out.println("  База 1: " + rate1.getShkala());
//            System.out.println("  База 2: " + rate2.getShkala());
//        }
//
//        if (!Objects.equals(rate1.getKursBO(), rate2.getKursBO())) {
//            System.out.println("Различие в поле 'RAT_KURS_BO' для записи " + key + ":");
//            System.out.println("  База 1: " + rate1.getKursBO());
//            System.out.println("  База 2: " + rate2.getKursBO());
//        }
//    }
//
//    private static List<Rate> fetchRates(Connection connection) throws SQLException {
//        List<Rate> rates = new ArrayList<>();
//        String query = "SELECT CUR_KOD, RAT_DATA, RAT_KURS, RAT_SHKALA, RAT_KURS_BO FROM RATE";
//
//        try (Statement stmt = connection.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                Rate rate = Rate.builder()
//                        .kod(rs.getLong("CUR_KOD"))
//                        .date(rs.getDate("RAT_DATA"))
//                        .kurs(rs.getDouble("RAT_KURS"))
//                        .shkala(rs.getLong("RAT_SHKALA"))
//                        .kursBO(rs.getDouble("RAT_KURS_BO"))
//                        .build();
//                rates.add(rate);
//            }
//        }
//        return rates;
//    }


//    public static void sendEmail() {
//        String host = "192.168.1.29"; // SMTP-сервер
//        String port = "25";          // Порт
//        String username = "bsz@bsz.by"; // Ваш email (может быть необязательным)
//
//        Properties props = new Properties();
//        props.put("mail.smtp.host", host);
//        props.put("mail.smtp.port", port);
//
//        Session session = Session.getInstance(props, null);
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(username));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("VLukyanchik@bsz.by"));
//            message.setSubject("Тестовое письмо");
//            message.setText("Это тестовое письмо.");
//
//            Transport.send(message);
//            System.out.println("Письмо успешно отправлено.");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }

//
//    public static void main(String[] args) {
//        // Настройки подключения к первой базе данных (исходной)
//        String url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=bsznt1)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=BSZO.BSZ)))";
//        String user1 = "s03000";
//        String password1 = "s03000";
//
//        // Настройки подключения ко второй базе данных (целевой)
//        String user2 = "ts02015";
//        String password2 = "ts02015";
//
//        Connection conn1 = null;
//        Connection conn2 = null;
//
//        try {
//            // Установка подключения к первой базе данных
//            conn1 = DriverManager.getConnection(url, user1, password1);
//            System.out.println("Подключение к первой базе данных установлено.");
//
//            // Установка подключения ко второй базе данных
//            conn2 = DriverManager.getConnection(url, user2, password2);
//            System.out.println("Подключение ко второй базе данных установлено.");
//
//            // SQL-запрос для чтения данных из таблицы RATE первой базы данных
//            String selectQuery = "SELECT CUR_KOD, RAT_DATA, RAT_KURS, RAT_SHKALA, RAT_KURS_BO FROM RATE";
//
//            // SQL-запрос для вставки данных в таблицу RATE1 второй базы данных
//            String insertQuery = "INSERT INTO RATE1 (CUR_KOD, RAT_DATA, RAT_KURS, RAT_SHKALA, RAT_KURS_BO) VALUES (?, ?, ?, ?, ?)";
//
//            // Создание PreparedStatement для вставки данных
//            PreparedStatement insertStatement = conn2.prepareStatement(insertQuery);
//
//            // Чтение данных из первой базы данных
//            Statement selectStatement = conn1.createStatement();
//            ResultSet resultSet = selectStatement.executeQuery(selectQuery);
//
//            int recordCount = 0;
//
//            // Обработка каждой строки из первой базы данных
//            while (resultSet.next()) {
//                // Извлечение данных из ResultSet
//                Long curKod = resultSet.getLong("CUR_KOD");
//                Date ratData = resultSet.getDate("RAT_DATA");
//                Double ratKurs = resultSet.getDouble("RAT_KURS");
//                Long ratShkala = resultSet.getLong("RAT_SHKALA");
//                Double ratKursBo = resultSet.getDouble("RAT_KURS_BO");
//
//                // Установка значений в PreparedStatement
//                insertStatement.setLong(1, curKod);
//                insertStatement.setDate(2, ratData);
//                insertStatement.setDouble(3, ratKurs);
//                insertStatement.setLong(4, ratShkala);
//                insertStatement.setDouble(5, ratKursBo);
//
//                // Выполнение вставки данных во вторую базу данных
//                insertStatement.addBatch();
//
//                // Для оптимизации: отправляем пакет каждые 100 записей
//                if (++recordCount % 100 == 0) {
//                    insertStatement.executeBatch();
//                }
//            }
//
//            // Выполнение оставшихся операций вставки
//            insertStatement.executeBatch();
//
//            System.out.println("Миграция завершена. Перенесено записей: " + recordCount);
//
//        } catch (SQLException e) {
//            System.err.println("Ошибка при работе с базой данных: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            // Закрытие подключений
//            try {
//                if (conn1 != null && !conn1.isClosed()) {
//                    conn1.close();
//                    System.out.println("Подключение к первой базе данных закрыто.");
//                }
//            } catch (SQLException e) {
//                System.err.println("Ошибка при закрытии подключения к первой базе данных: " + e.getMessage());
//            }
//
//            try {
//                if (conn2 != null && !conn2.isClosed()) {
//                    conn2.close();
//                    System.out.println("Подключение ко второй базе данных закрыто.");
//                }
//            } catch (SQLException e) {
//                System.err.println("Ошибка при закрытии подключения ко второй базе данных: " + e.getMessage());
//            }
//        }
//    }

}