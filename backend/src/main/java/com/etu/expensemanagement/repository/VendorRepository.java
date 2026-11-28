package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.model.Vendor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class VendorRepository {

    private final JdbcClient jdbc;

    public VendorRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    private static Vendor map(ResultSet rs, int rowNum) throws SQLException {
        return new Vendor(
                rs.getInt("vendor_id"),
                rs.getString("name"));
    }

    /** Used by: GET /api/vendors. */
    public List<Vendor> findAllOrderByName() {
        return jdbc.sql("""
                SELECT vendor_id, name
                FROM expense_management.vendor
                ORDER BY name
                """)
                .query(VendorRepository::map)
                .list();
    }
}