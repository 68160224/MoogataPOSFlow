package com.example.moogata;

import com.example.moogata.dao.DiningTableDao;
import com.example.moogata.helper.DatabaseHelper;
import com.example.moogata.model.Bill;
import com.example.moogata.model.DiningTable;
import com.example.moogata.model.Employee;
import com.example.moogata.service.TableService;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestTableService {

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
            stmt.executeUpdate("UPDATE dining_table SET status = 'ว่าง', headcount = 0 WHERE table_id IN (1, 4, 5)");
            System.out.println("[ตั้งต้น] ล้าง bill_item / bill / ตัวนับ id และคืนโต๊ะ 1, 4, 5 เป็นว่างแล้ว\n");
        } catch (SQLException ex) {
            System.err.println("Reset database failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            resetDatabase();

            TableService tableService = new TableService();
            DiningTableDao tableDao = new DiningTableDao();

            // สมมติพนักงาน ID 1
            Employee emp1 = new Employee(1, "พนักงานทดสอบ", "พนักงาน", "1234", 1);

            DiningTable table4 = tableDao.get(4);
            DiningTable table5 = tableDao.get(5);
            DiningTable table1 = tableDao.get(1);

            // [1] เปิดโต๊ะ 4 / 2 คน สำเร็จ
            Bill bill1 = tableService.openTable(table4, 2, emp1);
            DiningTable t4After = tableDao.get(4);
            check("เปิดโต๊ะ 4 / 2 คน สำเร็จ และโต๊ะเป็น 'ใช้งาน 2 คน'",
                    bill1 != null && bill1.getId() == 1 && "ใช้งาน".equals(t4After.getStatus()) && t4After.getHeadcount() == 2,
                    bill1 != null ? "bill_id=" + bill1.getId() : "ล้มเหลว");

            // [2] เปิดโต๊ะ 4 ซ้ำต้องถูกปฏิเสธ (กฎ SC-4)
            Bill bill1Repeat = tableService.openTable(t4After, 3, emp1);
            check("เปิดโต๊ะ 4 ซ้ำต้องถูกปฏิเสธ (กฎ SC-4)",
                    bill1Repeat == null,
                    bill1Repeat == null ? "openTable คืน null" : "เปิดซ้ำได้");

            // [3] เปิดโต๊ะ 5 / 3 คน สำเร็จ
            Bill bill2 = tableService.openTable(table5, 3, emp1);
            check("เปิดโต๊ะ 5 / 3 คน สำเร็จ",
                    bill2 != null && bill2.getId() == 2,
                    bill2 != null ? "bill_id=" + bill2.getId() : "ล้มเหลว");

            // [4] เปิดโต๊ะ 1 ด้วยลูกค้า 0 คน ต้องถูกปฏิเสธ
            Bill billInvalid = tableService.openTable(table1, 0, emp1);
            check("เปิดโต๊ะ 1 ด้วยลูกค้า 0 คน ต้องถูกปฏิเสธ",
                    billInvalid == null,
                    billInvalid == null ? "openTable คืน null" : "เปิดได้");

            System.out.println("\nสรุป: ผ่าน " + pass + "/" + total + " รายการ");
        } finally {
            DatabaseHelper.close();
        }
    }
}
