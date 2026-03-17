package com.tss.DB;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/food_ordering_app";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}