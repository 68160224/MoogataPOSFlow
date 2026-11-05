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
public class MenuService {

    private final MenuItemDao menuItemDao = new MenuItemDao();

    public List<MenuItem> getAvailable() {
        List<MenuItem> available = new ArrayList<>();
        for (MenuItem item : menuItemDao.getAll()) {
            if ("มี".equals(item.getStatus())) {
                available.add(item);
            }
        }
        return available;
    }
}
