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
public class MenuItem {

    private int id;
    private String name;
    private double price;
    private String unit;      // หน่วยขาย: ท่าน / หม้อ / จาน / แก้ว / ชิ้น
    private String status;    // สถานะขาย: มี / หมด
    private int categoryId;   // FK → menu_category

    public MenuItem(int id, String name, double price, String unit, String status, int categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.status = status;
        this.categoryId = categoryId;
    }

    /**
     * สำหรับเมนูใหม่ที่ยังไม่มี id (id จะถูกสร้างตอน save ผ่าน AUTOINCREMENT)
     */
    public MenuItem(String name, double price, String unit, String status, int categoryId) {
        this(-1, name, price, unit, status, categoryId);
    }

    public MenuItem() {
        this.id = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * ราคาพร้อมทศนิยม 2 ตำแหน่ง ไว้แสดงผล เช่น 289.00
     */
    public String getPriceText() {
        return String.format("%.2f", price);
    }

    /**
     * แปลงแถวข้อมูลจาก ResultSet เป็นวัตถุ MenuItem — แถวปัจจุบันของ rs
     * ต้องถูกเรียก rs.next() แล้วเท่านั้น
     */
    public static MenuItem fromRS(ResultSet rs) throws SQLException {
        MenuItem item = new MenuItem();
        item.setId(rs.getInt("item_id"));
        item.setName(rs.getString("item_name"));
        item.setPrice(rs.getDouble("item_price"));
        item.setUnit(rs.getString("item_unit"));
        item.setStatus(rs.getString("item_status"));
        item.setCategoryId(rs.getInt("category_id"));
        return item;
    }
}
