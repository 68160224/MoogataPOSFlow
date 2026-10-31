/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.moogata.service;

import com.example.moogata.model.*;
import com.example.moogata.dao.*;
import java.util.*;

/**
 *
 * @author Windows
 */
public class TableService {

    private final DiningTableDao tableDao = new DiningTableDao();
    private final BillDao billDao = new BillDao();

    public Bill openTable(DiningTable table, int headcount, Employee employee) {
        // กฎ 1: ต้องรู้จำนวนลูกค้าก่อนเปิดโต๊ะเสมอ
        if (table == null || employee == null || headcount < 1) {
            return null;
        }
        // กฎ 2 (SC-4): โต๊ะที่ยังใช้งานอยู่ ห้ามเปิดซ้ำ แม้เป็นพนักงานคนอื่นก็ตาม
        if (!table.isVacant()) {
            return null;
        }

        Bill bill = billDao.save(new Bill(table.getId(), employee.getId()));
        if (bill == null) {
            return null;
        }
        tableDao.updateStatus(table.getId(), DiningTable.STATUS_IN_USE, headcount);
        return bill;
    }

    /**
     * เช็คบิลสำเร็จแล้ว — คืนโต๊ะเป็น 'ว่าง' (เรียกจาก BillService.checkout)
     */
    public boolean releaseTable(int tableId) {
        return tableDao.updateStatus(tableId, DiningTable.STATUS_VACANT, 0) != null;
    }
}
