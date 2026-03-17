package com.tss.Repository;

import com.tss.DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBPaymentRepository implements PaymentRepository {

    private DBPaymentRepository() {}

    @Override
    public long createPayment(String paymentMode, String transactionReferenceId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                    INSERT INTO payment(payment_mode, transaction_reference_id)
                    VALUES (?::payment_mode, ?)
                    RETURNING payment_id
                    """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, paymentMode);
            ps.setString(2, transactionReferenceId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("payment_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public PaymentRecord getById(long paymentId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM payment WHERE payment_id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, paymentId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }

            return new PaymentRecord(
                    rs.getLong("payment_id"),
                    rs.getString("payment_mode"),
                    rs.getString("transaction_reference_id")
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updatePayment(long paymentId, String paymentMode, String transactionReferenceId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                    UPDATE payment
                    SET payment_mode = ?::payment_mode,
                        transaction_reference_id = ?
                    WHERE payment_id = ?
                    """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, paymentMode);
            ps.setString(2, transactionReferenceId);
            ps.setLong(3, paymentId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static class Initiator {
        private static final DBPaymentRepository instance = new DBPaymentRepository();
    }

    public static DBPaymentRepository getInstance() {
        return Initiator.instance;
    }
}
