/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.moogata;

import com.example.moogata.dao.BillItemDao;
import com.example.moogata.dao.DiningTableDao;
import com.example.moogata.dao.MenuItemDao;
import com.example.moogata.helper.DatabaseHelper;
import com.example.moogata.model.DiningTable;
import com.example.moogata.model.MenuItem;

/**
 *
 * @author Windows
 */
public class TestPosDao {

    private static int pass = 0;
    private static int total = 0;

    /**
     * ตรวจเงื่อนไข 1 ข้อ — พิมพ์ PASS/FAIL พร้อมเหตุผลทันที
     */
    private static void check(String label, boolean ok, String detail) {
        total++;
        if (ok) {
            pass++;
        }
        System.out.println("[" + total + "] " + label + " ... " + (ok ? "PASS" : "FAIL")
                + (detail.isEmpty() ? "" : "  (" + detail + ")"));
    }

    public static void main(String[] args) {
        try {
            MenuItemDao menuItemDao = new MenuItemDao();
            DiningTableDao tableDao = new DiningTableDao();
            BillItemDao billItemDao = new BillItemDao();

            // --- แสดงข้อมูลจริงให้ดูประกอบ ---
            System.out.println("=== เมนูทั้งหมด ===");
            for (MenuItem item : menuItemDao.getAll()) {
                System.out.printf("%2d. %s %s บาท/%s [%s]%n",
                        item.getId(), item.getName(), item.getPriceText(),
                        item.getUnit(), item.getStatus());
            }
            System.out.println();
            System.out.println("=== โต๊ะทั้งหมด ===");
            for (DiningTable table : tableDao.getAll()) {
                System.out.println("โต๊ะ " + table.getNumber() + " : " + table.getStatus()
                        + " (" + table.getHeadcount() + " คน)");
            }
            System.out.println();
            System.out.println("รายการในบิลทั้งหมดตอนนี้: " + billItemDao.getAll().size() + " แถว");
            System.out.println();
            // --- ตรวจตามค่าที่คาดหวัง ---
            check("เมนูต้องมี 10 รายการ", menuItemDao.getAll().size() == 10,
                    "ได้ " + menuItemDao.getAll().size());
            check("โต๊ะต้องมี 6 ตัว", tableDao.getAll().size() == 6,
                    "ได้ " + tableDao.getAll().size());
            DiningTable table3 = null;
            for (DiningTable table : tableDao.getAll()) {
                if (table.getNumber() == 3) {
                    table3 = table;
                }
            }
            check("โต๊ะ 3 ต้อง 'ใช้งาน' อยู่ 4 คน (seed)",
                    table3 != null && "ใช้งาน".equals(table3.getStatus()) && table3.getHeadcount() == 4,
                    table3 == null ? "ไม่พบโต๊ะ 3" : table3.getStatus() + " " + table3.getHeadcount() + " คน");
            System.out.println();
            System.out.println("สรุป: ผ่าน " + pass + "/" + total + " รายการ"
                    + (pass == total ? "" : "  <-- มี FAIL ให้กลับไปตรวจ DAO/seed"));
        } finally {
            DatabaseHelper.close();
        }
    }
}
