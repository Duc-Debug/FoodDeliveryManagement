package com.delivery.ui;

import java.nio.file.Path;

import com.delivery.model.*;
import com.delivery.repository.*;
import com.delivery.services.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Path storeDPath = Path.of("data","store.csv");
        Path userDataFilePath = Path.of("data", "users.csv");
        Path menuDataFilePath = Path.of("data", "menu.csv");
        Path orderDataFilePath = Path.of("data", "orders.csv");
        com.delivery.config.StoreConfig.init(storeDPath);
        IRepository<User, String> userRepository = new GenericsRepository<>() {
            @Override
            protected String getId(User entity) {
                return entity.getId();
            }
        };
        IRepository<MenuItem, String> menuRepository = new GenericsRepository<>() {
            @Override
            protected String getId(MenuItem entity){
                return entity.getId();
            }
        };
        IRepository<Order, String> orderRepository = new GenericsRepository<>() {
            @Override
            protected String getId(Order entity) {
                return entity.getOrderId();
            }
        };
        
        IUserService userService = new UserServiceImpl(userRepository, userDataFilePath);
        IMenuService menuService = new MenuServiceImpl(menuRepository, orderRepository, menuDataFilePath);
        IOrderService orderService = new OrderServiceImpl(userRepository, menuRepository, orderRepository, orderDataFilePath, userDataFilePath);

        AppConsole appConsole = new AppConsole(orderService, userService, menuService);
        appConsole.start();
    }
}
