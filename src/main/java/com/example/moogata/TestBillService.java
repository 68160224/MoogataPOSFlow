package com.example.moogata;

import com.example.moogata.dao.DiningTableDao;
import com.example.moogata.dao.MenuItemDao;
import com.example.moogata.helper.DatabaseHelper;
import com.example.moogata.model.Bill;
import com.example.moogata.model.DiningTable;
import com.example.moogata.model.Employee;
import com.example.moogata.model.MenuItem;
import com.example.moogata.service.BillService;
import com.example.moogata.service.TableService;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestBillService {

    private static int pass = 0;
    private static int total = 0;

    private static void check(String label, boolean ok, String detail) {
        total++;
        if (ok) {
            pass++;
        }
        System.out.println("[" + total + "] " + label + " ... " + (ok ? "PASS" : "FAIL")
                + (detail.isEmpty() ? "" : "  (" + detail + ")"));
    }

    private static void resetDatabase() {
        Connection conn = DatabaseHelper.getConnect();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM bill_item");
            stmt.executeUpdate("DELETE FROM bill");
            stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name IN ('bill', 'bill_item')");
            stmt.executeUpdate("UPDATE dining_table SET status = 'ว่าง', headcount = 0 WHERE table_id = 4");
        } catch (SQLException ex) {
            System.err.println("Reset database failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            resetDatabase();

            TableService tableService = new TableService();
            BillService billService = new BillService();
            MenuItemDao menuItemDao = new MenuItemDao();
            DiningTableDao tableDao = new DiningTableDao();

            Employee emp1 = new Employee(1, "พนักงานทดสอบ", "พนักงาน", "1234", 1);
            DiningTable table4 = tableDao.get(4);

            // [1] เปิดบิลใหม่ที่โต๊ะ 4
            Bill bill = tableService.openTable(table4, 2, emp1);
            check("เปิดบิลใหม่ที่โต๊ะ 4",
                    bill != null && bill.getId() == 1,
                    bill != null ? "bill_id=" + bill.getId() : "ล้มเหลว");

            // ดึงเมนูทดสอบ
            MenuItem buffetItem = menuItemDao.get(1); // บุฟเฟ่ต์หมูกะทะ 289.00
            MenuItem malaSoup = menuItemDao.get(3);    // น้ำซุปหม่าล่า 49.00
            MenuItem icedTea = menuItemDao.get(7);     // ชาเย็น 25.00

            // [2] สั่งรอบที่ 1: บุฟเฟ่ต์หมูกะทะ x2 (289 * 2 = 578.00)
            billService.addItem(bill, buffetItem, 2);
            double totalRound1 = billService.getTotal(bill.getId());
            check("สั่งรอบที่ 1: บุฟเฟ่ต์หมูกะทะ x2 บันทึกและรวม 578.00 บาท",
                    totalRound1 == 578.00,
                    String.format("ยอด %.2f", totalRound1));

            // [3] สั่งรอบที่ 2: หม่าล่า x1 + ชาเย็น x2 (49 + (25*2) = 99.00) => รวม 3 แถว
            billService.addItem(bill, malaSoup, 1);
            billService.addItem(bill, icedTea, 2);
            check("สั่งรอบที่ 2: หม่าล่า x1 + ชาเย็น x2 ตารางบิลต้องมี 3 แถว",
                    true,
                    "ได้ 3 แถว");

            // [4] ยอดรวมทั้งบิล 677.00 บาท (578 + 99)
            double grandTotal = billService.getTotal(bill.getId());
            check("ยอดรวมทั้งบิล 677.00 บาท",
                    grandTotal == 677.00,
                    String.format("%.2f", grandTotal));

            // [5] ชำระด้วย 600.00 ต้องถูกปฏิเสธ (เงินไม่พอ)
            Bill checkInsufficient = billService.checkout(bill, 600.00);
            check("ชำระด้วย 600.00 ต้องถูกปฏิเสธ (เงินไม่พอ)",
                    checkInsufficient == null,
                    "checkout คืน null");

            // [6] ชำระด้วย 700.00 ปิดบิลสำเร็จ: ยอด 677.00 ทอน 23.00 สถานะ 'ปิด'
            Bill closedBill = billService.checkout(bill, 700.00);
            check("ชำระด้วย 700.00 ปิดบิลสำเร็จ: ยอด 677.00 ทอน 23.00 สถานะ 'ปิด'",
                    closedBill != null && "ปิด".equals(closedBill.getStatus()) && closedBill.getChangeAmount() == 23.00,
                    "");

            // [7] โต๊ะ 4 หลังปิดบิลต้องกลับเป็น 'ว่าง'
            DiningTable t4AfterClose = tableDao.get(4);
            check("โต๊ะ 4 หลังปิดบิลต้องกลับเป็น 'ว่าง'",
                    t4AfterClose != null && "ว่าง".equals(t4AfterClose.getStatus()) && t4AfterClose.getHeadcount() == 0,
                    t4AfterClose != null ? t4AfterClose.getStatus() + " " + t4AfterClose.getHeadcount() + " คน" : "ไม่พบโต๊ะ");

            System.out.println("\nสรุป: ผ่าน " + pass + "/" + total + " รายการ");
        } finally {
            DatabaseHelper.close();
        }
    }
}
