/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.moogata.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Windows
 */
public class Bill {

    public static final String STATUS_OPEN = "เปิด";
    public static final String STATUS_AWAITING = "รอชำระ";
    public static final String STATUS_CLOSED = "ปิด";

    //field
    private int id;
    private int tableId;
    private String status;
    private String openedAt;
    private String closedAt;
    private double total;
    private double cashReceived;
    private double changeAmount;
    private int openedBy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(double cashReceived) {
        this.cashReceived = cashReceived;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public int getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(int openedBy) {
        this.openedBy = openedBy;
    }

    //Constructor
    public Bill() {
        this.id = -1;
    }
    
    public Bill(int id, int tableId, String status, String openedAt, String closedAt, double total, double cashReceived, double changeAmount, int openedBy) {
        this.id = id;
        this.tableId = tableId;
        this.status = status;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.total = total;
        this.cashReceived = cashReceived;
        this.changeAmount = changeAmount;
        this.openedBy = openedBy;
    }

    /**
     * สำหรับบิลที่เพิ่งเปิด — ยังไม่มียอด/เวลาปิด (ให้ฐานข้อมูล default ให้)
     */
    public Bill(int tableId, int openedBy) {
        this(-1, tableId, STATUS_OPEN, null, null, 0, 0, 0, openedBy);
    }

    public boolean isOpen() {
        return STATUS_OPEN.equals(status);
    }

    public static Bill fromRS(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("bill_id"));
        bill.setTableId(rs.getInt("table_id"));
        bill.setStatus(rs.getString("status"));
        bill.setOpenedAt(rs.getString("opened_at"));
        bill.setClosedAt(rs.getString("closed_at"));
        bill.setTotal(rs.getDouble("total"));
        bill.setCashReceived(rs.getDouble("cash_received"));
        bill.setChangeAmount(rs.getDouble("change_amount"));
        bill.setOpenedBy(rs.getInt("opened_by"));
        return bill;
    }
}
