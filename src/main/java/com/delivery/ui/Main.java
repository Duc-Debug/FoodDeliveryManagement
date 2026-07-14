package com.delivery.ui;

import java.nio.file.Path;

import com.delivery.model.User;
import com.delivery.repository.GenericsRepository;
import com.delivery.repository.IRepository;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Path userDataFilePath = Path.of("data", "users.csv");
        Path menuDataFilePath = Path.of("data", "menu.csv");
        Path orderDataFilePath = Path.of("data", "orders.csv");
        IRepository<User, String> userRepository = new GenericsRepository<>() {
            @Override
            protected String getId(User entity) {
                return entity.getId();
            }
        };
    }
}
