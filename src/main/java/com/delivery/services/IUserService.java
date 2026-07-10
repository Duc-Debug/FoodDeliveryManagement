package com.delivery.services;

import com.delivery.model.User;
import com.delivery.model.Merchant;

import java.util.List;

import com.delivery.model.Customer;

public interface IUserService {
void registerCustomer(Customer customer)throws Exception;
void registerMerchant(Merchant merchant)throws Exception;
User getUserById(String id);
List<User> getAllUser();
void updateUser(String userId,User user) throws Exception;
void deleteUser(String id) throws Exception;
void depositMoney(String userId, double amount) throws Exception;
} 