package com.tss.Utils;

import com.tss.config.DBConnection;

import java.sql.*;
import java.util.*;

public class DBUtil {

    private DBUtil( ){}

    private static final Connection connection = DBConnection.connect();

    public static <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        List<T> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    public static int update(String sql, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> list = query(sql, mapper, params);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}

//FETCH DATA
//List<String> names = jdbc.query(
//        "SELECT name FROM customer WHERE phone = ?",
//        rs -> rs.getString("name"),
//        "9876543210"
//);

//FETCH OBJECT
//List<Customer> customers = jdbc.query(
//        "SELECT * FROM customer",
//        rs -> new Customer(
//                rs.getLong("customer_id"),
//                rs.getString("phone")
//        )
//);

//UPDATE/INSERT
//int rows = jdbc.update(
//        "UPDATE delivery_partner SET is_available = ? WHERE delivery_partner_id = ?",
//        true,
//        5
//);

//QUERY ONE
//Optional<String> name = jdbc.queryOne(
//        "SELECT name FROM customer WHERE customer_id = ?",
//        rs -> rs.getString("name"),
//        1
//);