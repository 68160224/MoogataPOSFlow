package com.example.moogata.ui;

import java.awt.Font;
import java.util.Enumeration;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class ThaiFont {

    /**
     * ผู้สมัครที่มีอักขระไทย — เลือกตัวแรกที่มีในเครื่องจริง (Leelawadee UI
     * มีใน Windows 8+)
     */
    private static final String[] CANDIDATES = {
        "Leelawadee UI", "Tahoma", "Noto Sans Thai", "Sarabun"
    };

    public static void apply() {
        FontUIResource thai = new FontUIResource(pickThaiFont());
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, thai);
            }
        }
    }

    /**
     * เลือกฟอนต์ตัวแรกที่วาดอักขระไทยได้จริง (ทดสอบด้วย ว U+0E27)
     */
    static Font pickThaiFont() {
        for (String name : CANDIDATES) {
            Font font = new Font(name, Font.PLAIN, 14);
            if (font.canDisplay('\u0E27')) {
                return font;
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    }
}
