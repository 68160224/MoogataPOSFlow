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
public class BillService {

    private final BillDao billDao = new BillDao();
    private final BillItemDao billItemDao = new BillItemDao();
    private final TableService tableService = new TableService();

    /**
     * สั่งอาหาร 1 รอบ — snapshot แล้วบันทึกทันที / กฎ: บิลปิดแล้วสั่งไม่ได้
     */
    public BillItem addItem(Bill bill, MenuItem menuItem, int qty) {
        if (bill == null || menuItem == null || !bill.isOpen() || qty < 1) {
            return null;
        }
        return billItemDao.save(new BillItem(bill.getId(), menuItem, qty));
    }

    /**
     * รวมยอดบิล — ตรงกับ SQL: SELECT SUM(qty * unit_price)
     */
    public double getTotal(int billId) {
        double total = 0;
        for (BillItem item : billItemDao.findByBillId(billId)) {
            total += item.getLineTotal();
        }
        return total;
    }

    /**
     * เช็คบิล = ตรวจเงินรับ → ปิดบิล → คืนโต๊ะ (ผ่าน TableService.releaseTable)
     */
    public Bill checkout(Bill bill, double cashReceived) {
        if (bill == null || !bill.isOpen()) {
            return null;
        }
        double total = getTotal(bill.getId());
        if (total <= 0 || cashReceived < total) {
            return null; // ไม่มีรายการ หรือเงินรับไม่พอ
        }
        Bill closed = billDao.close(bill.getId(), total, cashReceived, cashReceived - total);
        if (closed == null) {
            return null;
        }
        tableService.releaseTable(bill.getTableId());
        return closed;
    }
}
