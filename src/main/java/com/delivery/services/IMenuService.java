package com.delivery.services;

import java.util.List;

import com.delivery.model.MenuItem;

public interface IMenuService {
void createMenu(MenuItem item) throws Exception;
List<MenuItem> getAllMenuItem();
MenuItem getMenuItemById(String menuId) throws Exception;
void updateMenuItem(String menuId,MenuItem item) throws Exception;
void deleteMenuItem(String menuId) throws Exception;
    
}