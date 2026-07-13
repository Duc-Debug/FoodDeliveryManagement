package com.delivery.services;

import com.delivery.model.User;
import java.util.List;


public interface IUserService {
void registerUser(User user)throws Exception;
User getUserById(String id);
List<User> getAllUser();
void updateUser(String userId,User user) throws Exception;
void deleteUser(String id) throws Exception;
void depositMoney(String userId, double amount) throws Exception;
} 