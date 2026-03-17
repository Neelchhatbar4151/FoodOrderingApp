package com.tss.Repository;

import com.tss.DB.DBConnection;
import com.tss.Datatype.Role;
import com.tss.model.User.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUserRepository implements UserRepository {

    private DBUserRepository(){}
    // ================================
    // 🔹 GET USER (LOGIN)
    // ================================
    @Override
    public User getUser(String phone, String password, Role role) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT * FROM users
                WHERE phone = ? AND password = ? AND role = ?
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, phone);
            ps.setString(2, password);
            ps.setString(3, role.name());

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return buildUser(conn, rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================================
    // 🔹 GET ALL USERS BY ROLE
    // ================================
    @Override
    public List<User> getAllUsersInRole(Role role) {

        List<User> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String query = "SELECT * FROM users WHERE role = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, role.name());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(buildUser(conn, rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================================
    // 🔹 GET USER BY ID
    // ================================
    @Override
    public User getUserById(int id) {

        try (Connection conn = DBConnection.getConnection()) {

            String query = "SELECT * FROM users WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return buildUser(conn, rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================================
    // 🔹 ADD NEW USER (REGISTER)
    // ================================
    @Override
    public boolean addNewUser(User user) {

        try (Connection conn = DBConnection.getConnection()) {

            // ❗ check duplicate phone in same role
            String checkQuery = """
                SELECT 1 FROM users WHERE phone = ? AND role = ?
            """;

            PreparedStatement checkPs = conn.prepareStatement(checkQuery);
            checkPs.setString(1, user.getPhone());
            checkPs.setString(2, user.getRole().name());

            ResultSet checkRs = checkPs.executeQuery();
            if (checkRs.next()) return false;

            // 🔹 insert into users
            String insertUser = """
                INSERT INTO users(name, phone, password, role, created_on)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
            """;

            PreparedStatement ps = conn.prepareStatement(insertUser);
            ps.setString(1, user.getName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());
            ps.setObject(5, user.getCreatedOn());

            ResultSet rs = ps.executeQuery();
            rs.next();
            int generatedId = rs.getInt("id");

            user.setId(generatedId);

            // 🔥 role specific insert
            insertRoleSpecific(conn, user);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ================================
    // 🔥 HELPER: BUILD USER
    // ================================
    private User buildUser(Connection conn, ResultSet rs) throws Exception {

        Role role = Role.valueOf(rs.getString("role"));
        int id = rs.getInt("id");

        switch (role) {

            case CUSTOMER -> {
                return buildCustomer(conn, rs, id);
            }

            case DELIVERY_PARTNER -> {
                return buildDeliveryPartner(conn, rs, id);
            }

            case ADMIN -> {
                return new Admin(
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("password")
                );
            }
        }

        return null;
    }

    // ================================
    // 🔹 CUSTOMER BUILDER
    // ================================
    private Customer buildCustomer(Connection conn, ResultSet userRs, int id) throws Exception {

        String query = "SELECT * FROM customers WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();

        return new Customer.Builder()
                .setId(id)
                .setName(userRs.getString("name"))
                .setPhone(userRs.getString("phone"))
                .setPassword(userRs.getString("password"))
                .setRole(Role.CUSTOMER)
                .setCreatedOn(userRs.getTimestamp("created_on").toLocalDateTime())

                .setUpiId(rs.getString("upi_id"))
                .setAddress(rs.getString("address"))

                // ❗ lazy loading
                .setOrderList(new ArrayList<>())
                .setNotifications(new ArrayList<>())
                .setCart(null)

                .build();
    }

    // ================================
    // 🔹 DELIVERY PARTNER BUILDER
    // ================================
    private DeliveryPartner buildDeliveryPartner(Connection conn, ResultSet userRs, int id) throws Exception {

        String query = "SELECT * FROM delivery_partners WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();

        return new DeliveryPartner(
                userRs.getString("name"),
                userRs.getString("phone"),
                userRs.getString("password")
        );
    }

    // ================================
    // 🔹 INSERT ROLE SPECIFIC
    // ================================
    private void insertRoleSpecific(Connection conn, User user) throws Exception {

        if (user instanceof Customer c) {

            String query = """
                INSERT INTO customers(id, upi_id, address)
                VALUES (?, ?, ?)
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, c.getId());
            ps.setString(2, c.getUpiId());
            ps.setString(3, c.getAddress());
            ps.executeUpdate();
        }

        else if (user instanceof DeliveryPartner d) {

            String query = """
                INSERT INTO delivery_partners(id)
                VALUES (?)
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, d.getId());
            ps.executeUpdate();
        }

        // Admin → nothing extra
    }

    public static class Initiator{
        private static final DBUserRepository instance = new DBUserRepository();

    }

    public static DBUserRepository getInstance(){
        return DBUserRepository.Initiator.instance;
    }
}