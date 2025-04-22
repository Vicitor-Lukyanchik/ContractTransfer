package com.example.transfer;

import java.sql.*;

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
}
