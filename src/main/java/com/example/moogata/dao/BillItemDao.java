/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.moogata.dao;

import com.example.moogata.helper.DatabaseHelper;
import com.example.moogata.model.BillItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Windows
 */
public class BillItemDao implements Dao<BillItem> {

    private static final Logger LOG = Logger.getLogger(BillItem.class.getName());

    @Override
    public BillItem get(int id) {
        String sql = "SELECT * FROM bill_item WHERE bill_item_id = ?";
        Connection conn = DatabaseHelper.getConnect();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return BillItem.fromRS(rs);
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "get ล้มเหลว", ex);
        } finally {
            closeQuietly(rs, stmt);
        }
        return null;
    }

    @Override
    public List<BillItem> getAll() {
        List<BillItem> list = new ArrayList<>();
        String sql = "SELECT * FROM bill_item ORDER BY bill_item_id";
        Connection conn = DatabaseHelper.getConnect();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(BillItem.fromRS(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "getAll() ล้มเหลว", ex);
        } finally {
            closeQuietly(rs, stmt);
        }
        return list;
    }

    @Override
    public List<BillItem> getAll(String where, String order) {
        List<BillItem> list = new ArrayList<>();
        String sql = "SELECT * FROM bill_item WHERE " + where + " ORDER BY " + order;
        LOG.warning("getAll(where, order) ต่อสตริงเข้า SQL ตรง ๆ — ใช้กับข้อมูลผู้ใช้จริงไม่ได้ (SQL injection)");
        Connection conn = DatabaseHelper.getConnect();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(BillItem.fromRS(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "getAll(where, order) ล้มเหลว: " + sql, ex);
        } finally {
            closeQuietly(rs, stmt);
        }
        return list;
    }
// BillItemDao — บันทึกรายการที่สั่ง 1 รอบ (เรียกใหม่ได้ทุกรอบ — นี่คือ "สั่งได้หลายรอบ" ในทางข้อมูล)

    @Override
    public BillItem save(BillItem obj) {
        String sql = "INSERT INTO bill_item (bill_id, menu_item_id, item_name, qty, unit_price) "
                + "VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseHelper.getConnect();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, obj.getBillId());
            stmt.setInt(2, obj.getMenuItemId());
            stmt.setString(3, obj.getItemName());
            stmt.setInt(4, obj.getQty());
            stmt.setDouble(5, obj.getUnitPrice());
            stmt.executeUpdate();
            obj.setId(DatabaseHelper.getInsertedId(stmt));
            return obj;
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "save(bill_item=" + obj.getItemName() + ") ล้มเหลว", ex);
            return null;
        } finally {
            closeQuietly(null, stmt);
        }
    }

    // BillItemDao — รายการทั้งหมดของบิลหนึ่งใบ เรียงตามลำดับที่สั่ง (ขั้น 2.4)
    public List<BillItem> findByBillId(int billId) {
        List<BillItem> list = new ArrayList<>();
        String sql = "SELECT * FROM bill_item WHERE bill_id = ? ORDER BY bill_item_id";
        Connection conn = DatabaseHelper.getConnect();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, billId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(BillItem.fromRS(rs));
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "findByBillId ล้มเหลว", ex);
        } finally {
            closeQuietly(rs, stmt);
        }
        return list;
    }

    @Override
    public BillItem update(BillItem obj) {
        String sql = "UPDATE bill_item "
                + "SET bill_id = ?, menu_item_id = ?, item_name = ?, qty = ?, unit_price = ? "
                + "WHERE bill_item_id = ?";
        Connection conn = DatabaseHelper.getConnect();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, obj.getBillId());
            stmt.setInt(2, obj.getMenuItemId());
            stmt.setString(3, obj.getItemName());
            stmt.setInt(4, obj.getQty());
            stmt.setDouble(5, obj.getUnitPrice());
            stmt.setInt(6, obj.getId());
            int affected = stmt.executeUpdate();
            return affected > 0 ? obj : null;
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "update(bill_item_id=" + obj.getId() + ") ล้มเหลว", ex);
            return null;
        } finally {
            closeQuietly(null, stmt);
        }
    }

    @Override
    public int delete(BillItem obj) {
        String sql = "DELETE FROM bill_item WHERE bill_item_id = ?";
        Connection conn = DatabaseHelper.getConnect();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, obj.getId());
            return stmt.executeUpdate();
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "delete(bill_item_id=" + obj.getId() + ") ล้มเหลว", ex);
            return -1;
        } finally {
            closeQuietly(null, stmt);
        }
    }

    private void closeQuietly(ResultSet rs, Statement stmt) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException closeEx) {
                System.err.println("Close rs failed: " + closeEx.getMessage());
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException closeEx) {
                System.err.println("Close stmt failed: " + closeEx.getMessage());
            }
        }
    }
}
