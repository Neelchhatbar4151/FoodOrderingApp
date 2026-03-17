package com.tss.Repository;

import com.tss.Datatype.AvailabilityStatus;
import com.tss.Datatype.Role;
import com.tss.DB.DBConnection;
import com.tss.model.User.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBUserRepository implements UserRepository {

    private DBUserRepository(){}
    @Override
    public User getUser(String phone, String password, Role role) {
        try (Connection conn = DBConnection.getConnection()) {
            return switch (role) {
                case CUSTOMER -> getCustomerByPhonePassword(conn, phone, password);
                case DELIVERY_PARTNER -> getDeliveryPartnerByPhonePassword(conn, phone, password);
                case ADMIN -> getAdminByPhonePassword(conn, phone, password);
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<User> getAllUsersInRole(Role role) {
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            switch (role) {
                case CUSTOMER -> loadAllCustomers(conn, users);
                case DELIVERY_PARTNER -> loadAllDeliveryPartners(conn, users);
                case ADMIN -> loadAllAdmins(conn, users);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public User getUserById(int id) {
        try (Connection conn = DBConnection.getConnection()) {

            User customer = getCustomerById(conn, id);
            if (customer != null) return customer;

            User partner = getDeliveryPartnerById(conn, id);
            if (partner != null) return partner;

            return getAdminById(conn, id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean addNewUser(User user) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (isPhoneExistsForRole(conn, user.getPhone(), user.getRole())) {
                    conn.rollback();
                    return false;
                }

                long userId = insertAppUser(conn, user);
                if (userId == -1) {
                    conn.rollback();
                    return false;
                }

                user.setId(userId);
                insertRoleSpecific(conn, user);

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isPhoneExistsForRole(Connection conn, String phone, Role role) throws Exception {
        String table = switch (role) {
            case CUSTOMER -> "customer";
            case DELIVERY_PARTNER -> "delivery_partner";
            case ADMIN -> "admin";
        };

        String query = "SELECT 1 FROM " + table + " WHERE phone = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, phone);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    private long insertAppUser(Connection conn, User user) throws Exception {
        String query = """
                INSERT INTO app_user(name, created_on, is_deleted)
                VALUES (?, ?, false)
                RETURNING user_id
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, user.getName());
        ps.setObject(2, user.getCreatedOn());

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getLong("user_id");
        }

        return -1;
    }

    private void insertRoleSpecific(Connection conn, User user) throws Exception {
        if (user instanceof Customer c) {
            String query = """
                    INSERT INTO customer(customer_id, phone, password, upi_id, address)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, c.getId());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getPassword());
            ps.setString(4, c.getUpiId());
            ps.setString(5, c.getAddress());
            ps.executeUpdate();
            return;
        }

        if (user instanceof DeliveryPartner d) {
            String query = """
                    INSERT INTO delivery_partner(delivery_partner_id, phone, password, is_available, total_earnings, is_approved)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, d.getId());
            ps.setString(2, d.getPhone());
            ps.setString(3, d.getPassword());
            ps.setBoolean(4, true);
            ps.setDouble(5, d.getTotalEarnings());
            ps.setBoolean(6, d.getIsApproved());
            ps.executeUpdate();
            return;
        }

        if (user instanceof Admin a) {
            String query = """
                    INSERT INTO admin(admin_id, phone, password)
                    VALUES (?, ?, ?)
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, a.getId());
            ps.setString(2, a.getPhone());
            ps.setString(3, a.getPassword());
            ps.executeUpdate();
        }
    }

    private User getCustomerByPhonePassword(Connection conn, String phone, String password) throws Exception {
        String query = """
                SELECT au.user_id, au.name, au.created_on,
                       c.phone, c.password, c.upi_id, c.address
                FROM app_user au
                JOIN customer c ON c.customer_id = au.user_id
                WHERE c.phone = ? AND c.password = ? AND au.is_deleted = false
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, phone);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return null;

        return buildCustomer(rs);
    }

    private User getDeliveryPartnerByPhonePassword(Connection conn, String phone, String password) throws Exception {
        String query = """
                SELECT au.user_id, au.name, au.created_on,
                       d.phone, d.password, d.is_available, d.total_earnings, d.is_approved
                FROM app_user au
                JOIN delivery_partner d ON d.delivery_partner_id = au.user_id
                WHERE d.phone = ? AND d.password = ? AND au.is_deleted = false
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, phone);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return null;

        return buildDeliveryPartner(rs);
    }

    private User getAdminByPhonePassword(Connection conn, String phone, String password) throws Exception {
        String query = """
                SELECT au.user_id, au.name, au.created_on,
                       a.phone, a.password
                FROM app_user au
                JOIN admin a ON a.admin_id = au.user_id
                WHERE a.phone = ? AND a.password = ? AND au.is_deleted = false
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, phone);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return null;

        return buildAdmin(rs);
    }

    private User getCustomerById(Connection conn, long id) throws Exception {
        String query = """
                SELECT au.user_id, au.name, au.created_on,
                       c.phone, c.password, c.upi_id, c.address
                FROM app_user au
                JOIN customer c ON c.customer_id = au.user_id
                WHERE au.user_id = ? AND au.is_deleted = false
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return null;

        return buildCustomer(rs);
    }

    private User getDeliveryPartnerById(Connection conn, long id) throws Exception {
        String query = """
                SELECT au.user_id, au.name, au.created_on,
                       d.phone, d.password, d.is_available, d.total_earnings, d.is_approved
                FROM app_user au
                JOIN delivery_partner d ON d.delivery_partner_id = au.user_id
                WHERE au.user_id = ? AND au.is_deleted = false
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return null;

        return buildDeliveryPartner(rs);
    }

    private User getAdminById(Connection conn, long id) throws Exception {
        String query = """
                SELECT au.user_id, au.name, au.created_on,
                       a.phone, a.password
                FROM app_user au
                JOIN admin a ON a.admin_id = au.user_id
                WHERE au.user_id = ? AND au.is_deleted = false
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return null;

        return buildAdmin(rs);
    }

    private void loadAllCustomers(Connection conn, List<User> users) throws Exception {
        String query = """
                SELECT au.user_id, au.name, au.created_on,
                       c.phone, c.password, c.upi_id, c.address
                FROM app_user au
                JOIN customer c ON c.customer_id = au.user_id
                WHERE au.is_deleted = false
                ORDER BY au.user_id
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            users.add(buildCustomer(rs));
        }
    }

    private void loadAllDeliveryPartners(Connection conn, List<User> users) throws Exception {
        String query = """
                SELECT au.user_id, au.name, au.created_on,
                       d.phone, d.password, d.is_available, d.total_earnings, d.is_approved
                FROM app_user au
                JOIN delivery_partner d ON d.delivery_partner_id = au.user_id
                WHERE au.is_deleted = false
                ORDER BY au.user_id
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            users.add(buildDeliveryPartner(rs));
        }
    }

    private void loadAllAdmins(Connection conn, List<User> users) throws Exception {
        String query = """
                SELECT au.user_id, au.name, au.created_on,
                       a.phone, a.password
                FROM app_user au
                JOIN admin a ON a.admin_id = au.user_id
                WHERE au.is_deleted = false
                ORDER BY au.user_id
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            users.add(buildAdmin(rs));
        }
    }

    private Customer buildCustomer(ResultSet rs) throws Exception {
        return new Customer.Builder()
                .setId(rs.getLong("user_id"))
                .setName(rs.getString("name"))
                .setPhone(rs.getString("phone"))
                .setPassword(rs.getString("password"))
                .setRole(Role.CUSTOMER)
                .setCreatedOn(rs.getTimestamp("created_on").toLocalDateTime())
                .setUpiId(rs.getString("upi_id"))
                .setAddress(rs.getString("address"))
                .setOrderList(new ArrayList<>())
                .setNotifications(new ArrayList<>())
                .setCart(null)
                .build();
    }

    private DeliveryPartner buildDeliveryPartner(ResultSet rs) throws Exception {
        return new DeliveryPartner.Builder()
                .setId(rs.getLong("user_id"))
                .setName(rs.getString("name"))
                .setPhone(rs.getString("phone"))
                .setPassword(rs.getString("password"))
                .setRole(Role.DELIVERY_PARTNER)
                .setCreatedOn(rs.getTimestamp("created_on").toLocalDateTime())
                .setStatus(rs.getBoolean("is_available") ? AvailabilityStatus.AVAILABLE : AvailabilityStatus.NOT_AVAILABLE)
                .setTotalEarnings(rs.getDouble("total_earnings"))
                .setIsApproved(rs.getBoolean("is_approved"))
                .setDeliveredOrders(new ArrayList<>())
                .setNotifications(new ArrayList<>())
                .build();
    }

    private Admin buildAdmin(ResultSet rs) throws Exception {
        return new Admin.Builder()
                .setId(rs.getLong("user_id"))
                .setName(rs.getString("name"))
                .setPhone(rs.getString("phone"))
                .setPassword(rs.getString("password"))
                .setRole(Role.ADMIN)
                .setCreatedOn(rs.getTimestamp("created_on").toLocalDateTime())
                .setNotifications(new ArrayList<>())
                .build();
    }

    public static class Initiator{
        private static final DBUserRepository instance = new DBUserRepository();

    }

    public static DBUserRepository getInstance(){
        return DBUserRepository.Initiator.instance;
    }
}
