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
public class DiningTable {

    public static final String STATUS_VACANT = "ว่าง";
    public static final String STATUS_IN_USE = "ใช้งาน";
    public static final String STATUS_AWAITING = "รอชำระ";

    private int id;
    private int number;
    private String status;
    private int headcount;

    /**
     * โต๊ะนี้ว่างอยู่หรือไม่ — ใช้บ่อยทั้งใน service และหน้าจอ
     */
    public boolean isVacant() {
        return STATUS_VACANT.equals(status);
    }

    //constructor
    public static DiningTable fromRS(ResultSet rs) throws SQLException {
        DiningTable table = new DiningTable();
        table.setId(rs.getInt("table_id"));
        table.setNumber(rs.getInt("table_number"));
        table.setStatus(rs.getString("status"));
        table.setHeadcount(rs.getInt("headcount"));
        return table;
    }

    public DiningTable(int id, int number, String status, int headcount) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.headcount = headcount;
    }

    public DiningTable(int number, String status, int headcount) {
        this(-1, number, status, headcount);
    }

    public DiningTable() {
        this.id = -1;
    }

    //getter setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getHeadcount() {
        return headcount;
    }

    public void setHeadcount(int headcount) {
        this.headcount = headcount;
    }

}
